package cm.a15022.athletelab.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.WorkoutRepository
import cm.a15022.athletelab.ui.adapters.WorkoutAdapter
import kotlinx.coroutines.launch

class WorkoutsFragment : Fragment() {
    private var editingWorkoutId: String = ""
    private val authRepository = AuthRepository()
    private val workoutRepository = WorkoutRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        val adapter = WorkoutAdapter(
            onDeleteClick = { workout ->
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_workout_title))
                    .setMessage(getString(R.string.delete_workout_message))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        lifecycleScope.launch {
                            try {
                                workoutRepository.deleteWorkout(workout.id)
                                Toast.makeText(requireContext(), getString(R.string.workout_deleted), Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), getString(R.string.workout_delete_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            },

            onEditClick = { workout ->
                val intent = Intent(requireContext(), AddWorkoutActivity::class.java)
                intent.putExtra("userId", uid)
                intent.putExtra("workoutId", workout.id)
                startActivity(intent)
            }
        )

        val recycler = view.findViewById<RecyclerView>(R.id.workoutsRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<Button>(R.id.addWorkoutButton).setOnClickListener {
            val intent = Intent(requireContext(), AddWorkoutActivity::class.java)
            intent.putExtra("userId", uid)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.openCalendarButton).setOnClickListener {
            (activity as? MainActivity)?.showFragment(
                CalendarFragment(),
                getString(R.string.calendar),
                "calendar"
            )
        }

        view.findViewById<Button>(R.id.openProgressButton).setOnClickListener {
            (activity as? MainActivity)?.showFragment(
                ProgressFragment(),
                getString(R.string.progress),
                "progress"
            )
        }

        workoutRepository.observeMyWorkouts(uid) { workouts ->
            adapter.submitList(workouts)
        }
    }
}