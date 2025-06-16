package psti.unram.kkana.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import psti.unram.kkana.R

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi view
        val emailRegister = findViewById<EditText>(R.id.emailRegister)
        val passwordRegister = findViewById<EditText>(R.id.passwordRegister)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginLink = findViewById<TextView>(R.id.loginLink) // Tambahkan ini di layout

        // Navigasi ke LoginActivity jika user klik "login"
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Tombol daftar ditekan
        registerButton.setOnClickListener {
            val email = emailRegister.text.toString().trim()
            val password = passwordRegister.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses registrasi
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Kirim email verifikasi (opsional)
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registrasi sukses. Silakan verifikasi email kamu.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Gagal kirim email verifikasi.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        // Arahkan ke login
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Registrasi gagal: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
