package cm.a15022.athletelab.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.FeedPost
import cm.a15022.athletelab.utils.UiUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedPostAdapter(
    private val currentUserId: String,
    private val onEditClick: (FeedPost) -> Unit,
    private val onDeleteClick: (FeedPost) -> Unit
) : RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder>() {

    private val posts = mutableListOf<FeedPost>()

    fun submitList(newList: List<FeedPost>) {
        posts.clear()
        posts.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_post, parent, false)

        return FeedPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedPostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class FeedPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.postIcon)
        private val author: TextView = itemView.findViewById(R.id.postAuthor)
        private val meta: TextView = itemView.findViewById(R.id.postMeta)
        private val text: TextView = itemView.findViewById(R.id.postText)
        private val result: TextView = itemView.findViewById(R.id.postResult)

        private val actionsLayout: LinearLayout = itemView.findViewById(R.id.postActionsLayout)
        private val editButton: Button = itemView.findViewById(R.id.editPostButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deletePostButton)

        fun bind(post: FeedPost) {
            val context = itemView.context
            UiUtils.setSportIcon(icon, post.sport)

            author.text = post.authorName.ifBlank { context.getString(R.string.athlete) }

            val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            val dateText = formatter.format(Date(post.createdAt))

            val visibilityLabel = when (post.visibility) {
                "friends" -> context.getString(R.string.friends_visibility)
                else -> context.getString(R.string.public_visibility)
            }

            meta.text = "${UiUtils.getSportLabel(context, post.sport)} • $visibilityLabel • $dateText"

            text.text = post.text

            if (post.result.isBlank()) {
                result.visibility = View.GONE
            } else {
                result.visibility = View.VISIBLE
                result.text = post.result
            }

            if (post.userId == currentUserId) {
                actionsLayout.visibility = View.VISIBLE

                editButton.setOnClickListener {
                    onEditClick(post)
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(post)
                }
            } else {
                actionsLayout.visibility = View.GONE
            }
        }
    }
}
