package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import psti.unram.kkana.R

class MenuActivity : AppCompatActivity() {

    private lateinit var btnBelajarHiragana: Button
    private lateinit var btnBelajarKatakana: Button
    private lateinit var btnBelajarKanji: Button
    private lateinit var btnKuisHiragana: Button
    private lateinit var btnKuisKatakana: Button
    private lateinit var btnKuisKanji: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnBelajarHiragana = findViewById(R.id.btnBelajarHiragana)
        btnBelajarKatakana = findViewById(R.id.btnBelajarKatakana)
        btnBelajarKanji = findViewById(R.id.btnBelajarKanji)
        btnKuisHiragana = findViewById(R.id.btnKuisHiragana)
        btnKuisKatakana = findViewById(R.id.btnKuisKatakana)
        btnKuisKanji = findViewById(R.id.btnKuisKanji)

        btnBelajarHiragana.setOnClickListener {
            val intent = Intent(this, BelajarActivity::class.java)
            intent.putExtra("jenisHuruf", "hiragana")
            startActivity(intent)
        }

        btnBelajarKatakana.setOnClickListener {
            val intent = Intent(this, BelajarActivity::class.java)
            intent.putExtra("jenisHuruf", "katakana")
            startActivity(intent)
        }

        btnBelajarKanji.setOnClickListener {
            val intent = Intent(this, BelajarActivity::class.java)
            intent.putExtra("jenisHuruf", "kanji")
            startActivity(intent)
        }

        btnKuisHiragana.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            intent.putExtra("jenisHuruf", "hiragana")
            startActivity(intent)
        }

        btnKuisKatakana.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            intent.putExtra("jenisHuruf", "katakana")
            startActivity(intent)
        }

        btnKuisKanji.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            intent.putExtra("jenisHuruf", "kanji")
            startActivity(intent)
        }
    }
}