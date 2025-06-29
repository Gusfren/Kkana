package psti.unram.kkana.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import psti.unram.kkana.R
import psti.unram.kkana.auth.LoginActivity
import psti.unram.kkana.utils.ProgressUtil
import java.io.File
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MenuActivity : AppCompatActivity() {

    private lateinit var btnHiragana: LinearLayout
    private lateinit var btnKatakana: LinearLayout
    private lateinit var btnKanji: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLevel: TextView
    private lateinit var tvKata: TextView
    private lateinit var profileIcon: ImageView
    private lateinit var tvUsername: TextView

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

        profileIcon = findViewById(R.id.profileIcon)
        tvUsername = findViewById(R.id.tvUsername)

        profileIcon.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        btnHiragana = findViewById(R.id.btnHiragana)
        btnKatakana = findViewById(R.id.btnKatakana)
        btnKanji = findViewById(R.id.btnKanji)

        progressBar = findViewById(R.id.progressBar)
        tvLevel = findViewById(R.id.tvLevel)
        tvKata = findViewById(R.id.tvKata)

        updateProgressGabungan()
        loadProfileData()

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

    override fun onResume() {
        super.onResume()
        updateProgressGabungan()
        loadProfileData() // refresh username dan foto profil saat kembali dari ProfilActivity
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

    private fun loadProfileData() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: "User"
                    tvUsername.text = "こんにちは, $username!"
                } else {
                    tvUsername.text = "こんにちは, User!"
                }
            }
            .addOnFailureListener {
                tvUsername.text = "こんにちは, User!"
            }

        val sharedPref = getSharedPreferences("profil_pref", MODE_PRIVATE)
        val photoPath = sharedPref.getString("photo_path", null)
        if (photoPath != null && File(photoPath).exists()) {
            Glide.with(this)
                .load(File(photoPath))
                .circleCrop()
                .placeholder(R.drawable.default_profile)
                .into(profileIcon)
        } else {
            Glide.with(this)
                .load(R.drawable.default_profile)
                .circleCrop()
                .into(profileIcon)
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
        val progressBarDialog = dialogView.findViewById<ProgressBar>(R.id.dialogProgressBar)
        val progressText = dialogView.findViewById<TextView>(R.id.dialogProgressText)
        val levelText = dialogView.findViewById<TextView>(R.id.dialogLevelText)

        val jumlahKata = ProgressUtil.getJumlahDipelajari(this, jenisHuruf, uid)
        val totalKata = ProgressUtil.getTotalHuruf(this, jenisHuruf)
        val jumlahKuis = ProgressUtil.getJumlahLevelKuisSelesai(this, jenisHuruf, uid)
        val totalKuis = 10

        val persenKata = if (totalKata > 0) (jumlahKata * 100 / totalKata) else 0
        val persenKuis = if (totalKuis > 0) (jumlahKuis * 100 / totalKuis) else 0
        val rataRata = (persenKata + persenKuis) / 2

        progressBarDialog.progress = rataRata
        progressText.text = "Progress: $jumlahKata/$totalKata kata • $jumlahKuis/$totalKuis kuis"
        levelText.text = getLevelText(rataRata)

        val alertDialog = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialogTheme)
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