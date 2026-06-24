package cm.a15022.athletelab.repository

import android.util.Log
import cm.a15022.athletelab.model.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WorkoutRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun deleteWorkout(workoutId: String) {
        db.collection("workouts")
            .document(workoutId)
            .delete()
            .await()
    }

    suspend fun getWorkoutById(workoutId: String): Workout? {
        val doc = db.collection("workouts")
            .document(workoutId)
            .get()
            .await()

        val workout = doc.toObject(Workout::class.java)
        workout?.id = doc.id

        return workout
    }

    suspend fun saveWorkout(workout: Workout): Workout {
        if (workout.dateMillis == 0L) {
            workout.dateMillis = System.currentTimeMillis()
        }

        val ref = if (workout.id.isBlank()) {
            db.collection("workouts").document()
        } else {
            db.collection("workouts").document(workout.id)
        }

        workout.id = ref.id
        ref.set(workout).await()
        return workout
    }

    suspend fun getMyWorkouts(userId: String): List<Workout> {
        return db.collection("workouts")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Workout::class.java)
            .sortedByDescending { it.dateMillis }
    }

    fun observeMyWorkouts(userId: String, onData: (List<Workout>) -> Unit) {
        db.collection("workouts")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e("WorkoutRepository", "Erro ao carregar treinos", error)
                    onData(emptyList())
                    return@addSnapshotListener
                }

                val list = snap
                    ?.toObjects(Workout::class.java)
                    ?.sortedByDescending { it.dateMillis }
                    ?: emptyList()

                onData(list)
            }
    }

    suspend fun getWorkoutsByDay(userId: String, start: Long, end: Long): List<Workout> {
        return getMyWorkouts(userId)
            .filter { it.dateMillis in start..end }
            .sortedByDescending { it.dateMillis }
    }
}