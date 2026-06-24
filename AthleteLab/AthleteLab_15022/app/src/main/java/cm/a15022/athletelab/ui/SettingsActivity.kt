package cm.a15022.athletelab.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val db = FirebaseFirestore.getInstance()

    private val sportValues = listOf(
        Sport.MUSCLE,
        Sport.FOOTBALL,
        Sport.RUNNING
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val logoImage = findViewById<ImageView>(R.id.settingsLogo)
        val nameText = findViewById<TextView>(R.id.settingsNameText)
        val emailText = findViewById<TextView>(R.id.settingsEmailText)
        val sportText = findViewById<TextView>(R.id.settingsSportText)
        val planText = findViewById<TextView>(R.id.settingsPlanText)

        val favoriteSportSpinner = findViewById<Spinner>(R.id.favoriteSportSpinner)
        val saveSportButton = findViewById<Button>(R.id.saveSportButton)

        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)
        val saveLanguageButton = findViewById<Button>(R.id.saveLanguageButton)
        val backButton = findViewById<Button>(R.id.backButton)

        logoImage.setImageResource(R.drawable.ic_athletelab)

        val uid = authRepository.currentUser()?.uid

        val sportLabels = listOf(
            getString(R.string.sport_muscle),
            getString(R.string.sport_football),
            getString(R.string.sport_running)
        )

        favoriteSportSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            sportLabels
        )

        if (uid != null) {
            userRepository.observeUser(uid) { user ->
                if (user != null) {
                    nameText.text = user.name.ifBlank { getString(R.string.athlete) }
                    emailText.text = user.email.ifBlank { getString(R.string.no_email) }

                    val currentSport = user.favoriteSport.ifBlank { Sport.MUSCLE }
                    sportText.text = getSportLabel(currentSport)

                    val sportIndex = sportValues.indexOf(currentSport)
                    if (sportIndex >= 0) {
                        favoriteSportSpinner.setSelection(sportIndex)
                    }

                    planText.text = if (user.premiumEnabled) {
                        getString(R.string.coach_premium)
                    } else {
                        getString(R.string.free_plan)
                    }
                }
            }
        }

        saveSportButton.setOnClickListener {
            if (uid == null) {
                Toast.makeText(this, getString(R.string.invalid_session), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedSport = sportValues[favoriteSportSpinner.selectedItemPosition]

            lifecycleScope.launch {
                try {
                    db.collection("users")
                        .document(uid)
                        .update("favoriteSport", selectedSport)
                        .await()

                    sportText.text = getSportLabel(selectedSport)

                    Toast.makeText(
                        this@SettingsActivity,
                        getString(R.string.favorite_sport_updated),
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@SettingsActivity,
                        getString(R.string.error_updating_sport),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val languages = listOf(
            getString(R.string.language_portuguese),
            getString(R.string.language_english),
            getString(R.string.language_spanish)
        )

        languageSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )

        when (getCurrentLanguage()) {
            "pt" -> languageSpinner.setSelection(0)
            "en" -> languageSpinner.setSelection(1)
            "es" -> languageSpinner.setSelection(2)
            else -> languageSpinner.setSelection(0)
        }

        saveLanguageButton.setOnClickListener {
            val languageCode = when (languageSpinner.selectedItemPosition) {
                0 -> "pt"
                1 -> "en"
                2 -> "es"
                else -> "pt"
            }

            setAppLanguage(languageCode)

            Toast.makeText(
                this,
                getString(R.string.language_updated),
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setAppLanguage(languageCode: String) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun getCurrentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()

        if (!locales.isEmpty) {
            return locales[0]?.language ?: "pt"
        }

        return "pt"
    }

    private fun getSportLabel(sport: String): String {
        return when (sport) {
            Sport.MUSCLE, "Musculação" -> getString(R.string.sport_muscle)
            Sport.FOOTBALL, "Futebol" -> getString(R.string.sport_football)
            Sport.RUNNING, "Atletismo" -> getString(R.string.sport_running)
            else -> sport
        }
    }
}
