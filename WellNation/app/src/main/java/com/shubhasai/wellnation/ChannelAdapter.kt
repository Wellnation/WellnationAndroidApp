package com.shubhasai.wellnation

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChannelAdapter(private val context: Context, private val channels: ArrayList<Channel> = ArrayList(),val listener:ChannelClicked ) :
    RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.channel_singleview, parent, false)
        val viewholder = ChannelViewHolder(itemView)
        viewholder.itemView.setOnClickListener {
            listener.onchannelClicked(channels[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.bind(channel)
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    inner class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val createdByTextView: TextView = itemView.findViewById(R.id.createdByTextView)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.createdAtTextView)
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(channel: Channel) {
            createdByTextView.text = "Created by: ${channel.name}"
            createdAtTextView.text = "Created at: ${channel.createdAt.toDate().toString()}"
            messageTextView.text =  Html.fromHtml(channel.message, Html.FROM_HTML_MODE_LEGACY).toString()
        }
    }
    interface ChannelClicked{
        fun onchannelClicked(channel:Channel){

        }
    }
}
