package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import psti.unram.kkana.R
import androidx.appcompat.app.AlertDialog

class MenuActivity : AppCompatActivity() {

    private lateinit var btnHiragana: Button
    private lateinit var btnKatakana: Button
    private lateinit var btnKanji: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnHiragana = findViewById(R.id.btnHiragana)
        btnKatakana = findViewById(R.id.btnKatakana)
        btnKanji = findViewById(R.id.btnKanji)

        btnHiragana.setOnClickListener {
            tampilkanPilihan("hiragana")
        }

        btnKatakana.setOnClickListener {
            tampilkanPilihan("katakana")
        }

        btnKanji.setOnClickListener {
            tampilkanPilihan("kanji")
        }
    }

    private fun tampilkanPilihan(jenisHuruf: String) {
        val namaHuruf = when (jenisHuruf) {
            "hiragana" -> "Hiragana"
            "katakana" -> "Katakana"
            "kanji" -> "Kanji"
            else -> "Huruf"
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_pilihan, null)
        val btnBelajar = dialogView.findViewById<Button>(R.id.btnBelajar)
        val btnKuis = dialogView.findViewById<Button>(R.id.btnKuis)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Pilih Mode $namaHuruf")
            .setView(dialogView)
            .create()

        btnBelajar.text = "📖 Belajar $namaHuruf"
        btnKuis.text = "📝 Kuis $namaHuruf"

        btnBelajar.setOnClickListener {
            val intent = Intent(this, BelajarActivity::class.java)
            intent.putExtra("jenisHuruf", jenisHuruf)
            startActivity(intent)
            alertDialog.dismiss()
        }

        btnKuis.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            intent.putExtra("jenisHuruf", jenisHuruf)
            startActivity(intent)
            alertDialog.dismiss()
        }

        alertDialog.show()

    }

}