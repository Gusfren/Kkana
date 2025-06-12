package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import psti.unram.kkana.R
import psti.unram.kkana.utils.ScoreManager


class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val skor = intent.getIntExtra("skor", 0)
        val total = intent.getIntExtra("total", 10)

        val tvHasil = findViewById<TextView>(R.id.tvHasilSkor)
        val btnKembali = findViewById<Button>(R.id.btnKembaliMenu)
        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val level = intent.getIntExtra("level", 1)

        ScoreManager.setHighScore(this, jenisHuruf, level, skor)

        tvHasil.text = "Skor kamu: $skor dari $total"

        btnKembali.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
