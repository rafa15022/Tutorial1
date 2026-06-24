package cm.a15022.athletelab.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.FeedRepository
import cm.a15022.athletelab.repository.UserRepository
import cm.a15022.athletelab.ui.adapters.FeedPostAdapter
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val feedRepository = FeedRepository()

    private lateinit var adapter: FeedPostAdapter

    private val postLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(requireContext(), getString(R.string.feed_updated), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        val recycler = view.findViewById<RecyclerView>(R.id.feedRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = FeedPostAdapter(
            currentUserId = uid,

            onEditClick = { post ->
                val intent = Intent(requireContext(), PostEditorActivity::class.java)
                intent.putExtra("userId", uid)
                intent.putExtra("postId", post.id)
                postLauncher.launch(intent)
            },

            onDeleteClick = { post ->
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_post_title))
                    .setMessage(getString(R.string.delete_post_message))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        lifecycleScope.launch {
                            try {
                                feedRepository.deletePost(post.id)

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.post_deleted),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.post_delete_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        )

        recycler.adapter = adapter

        userRepository.observeUser(uid) { user ->
            if (user != null) {
                feedRepository.observeFeed(user) { posts ->
                    adapter.submitList(posts)
                }

                view.findViewById<Button>(R.id.newPostButton).setOnClickListener {
                    val intent = Intent(requireContext(), PostEditorActivity::class.java)
                    intent.putExtra("userId", user.userId)
                    intent.putExtra("authorName", user.name)
                    intent.putExtra("favoriteSport", user.favoriteSport)

                    postLauncher.launch(intent)
                }
            }
        }
    }
}