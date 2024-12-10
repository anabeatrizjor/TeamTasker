package com.example.teamtasker.data

import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtasker.R
import com.example.teamtasker.data.ChatMessage

class ChatAdapter(private val messageList: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messageList[position]
        holder.messageTextView.text = message.text
        holder.userNameTextView.text = message.userName

        val timestamp = message.timestamp
        val time = DateFormat.format("HH:mm", timestamp).toString()
        holder.messageTimeTextView.text = time

        if (message.isSentByUser) {
            holder.messageTextView.setBackgroundResource(R.drawable.message_background_user)
            holder.messageTextView.layoutParams = (holder.messageTextView.layoutParams as LinearLayout.LayoutParams).apply {
                gravity = Gravity.END
            }
            holder.messageTimeTextView.layoutParams = (holder.messageTimeTextView.layoutParams as LinearLayout.LayoutParams).apply {
                gravity = Gravity.END
            }
        } else {
            holder.messageTextView.setBackgroundResource(R.drawable.message_background_other)
            holder.messageTextView.layoutParams = (holder.messageTextView.layoutParams as LinearLayout.LayoutParams).apply {
                gravity = Gravity.START
            }
            holder.messageTimeTextView.layoutParams = (holder.messageTimeTextView.layoutParams as LinearLayout.LayoutParams).apply {
                gravity = Gravity.START
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val messageTimeTextView: TextView = itemView.findViewById(R.id.messageTimeTextView)
    }
}
