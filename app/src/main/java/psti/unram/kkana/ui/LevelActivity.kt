// app/src/main/java/psti/unram/kkana/ui/LevelActivity.kt
package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View // Import View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import psti.unram.kkana.R
import psti.unram.kkana.utils.ScoreManager
import android.widget.ImageButton

class LevelActivity : AppCompatActivity() {

    private val TAG = "LevelActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level)

        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "Pengguna tidak terautentikasi.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val btnBackLevel = findViewById<ImageButton>(R.id.btnBackLevel)
        btnBackLevel?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        for (i in 1..10) {
            // Dapatkan ID dari tag <include>
            val includeId = resources.getIdentifier("level${i}Include", "id", packageName)

            if (includeId == 0) {
                Log.e(TAG, "Include ID for Level $i not found: level${i}Include")
                continue
            }

            // Temukan View yang di-include
            val levelView = findViewById<View>(includeId)

            if (levelView == null) {
                Log.e(TAG, "Level View for Level $i is null (ID: $includeId)")
                continue
            }

            // Temukan elemen di dalam View yang di-include
            val tvLevelNumber = levelView.findViewById<TextView>(R.id.tvLevelNumber)
            val btn = levelView.findViewById<Button>(R.id.btnStartQuiz) // Menggunakan ID baru
            val tv = levelView.findViewById<TextView>(R.id.tvScore)     // Menggunakan ID baru
            val iv = levelView.findViewById<ImageView>(R.id.ivStar)     // Menggunakan ID baru

            // Lakukan null check untuk elemen di dalam include
            if (tvLevelNumber == null || btn == null || tv == null || iv == null) {
                Log.e(TAG, "Some UI elements inside Level $i's include are null. " +
                        "tvLevelNumber=${tvLevelNumber == null}, btn=${btn == null}, tv=${tv == null}, iv=${iv == null}")
                continue
            }

            // Set teks nomor level
            tvLevelNumber.text = "Level $i"

            val highScore = ScoreManager.getHighScore(this, jenisHuruf, i, uid)
            tv.text = "Skor tertinggi: $highScore"
            iv.setImageResource(getStarDrawable(highScore))

            val currentLevel = i // Simpan i dalam variabel lokal untuk OnClickListener
            btn.setOnClickListener {
                val intent = Intent(this, KuisActivity::class.java)
                intent.putExtra("jenisHuruf", jenisHuruf)
                intent.putExtra("level", currentLevel) // Gunakan currentLevel
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