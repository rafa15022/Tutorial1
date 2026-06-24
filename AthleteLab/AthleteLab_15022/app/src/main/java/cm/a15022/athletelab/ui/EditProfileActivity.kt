package cm.a15022.athletelab.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.UserRepository
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private lateinit var avatarPreview: ImageView
    private lateinit var nameInput: EditText
    private lateinit var sportSpinner: Spinner
    private lateinit var premiumCheck: CheckBox
    private lateinit var bioInput: EditText

    private var selectedAvatarName = "android_musculacao"

    private val avatarNames = listOf(
        "android_musculacao",
        "android_musculacao_f",
        "android_atletismo",
        "android_atletismo_f",
        "android_futebol",
        "android_futebol_f"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val uid = authRepository.currentUser()?.uid ?: run {
            Toast.makeText(this, getString(R.string.invalid_session), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        avatarPreview = findViewById(R.id.avatarPreview)
        nameInput = findViewById(R.id.nameInput)
        sportSpinner = findViewById(R.id.sportSpinner)
        premiumCheck = findViewById(R.id.premiumCheck)
        bioInput = findViewById(R.id.bioInput)

        val chooseAvatarButton = findViewById<Button>(R.id.chooseAvatarButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        sportSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Sport.all
        )

        avatarPreview.setImageResource(getAvatarDrawable(selectedAvatarName))

        userRepository.observeUser(uid) { user ->
            if (user != null) {
                nameInput.setText(user.name)
                bioInput.setText(user.bio)
                premiumCheck.isChecked = user.premiumEnabled

                val sportIndex = Sport.all.indexOf(user.favoriteSport)
                if (sportIndex >= 0) {
                    sportSpinner.setSelection(sportIndex)
                }

                val savedAvatar = normalizeAvatarName(user.avatarUri)
                selectedAvatarName = savedAvatar
                avatarPreview.setImageResource(getAvatarDrawable(savedAvatar))
            }
        }

        chooseAvatarButton.setOnClickListener {
            showAvatarDialog()
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val bio = bioInput.text.toString().trim()
            val sport = sportSpinner.selectedItem.toString()
            val premium = premiumCheck.isChecked

            if (name.isBlank()) {
                Toast.makeText(this, getString(R.string.write_your_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    userRepository.updateProfile(
                        userId = uid,
                        name = name,
                        favoriteSport = sport,
                        premiumEnabled = premium,
                        avatarUri = selectedAvatarName,
                        bio = bio
                    )

                    Toast.makeText(
                        this@EditProfileActivity,
                        getString(R.string.profile_saved),
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@EditProfileActivity,
                        e.message ?: getString(R.string.profile_save_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showAvatarDialog() {
        val labels = arrayOf(
            getString(R.string.avatar_muscle),
            getString(R.string.avatar_muscle_f),
            getString(R.string.avatar_running),
            getString(R.string.avatar_running_f),
            getString(R.string.avatar_football),
            getString(R.string.avatar_football_f)
        )

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_avatar))
            .setItems(labels) { _, which ->
                selectedAvatarName = avatarNames[which]
                avatarPreview.setImageResource(getAvatarDrawable(selectedAvatarName))
            }
            .show()
    }

    private fun normalizeAvatarName(avatarName: String): String {
        return if (avatarNames.contains(avatarName)) {
            avatarName
        } else {
            "android_musculacao"
        }
    }

    private fun getAvatarDrawable(avatarName: String): Int {
        return when (avatarName) {
            "android_musculacao" -> R.drawable.android_musculacao
            "android_musculacao_f" -> R.drawable.android_musculacao_f
            "android_atletismo" -> R.drawable.android_atletismo
            "android_atletismo_f" -> R.drawable.android_atletismo_f
            "android_futebol" -> R.drawable.android_futebol
            "android_futebol_f" -> R.drawable.android_futebol_f
            else -> R.drawable.android_musculacao
        }
    }
}
