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
import psti.unram.kkana.utils.DailyChallengeManager

class MenuActivity : AppCompatActivity() {

    private lateinit var btnHiragana: LinearLayout
    private lateinit var btnKatakana: LinearLayout
    private lateinit var btnKanji: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLevel: TextView
    private lateinit var tvKata: TextView
    private lateinit var profileIcon: ImageView
    private lateinit var tvUsername: TextView
    // private lateinit var tvUserPoints: TextView // Dihilangkan karena tidak lagi menampilkan poin terpisah

    // Daily Challenge UI elements
    private lateinit var tvChallengeTitle: TextView
    private lateinit var tvChallengeDescription: TextView // Perbaikan dari 'var var'
    private lateinit var pbChallengeProgress: ProgressBar
    private lateinit var tvChallengeProgress: TextView
    private lateinit var tvChallengeReward: TextView
    private lateinit var ivChallengeFlame: ImageView

    private lateinit var uid: String
    private lateinit var dailyChallengeManager: DailyChallengeManager

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
        dailyChallengeManager = DailyChallengeManager.getInstance(this, uid)

        profileIcon = findViewById(R.id.profileIcon)
        tvUsername = findViewById(R.id.tvUsername)
        // tvUserPoints = findViewById(R.id.tvUserPoints) // Dihilangkan

        // Inisialisasi Daily Challenge UI elements
        tvChallengeTitle = findViewById(R.id.tvChallengeTitle)
        tvChallengeDescription = findViewById(R.id.tvChallengeDescription) // Pastikan sudah diperbaiki
        pbChallengeProgress = findViewById(R.id.pbChallengeProgress)
        tvChallengeProgress = findViewById(R.id.tvChallengeProgress)
        tvChallengeReward = findViewById(R.id.tvChallengeReward)
        ivChallengeFlame = findViewById(R.id.ivChallengeFlame)


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
        updateDailyChallengeUI()

        dailyChallengeManager.addOnChangeListener {
            updateDailyChallengeUI()
            updateProgressGabungan() // Panggil untuk update progress bar dan tvKata
        }

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
        dailyChallengeManager.initDailyChallenge() // Pastikan tantangan diinisialisasi
        updateProgressGabungan() // Perbarui progress bar dan tvKata
        loadProfileData() // Perbarui username dan foto profil
        updateDailyChallengeUI() // Perbarui UI tantangan
    }

    override fun onDestroy() {
        super.onDestroy()
        dailyChallengeManager.removeOnChangeListener {
            updateDailyChallengeUI()
            updateProgressGabungan()
        }
    }

    private fun updateProgressGabungan() {
        val (jumlahKata, totalKata) = ProgressUtil.getProgressGabungan(this, uid)
        val (jumlahKuis, totalKuis) = ProgressUtil.getKuisProgressGabungan(this, uid)
        val persentaseGabungan = ProgressUtil.getPersentaseGabungan(this, uid) // Ambil persentase gabungan

        progressBar.progress = persentaseGabungan
        tvLevel.text = ProgressUtil.getLevelLabel(persentaseGabungan)
        tvKata.text = "$jumlahKata/$totalKata kata • $jumlahKuis/$totalKuis kuis"
    }

    private fun loadProfileData() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: "User"
                    // Poin tidak lagi diambil karena tvUserPoints dihilangkan
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

    private fun updateDailyChallengeUI() {
        tvChallengeTitle.text = dailyChallengeManager.getChallengeTitle()
        tvChallengeDescription.text = dailyChallengeManager.getChallengeDescription()
        pbChallengeProgress.max = dailyChallengeManager.challengeTarget
        pbChallengeProgress.progress = dailyChallengeManager.currentChallengeProgress
        tvChallengeProgress.text = "${dailyChallengeManager.currentChallengeProgress}/${dailyChallengeManager.challengeTarget} selesai"
        tvChallengeReward.text = "Reward: ${dailyChallengeManager.rewardPoints} poin"

        if (dailyChallengeManager.isChallengeCompleted) {
            ivChallengeFlame.setImageResource(R.drawable.ic_flame_completed)
            tvChallengeReward.text = "Selesai!"
            tvChallengeReward.setTextColor(resources.getColor(R.color.green_success))
        } else {
            ivChallengeFlame.setImageResource(R.drawable.ic_flame)
            tvChallengeReward.setTextColor(resources.getColor(R.color.black))
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