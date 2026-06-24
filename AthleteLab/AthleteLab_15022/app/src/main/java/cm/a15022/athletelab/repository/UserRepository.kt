package cm.a15022.athletelab.repository

import cm.a15022.athletelab.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun observeUser(userId: String, onData: (UserProfile?) -> Unit) {
        db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null || !snapshot.exists()) {
                    onData(null)
                    return@addSnapshotListener
                }

                val user = snapshot.toObject(UserProfile::class.java)
                user?.userId = snapshot.id
                onData(user)
            }
    }

    suspend fun updateProfile(
        userId: String,
        name: String,
        favoriteSport: String,
        premiumEnabled: Boolean,
        avatarUri: String,
        bio: String
    ) {
        val updates = hashMapOf<String, Any>(
            "name" to name,
            "favoriteSport" to favoriteSport,
            "premiumEnabled" to premiumEnabled,
            "avatarUri" to avatarUri,
            "bio" to bio
        )

        db.collection("users")
            .document(userId)
            .set(updates, SetOptions.merge())
            .await()
    }
}