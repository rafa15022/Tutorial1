package cm.a15022.athletelab.repository

import cm.a15022.athletelab.model.Challenge
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.utils.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale

class ChallengeRepository {

    private val db = FirebaseFirestore.getInstance()

    fun observeChallenges(userId: String, onData: (List<Challenge>) -> Unit) =
        db.collection("challenges")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    val challenge = doc.toObject(Challenge::class.java)
                    challenge?.id = doc.id
                    challenge
                }.orEmpty()
                    .sortedByDescending { it.createdAt }

                onData(list)
            }

    suspend fun createChallenge(challenge: Challenge): Challenge {
        val ref = if (challenge.id.isBlank()) {
            db.collection("challenges").document()
        } else {
            db.collection("challenges").document(challenge.id)
        }

        challenge.id = ref.id
        ref.set(challenge).await()

        return challenge
    }

    suspend fun updateProgress(challenge: Challenge, amount: Double) {
        if (challenge.id.isBlank()) return

        challenge.progress = (challenge.progress + amount).coerceAtMost(challenge.target)
        challenge.status = if (challenge.progress >= challenge.target) "completed" else "active"

        db.collection("challenges")
            .document(challenge.id)
            .set(challenge)
            .await()
    }

    suspend fun addProgress(challengeId: String) {
        if (challengeId.isBlank()) return

        val doc = db.collection("challenges")
            .document(challengeId)
            .get()
            .await()

        val challenge = doc.toObject(Challenge::class.java) ?: return
        challenge.id = doc.id

        updateProgress(challenge, 1.0)
    }

    suspend fun seedDefaultChallenges(userId: String) {
        val existing = db.collection("challenges")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        if (!existing.isEmpty) {
            return
        }

        createDefaultChallenges(userId)
    }

    suspend fun resetDefaultChallenges(userId: String) {
        val existing = db.collection("challenges")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        existing.documents.forEach { doc ->
            doc.reference.delete().await()
        }

        createDefaultChallenges(userId)
    }

    private suspend fun createDefaultChallenges(userId: String) {
        val examples = listOf(
            Challenge(
                userId = userId,
                sport = Sport.MUSCLE,
                type = "weekly",
                scope = "individual",
                title = challengeTitle("muscle_load"),
                target = 5.0,
                progress = 0.0,
                status = "active",
                dueDateMillis = DateUtils.endOfWeek(),
                createdAt = System.currentTimeMillis()
            ),
            Challenge(
                userId = userId,
                sport = Sport.FOOTBALL,
                type = "weekly",
                scope = "group",
                title = challengeTitle("football_training"),
                target = 3.0,
                progress = 0.0,
                status = "active",
                dueDateMillis = DateUtils.endOfWeek(),
                createdAt = System.currentTimeMillis()
            ),
            Challenge(
                userId = userId,
                sport = Sport.RUNNING,
                type = "weekly",
                scope = "individual",
                title = challengeTitle("running_distance"),
                target = 10.0,
                progress = 0.0,
                status = "active",
                dueDateMillis = DateUtils.endOfWeek(),
                createdAt = System.currentTimeMillis()
            ),
            Challenge(
                userId = userId,
                sport = Sport.MUSCLE,
                type = "daily",
                scope = "individual",
                title = challengeTitle("daily_notes"),
                target = 1.0,
                progress = 0.0,
                status = "active",
                dueDateMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000,
                createdAt = System.currentTimeMillis()
            )
        )

        examples.forEach { challenge ->
            createChallenge(challenge)
        }
    }

    private fun challengeTitle(key: String): String {
        val language = Locale.getDefault().language

        return when (language) {
            "en" -> when (key) {
                "muscle_load" -> "Increase load by 5% this week"
                "football_training" -> "Complete 3 technical training sessions this week"
                "running_distance" -> "Run 10 km in total this week"
                "daily_notes" -> "Complete one workout with notes today"
                else -> key
            }

            "es" -> when (key) {
                "muscle_load" -> "Aumentar la carga un 5% esta semana"
                "football_training" -> "Hacer 3 entrenamientos técnicos esta semana"
                "running_distance" -> "Correr 10 km en total esta semana"
                "daily_notes" -> "Completar un entrenamiento con notas hoy"
                else -> key
            }

            else -> when (key) {
                "muscle_load" -> "Aumentar 5% na carga esta semana"
                "football_training" -> "Fazer 3 treinos técnicos esta semana"
                "running_distance" -> "Correr 10 km no total esta semana"
                "daily_notes" -> "Completar um treino com notas hoje"
                else -> key
            }
        }
    }
}