package cm.a15022.athletelab.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.UserRepository
import cm.a15022.athletelab.repository.WorkoutRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val workoutRepository = WorkoutRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        val avatar = view.findViewById<ImageView>(R.id.profileAvatar)
        val nameText = view.findViewById<TextView>(R.id.profileNameText)
        val emailText = view.findViewById<TextView>(R.id.profileEmailText)
        val sportText = view.findViewById<TextView>(R.id.profileSportText)
        val premiumText = view.findViewById<TextView>(R.id.profilePremiumText)
        val bioText = view.findViewById<TextView>(R.id.profileBioText)

        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)
        val coachButton = view.findViewById<Button>(R.id.openCoachButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        val workoutCountText = view.findViewById<TextView>(R.id.profileWorkoutCountText)
        val totalMinutesText = view.findViewById<TextView>(R.id.profileTotalMinutesText)
        val muscleCountText = view.findViewById<TextView>(R.id.profileMuscleCountText)
        val footballCountText = view.findViewById<TextView>(R.id.profileFootballCountText)
        val runningCountText = view.findViewById<TextView>(R.id.profileRunningCountText)

        userRepository.observeUser(uid) { user ->
            if (user != null) {
                nameText.text = user.name.ifBlank { getString(R.string.athlete) }

                emailText.text = user.email.ifBlank {
                    FirebaseAuth.getInstance().currentUser?.email ?: getString(R.string.no_email)
                }

                sportText.text = getString(
                    R.string.main_sport,
                    getSportLabel(user.favoriteSport)
                )

                premiumText.text = if (user.premiumEnabled) {
                    getString(R.string.coach_premium)
                } else {
                    getString(R.string.free_plan)
                }

                avatar.setImageResource(getAvatarDrawable(user.avatarUri))
                avatar.scaleType = ImageView.ScaleType.CENTER_CROP

                if (user.bio.isNotBlank()) {
                    bioText.visibility = View.VISIBLE
                    bioText.text = user.bio
                    bioText.gravity = Gravity.CENTER
                    bioText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                } else {
                    bioText.visibility = View.GONE
                }
            }
        }

        workoutRepository.observeMyWorkouts(uid) { workouts ->
            val totalWorkouts = workouts.size
            val totalMinutes = workouts.sumOf { it.durationMinutes.toInt() }
            val muscleCount = workouts.count { it.sport == Sport.MUSCLE }
            val footballCount = workouts.count { it.sport == Sport.FOOTBALL }
            val runningCount = workouts.count { it.sport == Sport.RUNNING }

            workoutCountText.text = totalWorkouts.toString()
            totalMinutesText.text = "$totalMinutes min"
            muscleCountText.text = muscleCount.toString()
            footballCountText.text = footballCount.toString()
            runningCountText.text = runningCount.toString()
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("userId", uid)
            startActivity(intent)
        }

        coachButton.setOnClickListener {
            (activity as? MainActivity)?.showFragment(
                CoachFragment(),
                getString(R.string.coach),
                "coach"
            )
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun getAvatarDrawable(avatarName: String): Int {
        return when (avatarName) {
            "android_musculacao" -> R.drawable.android_musculacao
            "android_musculacao_f" -> R.drawable.android_musculacao_f
            "android_atletismo" -> R.drawable.android_atletismo
            "android_atletismo_f" -> R.drawable.android_atletismo_f
            "android_futebol" -> R.drawable.android_futebol
            "android_futebol_f" -> R.drawable.android_futebol_f
            else -> R.drawable.android_musculacao
        }
    }

    private fun getSportLabel(sport: String): String {
        return when (sport) {
            Sport.MUSCLE, "Musculação" -> getString(R.string.sport_muscle)
            Sport.FOOTBALL, "Futebol" -> getString(R.string.sport_football)
            Sport.RUNNING, "Atletismo" -> getString(R.string.sport_running)
            else -> sport
        }
    }
}
