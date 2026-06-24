package com.notes.notesproxmlviews

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter that shows the user's notes in the RecyclerView.
 * When a note is clicked, it opens NoteDetailsActivity in edit mode.
 */
class NoteAdapter(
    private val notes: MutableList<NoteItem>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)
        val imageView: ImageView = itemView.findViewById(R.id.note_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val noteItem = notes[position]
        val note = noteItem.note

        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        val timestamp = note.timestamp
        holder.timestampTextView.text = if (timestamp != null) {
            Utility.timestampToString(timestamp)
        } else {
            ""
        }

        if (!note.imageBase64.isNullOrEmpty()) {
            try {
                val bytes = Base64.decode(note.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.imageView.setImageBitmap(bitmap)
                holder.imageView.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.imageView.visibility = View.GONE
            }
        } else {
            holder.imageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NoteDetailsActivity::class.java).apply {
                putExtra("title", note.title)
                putExtra("content", note.content)
                putExtra("docId", noteItem.docId)
                putExtra("imageBase64", note.imageBase64)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = notes.size
}
