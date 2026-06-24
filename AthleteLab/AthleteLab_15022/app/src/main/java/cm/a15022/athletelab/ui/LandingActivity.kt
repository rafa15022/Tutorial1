package cm.a15022.athletelab.ui

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.repository.ChallengeRepository
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import cm.a15022.athletelab.R
import cm.a15022.athletelab.auth.AuthActivity
import cm.a15022.athletelab.repository.AuthRepository

class LandingActivity : AppCompatActivity() {
    private val authRepository = AuthRepository()
    private val challengeRepository = ChallengeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val logo = findViewById<ImageView>(R.id.landingLogo)
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))

        findViewById<Button>(R.id.startButton).setOnClickListener {
            openAuth()
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            openAuth()
        }

        findViewById<Button>(R.id.demoButton).setOnClickListener {
            lifecycleScope.launch {
                try {
                    val user = authRepository.loginDemo()
                    challengeRepository.seedDefaultChallenges(user.uid)

                    val intent = Intent(this@LandingActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LandingActivity,
                        e.message ?: getString(R.string.demo_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
    }
}