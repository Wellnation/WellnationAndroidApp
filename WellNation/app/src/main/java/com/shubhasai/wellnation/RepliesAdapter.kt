package com.shubhasai.wellnation

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepliesAdapter(private val replies: ArrayList<replies>) :
    RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.replies_single_item, parent, false)
        return ReplyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        holder.bind(reply)
    }

    override fun getItemCount(): Int {
        return replies.size
    }

    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val createdByTextView: TextView = itemView.findViewById(R.id.createdByTextView)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.createdAtTextView)
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(reply: replies) {
            createdByTextView.text = "Created by: ${reply.name}"
            createdAtTextView.text = "Created at: ${reply.createdAt.toDate()}"
            messageTextView.text =  Html.fromHtml(reply.message, Html.FROM_HTML_MODE_LEGACY).toString()
        }
    }
}
