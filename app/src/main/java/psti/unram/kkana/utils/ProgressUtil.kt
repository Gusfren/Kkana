package psti.unram.kkana.utils

import android.content.Context
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object ProgressUtil {

    private const val KEY_DAILY_CHALLENGE_BONUS_COUNT = "daily_challenge_bonus_count"
    private const val MAX_DAILY_CHALLENGE_BONUS_COUNT = 30 // Maksimal 30 tantangan harian yang dihitung sebagai bonus

    private fun getPrefsName(uid: String): String {
        return "progress_prefs_$uid"
    }

    fun getTotalHuruf(context: Context, jenisHuruf: String): Int {
        return try {
            val fileName = "$jenisHuruf.json"
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            val jsonArray = JSONArray(jsonString)
            jsonArray.length()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getJumlahDipelajari(context: Context, jenisHuruf: String, uid: String): Int {
        return getSetHurufDipelajari(context, jenisHuruf, uid).size
    }

    fun getSetHurufDipelajari(context: Context, jenisHuruf: String, uid: String): Set<String> {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        return prefs.getStringSet("set_$jenisHuruf", emptySet()) ?: emptySet()
    }

    fun tandaiHurufDipelajari(context: Context, jenisHuruf: String, romaji: String, uid: String) {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        val keySet = "set_$jenisHuruf"
        val set = prefs.getStringSet(keySet, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (!set.contains(romaji)) {
            set.add(romaji)
            prefs.edit().putStringSet(keySet, set).apply()
        }
    }

    fun getJumlahLevelKuisSelesai(context: Context, jenisHuruf: String, uid: String): Int {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        var count = 0
        for (i in 1..10) {
            if (prefs.getBoolean("kuis_${jenisHuruf}_level_$i", false)) {
                count++
            }
        }
        return count
    }

    fun tandaiLevelKuisSelesai(context: Context, jenisHuruf: String, level: Int, uid: String) {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        prefs.edit().putBoolean("kuis_${jenisHuruf}_level_$level", true).apply()
    }

    fun resetProgress(context: Context, jenisHuruf: String, uid: String) {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Reset huruf dipelajari
        editor.remove("set_$jenisHuruf")

        // Reset kuis level selesai
        for (i in 1..10) {
            editor.remove("kuis_${jenisHuruf}_level_$i")
        }

        editor.apply()
    }

    // Fungsi baru untuk menambahkan 1 ke hitungan bonus tantangan harian
    fun incrementDailyChallengeBonusCount(context: Context, uid: String) {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        var currentCount = prefs.getInt(KEY_DAILY_CHALLENGE_BONUS_COUNT, 0)
        if (currentCount < MAX_DAILY_CHALLENGE_BONUS_COUNT) { // Batasi hingga MAX_DAILY_CHALLENGE_BONUS_COUNT
            currentCount++
            prefs.edit().putInt(KEY_DAILY_CHALLENGE_BONUS_COUNT, currentCount).apply()
        }
    }

    // Fungsi baru untuk mendapatkan hitungan bonus tantangan harian
    fun getDailyChallengeBonusCount(context: Context, uid: String): Int {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DAILY_CHALLENGE_BONUS_COUNT, 0)
    }

    // Fungsi baru untuk mereset bonus tantangan harian (misal di profil user)
    fun resetDailyChallengeBonusCount(context: Context, uid: String) {
        val prefs = context.getSharedPreferences(getPrefsName(uid), Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_DAILY_CHALLENGE_BONUS_COUNT).apply()
    }

    fun getProgressGabungan(context: Context, uid: String): Pair<Int, Int> {
        val jenis = listOf("hiragana", "katakana", "kanji")
        var totalDipelajari = 0
        var totalHuruf = 0
        for (j in jenis) {
            totalDipelajari += getJumlahDipelajari(context, j, uid)
            totalHuruf += getTotalHuruf(context, j)
        }

        return Pair(totalDipelajari, totalHuruf)
    }

    fun getKuisProgressGabungan(context: Context, uid: String): Pair<Int, Int> {
        val jenis = listOf("hiragana", "katakana", "kanji")
        var kuisSelesai = 0
        var totalKuis = 0
        for (j in jenis) {
            kuisSelesai += getJumlahLevelKuisSelesai(context, j, uid)
            totalKuis += 10
        }
        return Pair(kuisSelesai, totalKuis)
    }

    // Mengintegrasikan bonus tantangan harian ke dalam perhitungan persentase gabungan
    fun getPersentaseGabungan(context: Context, uid: String): Int {
        val (learnedItems, totalItems) = getProgressGabungan(context, uid)
        val (completedQuizzes, totalQuizzes) = getKuisProgressGabungan(context, uid)
        val completedDailyChallenges = getDailyChallengeBonusCount(context, uid)

        // Numerator total: items learned + quizzes completed + daily challenges completed (sebagai bonus unit)
        val totalNumerator = learnedItems + completedQuizzes + completedDailyChallenges

        // Denominator total: total possible items + total possible quizzes + max possible daily challenge bonus units
        val totalDenominator = totalItems + totalQuizzes + MAX_DAILY_CHALLENGE_BONUS_COUNT

        return if (totalDenominator > 0) (totalNumerator * 100) / totalDenominator else 0
    }

    fun getLevelLabel(persentase: Int): String {
        return when {
            persentase >= 90 -> "Level: Mahir"
            persentase >= 50 -> "Level: Menengah"
            else -> "Level: Pemula"
        }
    }
}