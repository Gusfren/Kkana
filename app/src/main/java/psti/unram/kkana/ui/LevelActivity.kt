package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import psti.unram.kkana.R
import psti.unram.kkana.utils.ScoreManager

class LevelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level)

        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        for (i in 1..10) {
            val btnId = resources.getIdentifier("btnLevel$i", "id", packageName)
            val tvId = resources.getIdentifier("tvScore$i", "id", packageName)
            val ivId = resources.getIdentifier("ivStar$i", "id", packageName)

            val btn = findViewById<Button>(btnId)
            val tv = findViewById<TextView>(tvId)
            val iv = findViewById<ImageView>(ivId)

            val highScore = ScoreManager.getHighScore(this, jenisHuruf, i, uid)
            tv.text = "Skor tertinggi: $highScore"
            iv.setImageResource(getStarDrawable(highScore))

            btn.setOnClickListener {
                val intent = Intent(this, KuisActivity::class.java)
                intent.putExtra("jenisHuruf", jenisHuruf)
                intent.putExtra("level", i)
                intent.putExtra("uid", uid)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun getStarDrawable(score: Int): Int {
        return when {
            score >= 8 -> R.drawable.star_3
            score >= 5 -> R.drawable.star_2
            score >= 1 -> R.drawable.star_1
            else -> R.drawable.star_0
        }
    }
}
