package cm.a15022.athletelab.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.model.Workout
import cm.a15022.athletelab.utils.UiUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutAdapter(
    private val onDeleteClick: ((Workout) -> Unit)? = null,
    private val onEditClick: ((Workout) -> Unit)? = null
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private val workouts = mutableListOf<Workout>()

    fun submitList(newList: List<Workout>) {
        workouts.clear()
        workouts.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)

        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val result: TextView? = itemView.findViewById(R.id.workoutResult)
        private val icon: ImageView = itemView.findViewById(R.id.workoutIcon)
        private val title: TextView = itemView.findViewById(R.id.workoutTitle)
        private val details: TextView = itemView.findViewById(R.id.workoutDetails)
        private val date: TextView = itemView.findViewById(R.id.workoutDate)
        private val notes: TextView? = itemView.findViewById(R.id.workoutNotes)

        private val editButton: Button? = itemView.findViewById(R.id.editWorkoutButton)
        private val deleteButton: Button? = itemView.findViewById(R.id.deleteWorkoutButton)

        fun bind(workout: Workout) {
            val context = itemView.context
            title.text = workout.title
            UiUtils.setSportIcon(icon, workout.sport)

            if (workout.sport == Sport.FOOTBALL && workout.trainingType.startsWith("Resultado:")) {
                val matchResult = workout.trainingType.removePrefix("Resultado:").trim()

                if (matchResult.isNotBlank()) {
                    result?.visibility = View.VISIBLE
                    result?.text = matchResult
                } else {
                    result?.visibility = View.GONE
                }
            } else {
                result?.visibility = View.GONE
            }

            details.text = when (workout.sport) {
                Sport.MUSCLE -> {
                    val duration = workout.durationMinutes.toInt()

                    if (workout.exercise.isNotBlank()) {
                        context.getString(R.string.duration_exercises_format, duration, workout.exercise)
                    } else {
                        context.getString(
                            R.string.duration_sets_format,
                            duration,
                            workout.sets,
                            workout.reps,
                            workout.loadKg
                        )
                    }
                }

                Sport.FOOTBALL -> {
                    if (workout.exercise.isNotBlank()) {
                        workout.exercise
                    } else {
                        context.getString(
                            R.string.football_stats_format,
                            workout.goals,
                            workout.assists,
                            workout.minutesPlayed,
                            workout.position
                        )
                    }
                }

                Sport.RUNNING -> {
                    val parts = mutableListOf<String>()

                    if (workout.distanceKm > 0) {
                        parts.add("${workout.distanceKm} km")
                    }

                    if (workout.durationMinutes > 0) {
                        parts.add(context.getString(R.string.duration_format, workout.durationMinutes.toInt()))
                    }

                    if (workout.pace.isNotBlank()) {
                        parts.add(context.getString(R.string.average_pace_text, workout.pace))
                    }

                    if (workout.position.isNotBlank()) {
                        parts.add(context.getString(R.string.final_position_text, workout.position))
                    }

                    parts.joinToString(" • ")
                }

                else -> workout.notes
            }

            if (workout.notes.isBlank()) {
                notes?.visibility = View.GONE
            } else {
                notes?.visibility = View.VISIBLE
                notes?.text = context.getString(R.string.notes_text, workout.notes)
            }

            val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            date.text = formatter.format(Date(workout.dateMillis))

            if (onEditClick == null) {
                editButton?.visibility = View.GONE
            } else {
                editButton?.visibility = View.VISIBLE
                editButton?.setOnClickListener {
                    onEditClick.invoke(workout)
                }
            }

            if (onDeleteClick == null) {
                deleteButton?.visibility = View.GONE
            } else {
                deleteButton?.visibility = View.VISIBLE
                deleteButton?.setOnClickListener {
                    onDeleteClick.invoke(workout)
                }
            }
        }
    }
}
