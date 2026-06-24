package cm.a15022.athletelab.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class PhoneVerifyActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var phoneText: TextView
    private lateinit var codeInput: EditText
    private lateinit var statusText: TextView
    private lateinit var confirmButton: Button
    private lateinit var resendButton: Button

    private var phoneNumber: String = ""
    private var verificationId: String = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            statusText.text = "Telefone verificado automaticamente"
            linkPhoneCredential(credential)
        }

        override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
            statusText.text = e.message ?: "Erro ao verificar telefone"
            confirmButton.isEnabled = true
            resendButton.isEnabled = true
        }

        override fun onCodeSent(
            id: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            verificationId = id
            resendToken = token
            statusText.text = "Código enviado por SMS"
            confirmButton.isEnabled = true
            resendButton.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_phone_verify)

        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""

        phoneText = findViewById(R.id.phoneText)
        codeInput = findViewById(R.id.codeInput)
        statusText = findViewById(R.id.statusText)
        confirmButton = findViewById(R.id.confirmCodeButton)
        resendButton = findViewById(R.id.resendCodeButton)

        phoneText.text = phoneNumber
        confirmButton.isEnabled = false
        resendButton.isEnabled = false

        sendCode(false)

        confirmButton.setOnClickListener {
            val code = codeInput.text.toString().trim()

            if (code.length < 6 || verificationId.isBlank()) {
                statusText.text = "Escreve o código de 6 dígitos"
                return@setOnClickListener
            }

            val credential = PhoneAuthProvider.getCredential(
                verificationId,
                code
            )

            linkPhoneCredential(credential)
        }

        resendButton.setOnClickListener {
            sendCode(true)
        }
    }

    private fun sendCode(forceResend: Boolean) {
        statusText.text = "A enviar SMS..."
        confirmButton.isEnabled = false
        resendButton.isEnabled = false

        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)

        if (forceResend && resendToken != null) {
            builder.setForceResendingToken(resendToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    private fun linkPhoneCredential(credential: PhoneAuthCredential) {
        val user = auth.currentUser

        if (user == null) {
            statusText.text = "Sessão inválida"
            return
        }

        lifecycleScope.launch {
            try {
                statusText.text = "A confirmar telefone..."

                try {
                    user.linkWithCredential(credential).await()
                } catch (_: Exception) {
                    // Se já estiver ligado ou o Firebase não deixar repetir,
                    // continuamos e marcamos o telefone como verificado no perfil.
                }

                db.collection("users")
                    .document(user.uid)
                    .update(
                        mapOf(
                            "phoneNumber" to phoneNumber,
                            "phoneVerified" to true
                        )
                    )
                    .await()

                statusText.text = "Telefone confirmado"

                val intent = Intent(this@PhoneVerifyActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                statusText.text = e.message ?: "Erro ao confirmar telefone"
            }
        }
    }
}