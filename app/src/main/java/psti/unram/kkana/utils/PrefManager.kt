package psti.unram.kkana.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

    companion object {
        private const val PREF_NAME = "user_pref"
        private const val KEY_UID = "uid"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(uid: String, email: String, name: String) {
        prefs.edit()
            .putString(KEY_UID, uid)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    fun saveProgress(uid: String, jenisHuruf: String, level: Int) {
        val editor = prefs.edit()
        editor.putInt("${uid}_progress_$jenisHuruf", level)
        editor.apply()
    }

    fun getProgress(uid: String, jenisHuruf: String): Int {
        return prefs.getInt("${uid}_progress_$jenisHuruf", 0)
    }

    fun getUid(): String? = prefs.getString(KEY_UID, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getName(): String? = prefs.getString(KEY_NAME, null)

    fun isLoggedIn(): Boolean = getUid() != null

    fun logout() {
        prefs.edit().clear().apply()
    }
}
