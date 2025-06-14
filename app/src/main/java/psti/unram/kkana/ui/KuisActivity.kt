package psti.unram.kkana.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import psti.unram.kkana.R
import psti.unram.kkana.data.Huruf
import psti.unram.kkana.data.HurufData
import kotlin.random.Random
import android.media.MediaPlayer
import android.content.Intent
import psti.unram.kkana.utils.ScoreManager
import psti.unram.kkana.utils.ProgressUtil

class KuisActivity : AppCompatActivity() {

    private lateinit var tvHuruf: TextView
    private lateinit var etJawaban: EditText
    private lateinit var btnCek: Button
    private lateinit var tvSkor: TextView
    private var totalSoal = 0
    private val batasSoal = 10

    private lateinit var listHuruf: List<Huruf>
    private lateinit var hurufSekarang: Huruf
    private var skor = 0

    private lateinit var jenisHuruf: String
    private var level: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuis)

        tvHuruf = findViewById(R.id.tvSoalHuruf)
        etJawaban = findViewById(R.id.etJawaban)
        btnCek = findViewById(R.id.btnCekJawaban)
        tvSkor = findViewById(R.id.tvSkor)

        jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        level = intent.getIntExtra("level", 1)

        listHuruf = HurufData.loadHuruf(this, jenisHuruf).filter { it.level <= level }

        tampilkanSoalBaru()

        btnCek.setOnClickListener {
            val jawaban = etJawaban.text.toString().trim().lowercase()

            if (jawaban == hurufSekarang.romaji.lowercase()) {
                skor++
                Toast.makeText(this, "Benar!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Salah! Jawaban: ${hurufSekarang.romaji}", Toast.LENGTH_SHORT).show()
            }

            val resId = resources.getIdentifier(hurufSekarang.suara, "raw", packageName)
            if (resId != 0) {
                val mediaPlayer = MediaPlayer.create(this, resId)
                mediaPlayer.setOnCompletionListener {
                    it.release()
                    totalSoal++
                    tampilkanSoalBaru()
                }
                mediaPlayer.start()
            } else {
                totalSoal++
                tampilkanSoalBaru()
            }

            tvSkor.text = "Skor: $skor"
            etJawaban.setText("")
        }
    }

    private fun tampilkanSoalBaru() {
        if (totalSoal >= batasSoal) {
            ScoreManager.setHighScore(this, jenisHuruf, level, skor)
            psti.unram.kkana.utils.ProgressUtil.tandaiLevelKuisSelesai(this, jenisHuruf, level)

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("skor", skor)
            intent.putExtra("total", batasSoal)
            intent.putExtra("jenisHuruf", jenisHuruf)
            intent.putExtra("level", level)
            startActivity(intent)
            finish()
            return
        }

        val index = Random.nextInt(listHuruf.size)
        hurufSekarang = listHuruf[index]
        tvHuruf.text = hurufSekarang.huruf
    }
}
