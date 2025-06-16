package psti.unram.kkana.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import psti.unram.kkana.R
import com.google.firebase.auth.FirebaseAuth
import psti.unram.kkana.utils.ProgressUtil
import psti.unram.kkana.auth.LoginActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var btnHiragana: Button
    private lateinit var btnKatakana: Button
    private lateinit var btnKanji: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLevel: TextView
    private lateinit var tvKata: TextView

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        uid = user.uid

        btnHiragana = findViewById(R.id.btnHiragana)
        btnKatakana = findViewById(R.id.btnKatakana)
        btnKanji = findViewById(R.id.btnKanji)

        val logoutBtn = findViewById<Button>(R.id.logoutButton)
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        progressBar = findViewById(R.id.progressBar)
        tvLevel = findViewById(R.id.tvLevel)
        tvKata = findViewById(R.id.tvKata)

        updateProgressGabungan()

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

    private fun updateProgressGabungan() {
        val (jumlahKata, totalKata) = ProgressUtil.getProgressGabungan(this, uid)
        val (jumlahKuis, totalKuis) = ProgressUtil.getKuisProgressGabungan(this, uid)

        val persenKata = if (totalKata > 0) (jumlahKata * 100 / totalKata) else 0
        val persenKuis = if (totalKuis > 0) (jumlahKuis * 100 / totalKuis) else 0
        val rataRata = (persenKata + persenKuis) / 2

        progressBar.progress = rataRata
        tvLevel.text = getLevelText(rataRata)
        tvKata.text = "$jumlahKata/$totalKata kata • $jumlahKuis/$totalKuis kuis"
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
        val progressBarDialog = dialogView.findViewById<ProgressBar>(R.id.dialogProgressBar)
        val progressText = dialogView.findViewById<TextView>(R.id.dialogProgressText)
        val levelText = dialogView.findViewById<TextView>(R.id.dialogLevelText) // <- ini baris tambahan

        val jumlahKata = ProgressUtil.getJumlahDipelajari(this, jenisHuruf, uid)
        val totalKata = ProgressUtil.getTotalHuruf(this, jenisHuruf)
        val jumlahKuis = ProgressUtil.getJumlahLevelKuisSelesai(this, jenisHuruf, uid)
        val totalKuis = 10

        val persenKata = if (totalKata > 0) (jumlahKata * 100 / totalKata) else 0
        val persenKuis = if (totalKuis > 0) (jumlahKuis * 100 / totalKuis) else 0
        val rataRata = (persenKata + persenKuis) / 2

        progressBarDialog.progress = rataRata
        progressText.text = "Progress: $jumlahKata/$totalKata kata • $jumlahKuis/$totalKuis kuis"
        levelText.text = getLevelText(rataRata) // <- ini baris tambahan

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

    private fun getLevelText(persen: Int): String {
        return when {
            persen < 25 -> "Level: Pemula"
            persen < 50 -> "Level: Belajar Terus!"
            persen < 75 -> "Level: Sudah Jago!"
            else -> "Level: Kana Master"
        }
    }
}
