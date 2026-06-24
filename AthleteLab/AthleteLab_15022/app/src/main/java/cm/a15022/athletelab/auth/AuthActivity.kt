package cm.a15022.athletelab.auth

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.ChallengeRepository
import cm.a15022.athletelab.ui.MainActivity
import cm.a15022.athletelab.ui.PhoneVerifyActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val challengeRepository = ChallengeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)

        if (authRepository.currentUser() != null) {
            openMain()
            return
        }

        val logo = findViewById<ImageView>(R.id.logoImage)
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val sportSpinner = findViewById<Spinner>(R.id.sportSpinner)
        val statusText = findViewById<TextView>(R.id.statusText)

        sportSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Sport.all
        )

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isBlank() || password.isBlank()) {
                statusText.text = "Preenche email e password"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    statusText.text = "A entrar..."
                    val user = authRepository.login(email, password)
                    challengeRepository.seedDefaultChallenges(user.uid)
                    openMain()
                } catch (e: Exception) {
                    statusText.text = e.message ?: "Erro ao entrar"
                }
            }
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = normalizePhone(phoneInput.text.toString())
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val sport = sportSpinner.selectedItem.toString()

            if (name.isBlank()) {
                statusText.text = "Preenche o nome"
                return@setOnClickListener
            }

            if (phone.isBlank()) {
                statusText.text = "Preenche o número de telefone"
                return@setOnClickListener
            }

            if (email.isBlank() || password.isBlank()) {
                statusText.text = "Preenche email e password"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    statusText.text = "A criar conta..."

                    val user = authRepository.register(
                        name = name,
                        email = email,
                        password = password,
                        sport = sport,
                        phoneNumber = phone
                    )

                    challengeRepository.seedDefaultChallenges(user.uid)

                    val intent = Intent(this@AuthActivity, PhoneVerifyActivity::class.java)
                    intent.putExtra("phoneNumber", phone)
                    startActivity(intent)

                } catch (e: Exception) {
                    statusText.text = e.message ?: "Erro ao criar conta"
                }
            }
        }

        findViewById<Button>(R.id.forgotPasswordButton).setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isBlank()) {
                statusText.text = "Escreve o email para recuperar a password"
                return@setOnClickListener
            }

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    statusText.text = "Email de recuperação enviado"
                }
                .addOnFailureListener {
                    statusText.text = it.message ?: "Erro ao enviar email"
                }
        }
    }

    private fun normalizePhone(input: String): String {
        val phone = input
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")

        if (phone.startsWith("+")) {
            return phone
        }

        if (phone.length == 9 && phone.startsWith("9")) {
            return "+351$phone"
        }

        return phone
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}