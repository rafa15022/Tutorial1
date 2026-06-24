package com.google.firebase.codelab.friendlychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            goToMainActivity()
            return
        }

        setContentView(R.layout.activity_sign_in)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preenche o email e a password", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                showLoading(false)
                goToMainActivity()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Erro ao iniciar sessão: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun registerUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preenche o email e a password", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "A password deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Conta criada com sucesso", Toast.LENGTH_SHORT).show()
                goToMainActivity()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Erro ao criar conta: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
        registerButton.isEnabled = !show
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}