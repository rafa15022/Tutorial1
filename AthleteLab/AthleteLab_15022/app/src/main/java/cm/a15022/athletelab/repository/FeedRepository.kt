package cm.a15022.athletelab.repository

import cm.a15022.athletelab.model.FeedPost
import cm.a15022.athletelab.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FeedRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun createPost(post: FeedPost): FeedPost {
        return savePost(post)
    }

    suspend fun savePost(post: FeedPost): FeedPost {
        val ref = if (post.id.isBlank()) {
            db.collection("feedPosts").document()
        } else {
            db.collection("feedPosts").document(post.id)
        }

        if (post.id.isBlank()) {
            post.id = ref.id
            post.createdAt = System.currentTimeMillis()
        }

        ref.set(post).await()
        return post
    }

    suspend fun getPostById(postId: String): FeedPost? {
        val doc = db.collection("feedPosts")
            .document(postId)
            .get()
            .await()

        val post = doc.toObject(FeedPost::class.java)
        post?.id = doc.id
        return post
    }

    suspend fun deletePost(postId: String) {
        db.collection("feedPosts")
            .document(postId)
            .delete()
            .await()
    }

    fun observeFeed(currentUser: UserProfile, onData: (List<FeedPost>) -> Unit) {
        db.collection("feedPosts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onData(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(FeedPost::class.java)
                    post?.id = doc.id
                    post
                }

                val visiblePosts = posts.filter { post ->
                    post.visibility == "public" ||
                            post.userId == currentUser.userId ||
                            (post.visibility == "friends" && currentUser.friends.contains(post.userId))
                }

                onData(visiblePosts)
            }
    }

    fun observePublicFeed(onData: (List<FeedPost>) -> Unit) {
        db.collection("feedPosts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onData(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(FeedPost::class.java)
                    post?.id = doc.id
                    post
                }.filter { post ->
                    post.visibility == "public"
                }

                onData(posts)
            }
    }
}
