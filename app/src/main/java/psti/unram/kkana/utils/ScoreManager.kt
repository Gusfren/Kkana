package psti.unram.kkana.utils

import android.content.Context
import android.content.SharedPreferences

object ScoreManager {
    private const val PREF_NAME = "score_pref"

    fun getHighScore(context: Context, jenisHuruf: String, level: Int): Int {
        val key = "${jenisHuruf}_level_$level"
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }

    fun setHighScore(context: Context, jenisHuruf: String, level: Int, score: Int) {
        val key = "${jenisHuruf}_level_$level"
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(key, 0)
        if (score > current) {
            prefs.edit().putInt(key, score).apply()
        }
    }
}
