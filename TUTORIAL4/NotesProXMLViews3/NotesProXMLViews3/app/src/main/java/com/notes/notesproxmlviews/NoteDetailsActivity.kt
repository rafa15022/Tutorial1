package com.notes.notesproxmlviews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp.Companion.now
import com.google.firebase.firestore.DocumentReference
import java.io.ByteArrayOutputStream
import kotlin.math.min

class NoteDetailsActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveNoteBtn: ImageButton
    private lateinit var pageTitleTextView: TextView
    private lateinit var deleteNoteTextViewBtn: TextView
    private lateinit var chooseImageBtn: Button
    private lateinit var noteImagePreview: ImageView

    private var title: String? = null
    private var content: String? = null
    private var docId: String? = null
    private var isEditMode: Boolean = false
    private var imageBase64: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val resizedBitmap = resizeBitmap(bitmap, 600)
                imageBase64 = bitmapToBase64(resizedBitmap)
                noteImagePreview.setImageBitmap(resizedBitmap)
                noteImagePreview.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Utility.showToast(this, "Erro ao escolher imagem: ${e.localizedMessage}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.notes_content_text)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        pageTitleTextView = findViewById(R.id.page_title)
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn)
        chooseImageBtn = findViewById(R.id.choose_image_btn)
        noteImagePreview = findViewById(R.id.note_image_preview)

        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId")
        imageBase64 = intent.getStringExtra("imageBase64")

        isEditMode = !docId.isNullOrEmpty()

        titleEditText.setText(title)
        contentEditText.setText(content)

        if (!imageBase64.isNullOrEmpty()) {
            base64ToBitmap(imageBase64!!)?.let {
                noteImagePreview.setImageBitmap(it)
                noteImagePreview.visibility = View.VISIBLE
            }
        }

        if (isEditMode) {
            pageTitleTextView.text = getString(R.string.edit_your_note)
            deleteNoteTextViewBtn.visibility = View.VISIBLE
        }

        chooseImageBtn.setOnClickListener {
            pickImage.launch("image/*")
        }

        saveNoteBtn.setOnClickListener {
            saveNote()
        }

        deleteNoteTextViewBtn.setOnClickListener {
            deleteNoteFromFirebase()
        }
    }

    private fun saveNote() {
        val noteTitle = titleEditText.text.toString().trim()
        val noteContent = contentEditText.text.toString().trim()

        if (noteTitle.isEmpty()) {
            titleEditText.error = "Title is required"
            return
        }

        val note = Note()
        note.title = noteTitle
        note.content = noteContent
        note.timestamp = now()
        note.imageBase64 = imageBase64

        saveNoteToFirebase(note)
    }

    private fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference = if (isEditMode) {
            Utility.getCollectionReferenceForNotes().document(docId.toString())
        } else {
            Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note saved successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while saving note")
                }
            }
        })
    }

    private fun deleteNoteFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes()
            .document(docId.toString())

        documentReference.delete().addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note deleted successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while deleting note")
                }
            }
        })
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val scale = min(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
