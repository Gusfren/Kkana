package psti.unram.kkana.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HurufData {
    fun loadHuruf(context: Context, jenis: String): List<Huruf> {
        val filename = when (jenis) {
            "hiragana" -> "hiragana.json"
            "katakana" -> "katakana.json"
            "kanji" -> "kanji.json"
            else -> "hiragana.json"
        }

        val json = context.assets.open(filename).bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Huruf>>() {}.type
        return Gson().fromJson(json, listType)
    }
}