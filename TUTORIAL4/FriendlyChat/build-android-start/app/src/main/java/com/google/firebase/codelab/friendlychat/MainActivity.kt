/**
 * Friendly Chat - versão sem Firebase Storage.
 *
 * Esta versão mantém:
 * - Firebase Authentication para login;
 * - Firebase Realtime Database para mensagens de texto.
 *
 * A parte de envio de imagens foi removida porque o Firebase Storage
 * pode exigir upgrade para o plano Blaze em projetos novos.
 */
package com.google.firebase.codelab.friendlychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.codelab.friendlychat.databinding.ActivityMainBinding
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: FriendlyMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)

        val options = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
            .setQuery(messagesRef, FriendlyMessage::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = FriendlyMessageAdapter(options, getUserName())

        binding.progressBar.visibility = ProgressBar.INVISIBLE

        manager = LinearLayoutManager(this)
        manager.stackFromEnd = true

        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )

        binding.messageEditText.addTextChangedListener(
            MyButtonObserver(binding.sendButton)
        )

        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()

            if (text.isNotBlank()) {
                sendMessage(
                    text = text,
                    userName = getUserName(),
                    photoUrl = getPhotoUrl()
                )

                binding.messageEditText.setText("")
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName ?: user.email ?: ANONYMOUS
        } else {
            ANONYMOUS
        }
    }

    private fun sendMessage(
        text: String,
        userName: String?,
        photoUrl: String?
    ) {
        val friendlyMessage = FriendlyMessage(
            text = text,
            name = userName,
            photoUrl = photoUrl,
            imageUrl = null
        )

        Log.d(TAG, "Writing to DB: $MESSAGES_CHILD / $friendlyMessage")
        db.reference.child(MESSAGES_CHILD).push().setValue(friendlyMessage)
    }

    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
    }
}
