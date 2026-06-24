package cm.a15022.athletelab.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Challenge
import cm.a15022.athletelab.utils.DateUtils
import cm.a15022.athletelab.utils.UiUtils

class ChallengeAdapter(private val onUpdate: (Challenge) -> Unit) : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {
    private val items = mutableListOf<Challenge>()

    fun submitList(list: List<Challenge>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_challenge, parent, false)
        return ChallengeViewHolder(view, onUpdate)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ChallengeViewHolder(itemView: View, private val onUpdate: (Challenge) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleText)
        private val detail: TextView = itemView.findViewById(R.id.detailText)
        private val progress: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val button: Button = itemView.findViewById(R.id.updateButton)

        fun bind(challenge: Challenge) {
            val context = itemView.context
            title.text = challenge.title
            val percent = if (challenge.target > 0) ((challenge.progress / challenge.target) * 100).toInt() else 0

            detail.text = context.getString(
                R.string.challenge_meta,
                UiUtils.getSportLabel(context, challenge.sport),
                UiUtils.getChallengeTypeLabel(context, challenge.type),
                UiUtils.getChallengeScopeLabel(context, challenge.scope),
                UiUtils.getChallengeStatusLabel(context, challenge.status),
                DateUtils.formatDay(challenge.dueDateMillis)
            )

            progress.progress = percent.coerceIn(0, 100)
            button.isEnabled = challenge.status == "active"
            button.text = if (challenge.status == "completed") {
                context.getString(R.string.completed)
            } else {
                context.getString(R.string.progress_button)
            }
            button.setOnClickListener { onUpdate(challenge) }
        }
    }
}
