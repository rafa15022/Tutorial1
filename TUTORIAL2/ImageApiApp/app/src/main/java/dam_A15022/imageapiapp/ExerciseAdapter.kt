package dam_A15022.imageapiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExerciseAdapter : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private var exerciseList: List<Exercise> = emptyList()

    private fun capitalizeText(text: String?): String {
        if (text.isNullOrBlank()) {
            return "-"
        }

        return text.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)

        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]

        holder.exerciseTitle.text = capitalizeText(exercise.name)

        val muscles = exercise.primaryMuscles.joinToString(", ") { muscle ->
            capitalizeText(muscle)
        }

        holder.exerciseSubtitle.text =
            "Músculo: $muscles\nEquipamento: ${capitalizeText(exercise.equipment)}"

        val imageUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/${exercise.images[0]}"

        Glide.with(holder.exerciseImage.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.exerciseImage)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    fun submitList(newList: List<Exercise>) {
        exerciseList = newList
        notifyDataSetChanged()
    }

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseImage: ImageView = itemView.findViewById(R.id.exerciseImage)
        val exerciseTitle: TextView = itemView.findViewById(R.id.exerciseTitle)
        val exerciseSubtitle: TextView = itemView.findViewById(R.id.exerciseSubtitle)
    }
}