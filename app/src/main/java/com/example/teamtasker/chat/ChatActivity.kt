package com.example.teamtasker.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtasker.R
import com.example.teamtasker.data.ChatAdapter
import com.example.teamtasker.data.ChatMessage
import com.example.teamtasker.login.LoginActivity
import com.example.teamtasker.task.TaskActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatRecyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(messageList)
        chatRecyclerView.adapter = chatAdapter

        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val messageInput: EditText = findViewById(R.id.messageInput)
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                val message = ChatMessage(
                    text = messageText,
                    timestamp = System.currentTimeMillis(),
                    isSentByUser = true
                )
                sendMessage(message)
                messageInput.text.clear()
            }
        }

        val logout = findViewById<ImageView>(R.id.logoutButton)

        logout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("messages")


        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()


                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(ChatMessage::class.java)
                    message?.let { messageList.add(it) }
                }

                chatAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        val taskButton = findViewById<ImageView>(R.id.taskButtonChat)
        taskButton.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
        }


        window.statusBarColor = getColor(R.color.blueDark)
    }

    private fun sendMessage(message: ChatMessage) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("messages")
        val messageId = myRef.push().key ?: return

        val userName = FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) ?: "Anonymous"

        val messageWithUser = ChatMessage(
            text = message.text,
            timestamp = message.timestamp,
            isSentByUser = message.isSentByUser,
            userName = userName
        )

        myRef.child(messageId).setValue(messageWithUser)

        addMessageToList(messageWithUser)
    }


    private fun addMessageToList(message: ChatMessage) {
        messageList.add(message)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        findViewById<RecyclerView>(R.id.chatRecyclerView).scrollToPosition(messageList.size - 1)
    }
}
