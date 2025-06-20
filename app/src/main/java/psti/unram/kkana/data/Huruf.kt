package psti.unram.kkana.data

data class Huruf(
    val huruf: String,
    val romaji: String,
    val suara: String,
    val level: Int = 1,
    val terjemahan: String? = null
)