package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        val level = intent.getIntExtra("level", 1)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Set skor hanya jika UID valid
        if (uid.isNotEmpty()) {
            ScoreManager.setHighScore(this, jenisHuruf, level, skor, uid)
        }

        val tvHasil = findViewById<TextView>(R.id.tvHasilSkor)
        val btnKembali = findViewById<Button>(R.id.btnKembaliMenu)

        tvHasil.text = "Skor kamu: $skor dari $total"

        btnKembali.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("jenisHuruf", jenisHuruf) // Biar kembali tetap sesuai jenis huruf
            startActivity(intent)
            finish()
        }
    }
}
