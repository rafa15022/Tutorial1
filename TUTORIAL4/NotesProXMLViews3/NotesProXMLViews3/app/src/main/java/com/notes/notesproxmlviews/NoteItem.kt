package com.notes.notesproxmlviews

/**
 * Small helper model used by the RecyclerView.
 * Firestore gives us the document id separately from the note fields.
 */
data class NoteItem(
    val docId: String,
    val note: Note
)
