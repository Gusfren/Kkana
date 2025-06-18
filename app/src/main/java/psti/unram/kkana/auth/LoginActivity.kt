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
import psti.unram.kkana.ui.MenuActivity
import psti.unram.kkana.utils.PrefManager

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val goToRegister = findViewById<TextView>(R.id.goToRegister)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid ?: ""
                        val userEmail = user?.email ?: ""
                        val displayName = user?.displayName ?: "User"

                        // Simpan ke SharedPreferences
                        val prefManager = PrefManager(this)
                        prefManager.saveUser(uid, userEmail, displayName)

                        Toast.makeText(this, "Login sukses", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        // Cek apakah datang dari register
        val fromRegister = intent.getBooleanExtra("fromRegister", false)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && !fromRegister) {
            // Auto-login hanya kalau BUKAN dari register
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

}
