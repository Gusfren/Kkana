package psti.unram.kkana.ui

import android.os.Bundle
import android.widget.ImageButton // Import ImageButton
import android.widget.TextView // Import TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import psti.unram.kkana.R
import psti.unram.kkana.data.HurufAdapter
import psti.unram.kkana.data.HurufData

class BelajarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_belajar)

        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val listHuruf = HurufData.loadHuruf(this, jenisHuruf)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHuruf)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = HurufAdapter(this, listHuruf, jenisHuruf)

        // Inisialisasi elemen header
        val btnBackBelajar = findViewById<ImageButton>(R.id.btnBackBelajar)
        val tvBelajarTitle = findViewById<TextView>(R.id.tvBelajarTitle)

        // Set judul berdasarkan jenis huruf (Hiragana, Katakana, Kanji)
        tvBelajarTitle.text = "Belajar ${jenisHuruf.replaceFirstChar { it.uppercaseChar() }}"

        // Set listener untuk tombol kembali
        btnBackBelajar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Navigasi kembali ke activity sebelumnya
        }
    }
}