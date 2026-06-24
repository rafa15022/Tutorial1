package cm.a15022.athletelab.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.FeedPost
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.repository.FeedRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PostEditorActivity : AppCompatActivity() {

    private val feedRepository = FeedRepository()

    private var editingPostId: String = ""
    private var originalCreatedAt: Long = System.currentTimeMillis()
    private var originalAuthorName: String = ""

    private lateinit var sportSpinner: Spinner
    private lateinit var visibilitySpinner: Spinner
    private lateinit var textInput: EditText
    private lateinit var resultInput: EditText
    private lateinit var chooseImageButton: Button
    private lateinit var publishButton: Button

    private val sportsRaw = listOf(Sport.MUSCLE, Sport.FOOTBALL, Sport.RUNNING)
    private val visibilitiesRaw = listOf("public", "friends")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_editor)

        val uid = intent.getStringExtra("userId")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: run {
                Toast.makeText(this, getString(R.string.invalid_session), Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        val defaultAuthorName = intent.getStringExtra("authorName")
            ?: FirebaseAuth.getInstance().currentUser?.email
            ?: getString(R.string.athlete)

        val favoriteSport = intent.getStringExtra("favoriteSport") ?: Sport.MUSCLE

        editingPostId = intent.getStringExtra("postId") ?: ""
        originalAuthorName = defaultAuthorName

        sportSpinner = findViewById(R.id.sportSpinner)
        visibilitySpinner = findViewById(R.id.visibilitySpinner)
        textInput = findViewById(R.id.textInput)
        resultInput = findViewById(R.id.resultInput)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        publishButton = findViewById(R.id.publishButton)

        val sportLabels = listOf(
            getString(R.string.sport_muscle),
            getString(R.string.sport_football),
            getString(R.string.sport_running)
        )

        sportSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            sportLabels
        )

        val sportIndex = sportsRaw.indexOf(favoriteSport).coerceAtLeast(0)
        sportSpinner.setSelection(sportIndex)

        val visibilityLabels = listOf(
            getString(R.string.public_visibility),
            getString(R.string.friends_visibility)
        )

        visibilitySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            visibilityLabels
        )

        chooseImageButton.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.optional_local_image),
                Toast.LENGTH_SHORT
            ).show()
        }

        publishButton.setOnClickListener {
            val text = textInput.text.toString().trim()
            val result = resultInput.text.toString().trim()

            if (text.isBlank()) {
                Toast.makeText(this, getString(R.string.write_post_text), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = FeedPost(
                id = editingPostId,
                userId = uid,
                authorName = originalAuthorName,
                text = text,
                sport = sportsRaw[sportSpinner.selectedItemPosition],
                result = result,
                visibility = visibilitiesRaw[visibilitySpinner.selectedItemPosition],
                createdAt = if (editingPostId.isBlank()) {
                    System.currentTimeMillis()
                } else {
                    originalCreatedAt
                }
            )

            lifecycleScope.launch {
                try {
                    feedRepository.savePost(post)

                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra("post_id", post.id)
                    )

                    val message = if (editingPostId.isBlank()) {
                        getString(R.string.post_created)
                    } else {
                        getString(R.string.post_updated)
                    }

                    Toast.makeText(
                        this@PostEditorActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@PostEditorActivity,
                        e.message ?: getString(R.string.post_save_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        if (editingPostId.isNotBlank()) {
            publishButton.text = getString(R.string.save_changes)
            loadPostForEdit(editingPostId)
        }
    }

    private fun loadPostForEdit(postId: String) {
        lifecycleScope.launch {
            try {
                val post = feedRepository.getPostById(postId)

                if (post == null) {
                    Toast.makeText(
                        this@PostEditorActivity,
                        getString(R.string.post_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }

                originalCreatedAt = post.createdAt
                originalAuthorName = post.authorName.ifBlank { getString(R.string.athlete) }

                textInput.setText(post.text)
                resultInput.setText(post.result)

                val sportIndex = sportsRaw.indexOf(post.sport).coerceAtLeast(0)
                sportSpinner.setSelection(sportIndex)

                val visibilityIndex = visibilitiesRaw.indexOf(post.visibility).coerceAtLeast(0)
                visibilitySpinner.setSelection(visibilityIndex)

                publishButton.text = getString(R.string.save_changes)

            } catch (e: Exception) {
                Toast.makeText(
                    this@PostEditorActivity,
                    getString(R.string.post_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
