package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView // Pastikan ini terimport
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import psti.unram.kkana.R
import psti.unram.kkana.utils.ScoreManager

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val skor = intent.getIntExtra("skor", 0)
        val total = intent.getIntExtra("total", 10)
        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val level = intent.getIntExtra("level", 1) // Ambil level dari Intent
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Set skor hanya jika UID valid
        if (uid.isNotEmpty()) {
            ScoreManager.setHighScore(this, jenisHuruf, level, skor, uid)
        }

        val tvSkorAngka = findViewById<TextView>(R.id.tvSkorAngka)
        val ivResultStars = findViewById<ImageView>(R.id.ivResultStars) // Inisialisasi ImageView baru
        val tvFeedbackMessage = findViewById<TextView>(R.id.tvFeedbackMessage)
        val btnKembali = findViewById<Button>(R.id.btnKembaliMenu)

        tvSkorAngka.text = "$skor/$total"

        // Menampilkan bintang sesuai logika LevelActivity
        ivResultStars.setImageResource(getStarDrawable(skor))

        // Menentukan pesan umpan balik
        tvFeedbackMessage.text = getFeedbackMessage(skor, total)

        btnKembali.setOnClickListener {
            // Kembali ke LevelActivity, mengirim jenisHuruf agar tidak perlu memilih ulang
            val intent = Intent(this, LevelActivity::class.java)
            intent.putExtra("jenisHuruf", jenisHuruf)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    // Fungsi ini diambil dan disesuaikan dari LevelActivity untuk konsistensi
    private fun getStarDrawable(score: Int): Int {
        // Sesuaikan ambang batas skor ini dengan total soal kuis Anda
        // Misalnya, jika total soal 10:
        // 8-10 benar -> star_3
        // 5-7 benar  -> star_2
        // 1-4 benar  -> star_1
        // 0 benar    -> star_0
        return when {
            score >= 8 -> R.drawable.star_3
            score >= 5 -> R.drawable.star_2
            score >= 1 -> R.drawable.star_1
            else -> R.drawable.star_0
        }
    }

    // Fungsi untuk mendapatkan pesan umpan balik
    private fun getFeedbackMessage(score: Int, total: Int): String {
        return when (score) {
            total -> "Luar biasa! Skor sempurna! Anda adalah Master Kana!"
            in (total * 0.8).toInt() until total -> "Hebat! Anda sangat mahir!"
            in (total * 0.5).toInt() until (total * 0.8).toInt() -> "Bagus sekali! Terus berlatih!"
            in 1 until (total * 0.5).toInt() -> "Jangan menyerah! Sedikit lagi lebih baik!"
            else -> "Terus berlatih ya! Semangat!"
        }
    }
}