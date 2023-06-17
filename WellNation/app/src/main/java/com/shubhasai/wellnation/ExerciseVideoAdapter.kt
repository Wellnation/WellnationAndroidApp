package com.shubhasai.wellnation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ExerciseVideoAdapter(private val data: List<String>,private val context: Context) : RecyclerView.Adapter<ExerciseVideoAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: CardView = itemView.findViewById(R.id.btnVideoPlayer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.videoplayersingleview, parent, false))
        itemView.textView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data[itemView.adapterPosition]))
            context.startActivity(intent)
        }
        return itemView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }
}