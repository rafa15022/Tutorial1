package cm.a15022.athletelab.repository

import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).await()

        return result.user ?: throw IllegalStateException("Utilizador inválido")
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        sport: String,
        phoneNumber: String
    ): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(
            email.trim(),
            password
        ).await()

        val user = result.user ?: throw IllegalStateException("Utilizador inválido")

        val profile = UserProfile(
            userId = user.uid,
            name = name.trim(),
            email = email.trim(),
            phoneNumber = phoneNumber,
            phoneVerified = false,
            favoriteSport = sport,
            premiumEnabled = false,
            avatarUri = getDefaultAvatar(sport),
            bio = ""
        )

        db.collection("users")
            .document(user.uid)
            .set(profile)
            .await()

        return user
    }

    suspend fun loginDemo(): FirebaseUser {
        val result = auth.signInAnonymously().await()
        val user = result.user ?: throw IllegalStateException("Utilizador demo inválido")

        val profileRef = db.collection("users").document(user.uid)
        val profileSnap = profileRef.get().await()

        if (!profileSnap.exists()) {
            val profile = UserProfile(
                userId = user.uid,
                name = "demo_athlete",
                email = "demo@athletelab.pt",
                phoneNumber = "+351910000001",
                phoneVerified = true,
                favoriteSport = Sport.MUSCLE,
                premiumEnabled = true,
                avatarUri = "android_musculacao",
                bio = "Conta demo da AthleteLab."
            )

            profileRef.set(profile).await()
        }

        return user
    }

    fun logout() {
        auth.signOut()
    }

    private fun getDefaultAvatar(sport: String): String {
        return when (sport) {
            Sport.FOOTBALL, "Futebol" -> "android_futebol"
            Sport.RUNNING, "Atletismo" -> "android_atletismo"
            else -> "android_musculacao"
        }
    }
}