package psti.unram.kkana.utils

import android.content.Context
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object ProgressUtil {

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

    fun getProgressGabunganTotal50Persen(context: Context, uid: String): Pair<Int, Int> {
        val huruf = getProgressGabungan(context, uid)
        val kuis = getKuisProgressGabungan(context, uid)
        return Pair(huruf.first + kuis.first, huruf.second + kuis.second)
    }

    fun getPersentaseGabungan(context: Context, uid: String): Int {
        val gabungan = getProgressGabunganTotal50Persen(context, uid)
        return if (gabungan.second > 0) (gabungan.first * 100) / gabungan.second else 0
    }

    fun getLevelLabel(persentase: Int): String {
        return when {
            persentase >= 90 -> "Level: Mahir"
            persentase >= 50 -> "Level: Menengah"
            else -> "Level: Pemula"
        }
    }
}
