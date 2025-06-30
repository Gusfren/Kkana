package psti.unram.kkana.utils

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import psti.unram.kkana.utils.ProgressUtil
import kotlin.random.Random

class DailyChallengeManager private constructor(private val context: Context, private val uid: String) {

    companion object {
        @Volatile
        private var INSTANCE: DailyChallengeManager? = null

        fun getInstance(context: Context, uid: String): DailyChallengeManager {
            if (INSTANCE == null || INSTANCE?.uid != uid) {
                synchronized(this) {
                    if (INSTANCE == null || INSTANCE?.uid != uid) {
                        INSTANCE = DailyChallengeManager(context, uid)
                    }
                }
            }
            INSTANCE?.initDailyChallenge()
            return INSTANCE!!
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }

    private val listeners = mutableListOf<() -> Unit>()

    private val PREFS_NAME = "daily_challenge_prefs_$uid"
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val KEY_CHALLENGE_DATE = "challenge_date"
    private val KEY_CHALLENGE_TYPE = "challenge_type"
    private val KEY_CHALLENGE_TARGET = "challenge_target"
    private val KEY_CURRENT_PROGRESS = "current_progress"
    private val KEY_IS_COMPLETED = "is_completed"
    private val KEY_LEARNED_KANJI_SET = "learned_kanji_set"
    private val KEY_REWARD_POINTS = "reward_points"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var currentChallengeProgress: Int = 0
        private set(value) {
            field = value
            prefs.edit().putInt(KEY_CURRENT_PROGRESS, value).apply()
            notifyListeners()
        }

    var isChallengeCompleted: Boolean = false
        private set(value) {
            field = value
            prefs.edit().putBoolean(KEY_IS_COMPLETED, value).apply()
            notifyListeners()
        }

    var challengeTarget: Int = 0
        private set(value) {
            field = value
            prefs.edit().putInt(KEY_CHALLENGE_TARGET, value).apply()
        }

    var challengeType: String = ""
        private set(value) {
            field = value
            prefs.edit().putString(KEY_CHALLENGE_TYPE, value).apply()
        }

    var rewardPoints: Int = 0
        private set(value) {
            field = value
            prefs.edit().putInt(KEY_REWARD_POINTS, value).apply()
        }

    private var dailyLearnedKanjiSet: MutableSet<String> = mutableSetOf()
    private var dailyLearnedHiraganaSet: MutableSet<String> = mutableSetOf()
    private var dailyLearnedKatakanaSet: MutableSet<String> = mutableSetOf()

    private val challengeVariations = listOf(
        Challenge(type = "learn_kanji", target = 10, reward = 50),
        Challenge(type = "learn_hiragana", target = 15, reward = 30),
        Challenge(type = "learn_katakana", target = 15, reward = 30),
        Challenge(type = "complete_quiz", target = 1, reward = 40)
    )

    init {
        loadChallengeState()
    }

    private fun loadChallengeState() {
        currentChallengeProgress = prefs.getInt(KEY_CURRENT_PROGRESS, 0)
        isChallengeCompleted = prefs.getBoolean(KEY_IS_COMPLETED, false)
        challengeTarget = prefs.getInt(KEY_CHALLENGE_TARGET, 0)
        challengeType = prefs.getString(KEY_CHALLENGE_TYPE, "") ?: ""
        rewardPoints = prefs.getInt(KEY_REWARD_POINTS, 0)

        val savedKanjiSet = prefs.getStringSet(KEY_LEARNED_KANJI_SET, emptySet())
        dailyLearnedKanjiSet = savedKanjiSet?.toMutableSet() ?: mutableSetOf()

        val savedHiraganaSet = prefs.getStringSet("learned_hiragana_set", emptySet())
        dailyLearnedHiraganaSet = savedHiraganaSet?.toMutableSet() ?: mutableSetOf()

        val savedKatakanaSet = prefs.getStringSet("learned_katakana_set", emptySet())
        dailyLearnedKatakanaSet = savedKatakanaSet?.toMutableSet() ?: mutableSetOf()
    }

    fun initDailyChallenge() {
        val today = dateFormat.format(Date())
        val savedChallengeDate = prefs.getString(KEY_CHALLENGE_DATE, "")

        if (savedChallengeDate != today) {
            val newChallenge = challengeVariations[Random.nextInt(challengeVariations.size)]

            challengeType = newChallenge.type
            challengeTarget = newChallenge.target
            rewardPoints = newChallenge.reward

            currentChallengeProgress = 0
            isChallengeCompleted = false
            dailyLearnedKanjiSet.clear()
            dailyLearnedHiraganaSet.clear()
            dailyLearnedKatakanaSet.clear()

            prefs.edit()
                .putString(KEY_CHALLENGE_DATE, today)
                .putString(KEY_CHALLENGE_TYPE, challengeType)
                .putInt(KEY_CHALLENGE_TARGET, challengeTarget)
                .putInt(KEY_REWARD_POINTS, rewardPoints)
                .putInt(KEY_CURRENT_PROGRESS, currentChallengeProgress)
                .putBoolean(KEY_IS_COMPLETED, isChallengeCompleted)
                .putStringSet(KEY_LEARNED_KANJI_SET, dailyLearnedKanjiSet)
                .putStringSet("learned_hiragana_set", dailyLearnedHiraganaSet)
                .putStringSet("learned_katakana_set", dailyLearnedKatakanaSet)
                .apply()
        } else {
            loadChallengeState()
        }
    }

    fun trackLearningProgress(jenisHuruf: String, hurufChar: String) {
        if (isChallengeCompleted) return

        when (challengeType) {
            "learn_kanji" -> {
                if (jenisHuruf == "kanji" && !dailyLearnedKanjiSet.contains(hurufChar)) {
                    dailyLearnedKanjiSet.add(hurufChar)
                    currentChallengeProgress = dailyLearnedKanjiSet.size
                    prefs.edit().putStringSet(KEY_LEARNED_KANJI_SET, dailyLearnedKanjiSet).apply() // Pastikan disimpan
                }
            }
            "learn_hiragana" -> {
                if (jenisHuruf == "hiragana" && !dailyLearnedHiraganaSet.contains(hurufChar)) {
                    dailyLearnedHiraganaSet.add(hurufChar)
                    currentChallengeProgress = dailyLearnedHiraganaSet.size
                    prefs.edit().putStringSet("learned_hiragana_set", dailyLearnedHiraganaSet).apply() // Pastikan disimpan
                }
            }
            "learn_katakana" -> {
                if (jenisHuruf == "katakana" && !dailyLearnedKatakanaSet.contains(hurufChar)) {
                    dailyLearnedKatakanaSet.add(hurufChar)
                    currentChallengeProgress = dailyLearnedKatakanaSet.size
                    prefs.edit().putStringSet("learned_katakana_set", dailyLearnedKatakanaSet).apply() // Pastikan disimpan
                }
            }
        }

        prefs.edit().putInt(KEY_CURRENT_PROGRESS, currentChallengeProgress).apply()

        if (currentChallengeProgress >= challengeTarget) {
            completeChallenge()
        }
    }

    fun trackQuizCompletion() {
        if (isChallengeCompleted) return

        if (challengeType == "complete_quiz") {
            currentChallengeProgress++
            prefs.edit().putInt(KEY_CURRENT_PROGRESS, currentChallengeProgress).apply()

            if (currentChallengeProgress >= challengeTarget) {
                completeChallenge()
            }
        }
    }

    private fun completeChallenge() {
        isChallengeCompleted = true
        prefs.edit().putBoolean(KEY_IS_COMPLETED, true).apply()
        ProgressUtil.incrementDailyChallengeBonusCount(context, uid)
    }

    fun resetDailyChallengeState() {
        prefs.edit().apply {
            remove(KEY_CHALLENGE_DATE)
            remove(KEY_CHALLENGE_TYPE)
            remove(KEY_CHALLENGE_TARGET)
            remove(KEY_CURRENT_PROGRESS)
            remove(KEY_IS_COMPLETED)
            remove(KEY_LEARNED_KANJI_SET)
            remove("learned_hiragana_set")
            remove("learned_katakana_set")
            remove(KEY_REWARD_POINTS)
            apply()
        }
        loadChallengeState()
        initDailyChallenge()
    }

    fun getChallengeTitle(): String {
        return if (isChallengeCompleted) "Tantangan Selesai!" else "今日の挑戦"
    }

    fun getChallengeDescription(): String {
        return when (challengeType) {
            "learn_kanji" -> {
                if (isChallengeCompleted) "Anda telah menyelesaikan tantangan!"
                else "Belajar $challengeTarget Kanji baru hari ini"
            }
            "learn_hiragana" -> {
                if (isChallengeCompleted) "Anda telah menyelesaikan tantangan!"
                else "Belajar $challengeTarget Hiragana baru hari ini"
            }
            "learn_katakana" -> {
                if (isChallengeCompleted) "Anda telah menyelesaikan tantangan!"
                else "Belajar $challengeTarget Katakana baru hari ini"
            }
            "complete_quiz" -> {
                if (isChallengeCompleted) "Anda telah menyelesaikan tantangan!"
                else "Selesaikan $challengeTarget Kuis hari ini"
            }
            else -> "Tidak ada tantangan hari ini"
        }
    }

    fun addOnChangeListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeOnChangeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.invoke() }
    }

    data class Challenge(
        val type: String,
        val target: Int,
        val reward: Int
    )
}