package psti.unram.kkana.ui

import android.os.Bundle
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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHuruf)
        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val listHuruf = HurufData.loadHuruf(this, jenisHuruf)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = HurufAdapter(this, listHuruf)
    }
}
