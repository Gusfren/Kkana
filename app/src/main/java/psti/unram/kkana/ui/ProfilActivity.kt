package psti.unram.kkana.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import psti.unram.kkana.R
import psti.unram.kkana.auth.LoginActivity
import psti.unram.kkana.utils.ProgressUtil
import psti.unram.kkana.utils.ScoreManager
import java.io.File
import java.io.FileOutputStream

class ProfilActivity : AppCompatActivity() {

    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnChangePhoto: Button
    private lateinit var tvEmail: TextView
    private lateinit var etUsername: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnReset: Button
    private lateinit var btnResetScore: Button
    private lateinit var btnLogout: Button
    private lateinit var btnBack: ImageButton

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid
    private var imageUri: Uri? = null
    private var localPhotoPath: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let {
                val savedPath = saveImageToInternalStorage(it)
                if (savedPath != null) {
                    localPhotoPath = savedPath
                    savePhotoPathLocally(savedPath)
                    ivProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(savedPath))
                } else {
                    Toast.makeText(this, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        tvEmail = findViewById(R.id.tvEmail)
        etUsername = findViewById(R.id.etUsername)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnReset = findViewById(R.id.btnResetProgress)
        btnResetScore = findViewById(R.id.btnResetScore)
        btnLogout = findViewById(R.id.btnLogout)
        btnBack = findViewById(R.id.btnBack)

        tvEmail.text = auth.currentUser?.email ?: "Email tidak ditemukan"

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnSimpan.setOnClickListener {
            updateUserData()
        }

        btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Reset Progres")
                .setMessage("Apakah Anda yakin ingin mereset seluruh progres belajar?")
                .setPositiveButton("Ya") { _, _ ->
                    val jenisHuruf = listOf("hiragana", "katakana", "kanji")
                    userId?.let { uid ->
                        for (jenis in jenisHuruf) {
                            ProgressUtil.resetProgress(this, jenis, uid)
                        }
                        Toast.makeText(this, "Progress belajar berhasil di-reset!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        btnResetScore.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Reset Skor")
                .setMessage("Apakah Anda yakin ingin mereset semua skor kuis?")
                .setPositiveButton("Ya") { _, _ ->
                    userId?.let { uid ->
                        ScoreManager.resetScores(this, uid)
                        Toast.makeText(this, "Semua skor kuis berhasil di-reset!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        loadUserData()
    }

    private fun loadUserData() {
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val username = doc.getString("username") ?: ""
                        etUsername.setText(username)
                    } else {
                        Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengambil data profil", Toast.LENGTH_SHORT).show()
                }

            val path = loadPhotoPathLocally()
            if (path != null && File(path).exists()) {
                ivProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(path))
            } else {
                ivProfilePhoto.setImageResource(R.drawable.default_profile)
            }
        }
    }

    private fun updateUserData() {
        val username = etUsername.text.toString().trim()

        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)
            val updateData = mapOf("username" to username)

            userRef.update(updateData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "foto_profil.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun savePhotoPathLocally(path: String) {
        val sharedPref = getSharedPreferences("profil_pref", MODE_PRIVATE)
        sharedPref.edit().putString("photo_path", path).apply()
    }

    private fun loadPhotoPathLocally(): String? {
        val sharedPref = getSharedPreferences("profil_pref", MODE_PRIVATE)
        return sharedPref.getString("photo_path", null)
    }
}
