package psti.unram.kkana.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import psti.unram.kkana.R
import psti.unram.kkana.data.HurufAdapter
import psti.unram.kkana.data.HurufData

class BelajarActivity : AppCompatActivity() {

    private lateinit var rvHuruf: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_belajar)

        rvHuruf = findViewById(R.id.rvHuruf)
        rvHuruf.layoutManager = LinearLayoutManager(this)

        val jenisHuruf = intent.getStringExtra("jenisHuruf") ?: "hiragana"
        val listHuruf = HurufData.loadHuruf(this, jenisHuruf)

        val adapter = HurufAdapter(this, listHuruf)
        rvHuruf.adapter = adapter
    }
}
