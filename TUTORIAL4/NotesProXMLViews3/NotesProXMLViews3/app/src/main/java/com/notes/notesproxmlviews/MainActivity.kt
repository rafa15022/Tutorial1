package com.notes.notesproxmlviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var addNoteBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuBtn: ImageButton
    private lateinit var emptyNotesTextView: TextView

    private val notes = mutableListOf<NoteItem>()
    private lateinit var noteAdapter: NoteAdapter
    private var notesListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteBtn = findViewById(R.id.add_note_btn)
        recyclerView = findViewById(R.id.recyler_view)
        menuBtn = findViewById(R.id.menu_btn)
        emptyNotesTextView = findViewById(R.id.empty_notes_text_view)

        noteAdapter = NoteAdapter(notes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = noteAdapter

        addNoteBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, NoteDetailsActivity::class.java))
        }

        menuBtn.setOnClickListener {
            showMenu()
        }
    }

    override fun onStart() {
        super.onStart()
        listenForNotes()
    }

    override fun onStop() {
        super.onStop()
        notesListener?.remove()
        notesListener = null
    }

    private fun listenForNotes() {
        notesListener?.remove()

        notesListener = Utility.getCollectionReferenceForNotes()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Utility.showToast(this, "Erro ao carregar notas: ${error.localizedMessage}")
                    return@addSnapshotListener
                }

                notes.clear()

                snapshot?.documents?.forEach { document ->
                    val note = document.toObject(Note::class.java)
                    if (note != null) {
                        notes.add(NoteItem(document.id, note))
                    }
                }

                noteAdapter.notifyDataSetChanged()
                emptyNotesTextView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun showMenu() {
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("Logout")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.title.toString()) {
                "Logout" -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}
