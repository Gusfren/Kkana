package psti.unram.kkana.utils

import android.content.Context

object ScoreManager {

    private fun getPrefs(context: Context, uid: String): android.content.SharedPreferences {
        return context.getSharedPreferences("score_prefs_$uid", Context.MODE_PRIVATE)
    }

    fun getHighScore(context: Context, jenisHuruf: String, level: Int, uid: String): Int {
        val key = "${jenisHuruf}_level_$level"
        val prefs = getPrefs(context, uid)
        return prefs.getInt(key, 0)
    }

    fun setHighScore(context: Context, jenisHuruf: String, level: Int, score: Int, uid: String) {
        val key = "${jenisHuruf}_level_$level"
        val prefs = getPrefs(context, uid)
        val current = prefs.getInt(key, 0)
        if (score > current) {
            prefs.edit().putInt(key, score).apply()
        }
    }

    fun resetScores(context: Context, uid: String) {
        val prefs = getPrefs(context, uid)
        prefs.edit().clear().apply()
    }
}
