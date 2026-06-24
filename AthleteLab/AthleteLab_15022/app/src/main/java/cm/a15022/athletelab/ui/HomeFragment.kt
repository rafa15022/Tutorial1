package cm.a15022.athletelab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.ChallengeRepository
import cm.a15022.athletelab.repository.UserRepository
import cm.a15022.athletelab.repository.WorkoutRepository
import cm.a15022.athletelab.ui.adapters.ChallengeAdapter
import cm.a15022.athletelab.ui.adapters.WorkoutAdapter
import cm.a15022.athletelab.utils.UiUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val workoutRepository = WorkoutRepository()
    private val challengeRepository = ChallengeRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        val latestAdapter = WorkoutAdapter()

        val challengesAdapter = ChallengeAdapter { challenge ->
            lifecycleScope.launch {
                try {
                    challengeRepository.addProgress(challenge.id)

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.add_progress_success),
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.add_progress_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val workoutsRecycler = view.findViewById<RecyclerView>(R.id.latestWorkoutsRecycler)
        workoutsRecycler.layoutManager = LinearLayoutManager(requireContext())
        workoutsRecycler.adapter = latestAdapter

        val challengesRecycler = view.findViewById<RecyclerView>(R.id.activeChallengesRecycler)
        challengesRecycler.layoutManager = LinearLayoutManager(requireContext())
        challengesRecycler.adapter = challengesAdapter

        val welcomeText = view.findViewById<TextView>(R.id.welcomeText)
        val sportText = view.findViewById<TextView>(R.id.sportText)
        val sportIcon = view.findViewById<ImageView>(R.id.sportIcon)
        val countText = view.findViewById<TextView>(R.id.workoutCountText)
        val weekText = view.findViewById<TextView>(R.id.weekProgressText)

        userRepository.observeUser(uid) { user ->
            val name = user?.name?.ifBlank { getString(R.string.athlete) } ?: getString(R.string.athlete)
            val sport = user?.favoriteSport?.ifBlank { Sport.MUSCLE } ?: Sport.MUSCLE

            val sportLabel = UiUtils.getSportLabel(requireContext(), sport)

            welcomeText.text = getString(R.string.welcome_user, name)
            sportText.text = getString(R.string.main_sport, sportLabel)
            UiUtils.setSportIcon(sportIcon, sport)
        }

        workoutRepository.observeMyWorkouts(uid) { workouts ->
            val orderedWorkouts = workouts.sortedByDescending { it.dateMillis }

            latestAdapter.submitList(orderedWorkouts.take(3))
            countText.text = workouts.size.toString()

            val workoutsThisWeek = workouts.filter {
                isInCurrentWeek(it.dateMillis)
            }

            val weeklyGoal = 5
            val progress = ((workoutsThisWeek.size.toDouble() / weeklyGoal) * 100)
                .toInt()
                .coerceAtMost(100)

            weekText.text = "$progress%"
        }

        challengeRepository.observeChallenges(uid) { challenges ->
            challengesAdapter.submitList(
                challenges.filter { it.status == "active" }.take(3)
            )
        }
    }

    private fun isInCurrentWeek(dateMillis: Long): Boolean {
        val now = Calendar.getInstance()

        val startOfWeek = Calendar.getInstance()
        startOfWeek.timeInMillis = now.timeInMillis
        startOfWeek.firstDayOfWeek = Calendar.MONDAY

        val dayOfWeek = startOfWeek.get(Calendar.DAY_OF_WEEK)

        val diff = if (dayOfWeek == Calendar.SUNDAY) {
            -6
        } else {
            Calendar.MONDAY - dayOfWeek
        }

        startOfWeek.add(Calendar.DAY_OF_MONTH, diff)
        startOfWeek.set(Calendar.HOUR_OF_DAY, 0)
        startOfWeek.set(Calendar.MINUTE, 0)
        startOfWeek.set(Calendar.SECOND, 0)
        startOfWeek.set(Calendar.MILLISECOND, 0)

        val endOfWeek = Calendar.getInstance()
        endOfWeek.timeInMillis = startOfWeek.timeInMillis
        endOfWeek.add(Calendar.DAY_OF_WEEK, 7)

        return dateMillis >= startOfWeek.timeInMillis &&
                dateMillis < endOfWeek.timeInMillis
    }
}