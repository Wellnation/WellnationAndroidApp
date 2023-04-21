package com.shubhasai.wellnation

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BlogAdapter(private val context: Context?,val blogs: ArrayList<blogdata>, val listener:BlogClicked) :
    RecyclerView.Adapter<BlogAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardview:CardView = itemView.findViewById(R.id.blog_item)
        val title: TextView = itemView.findViewById(R.id.blog_title)
        val author: TextView = itemView.findViewById(R.id.blog_author)
        val date: TextView = itemView.findViewById(R.id.blog_date)
        val image:ImageView = itemView.findViewById(R.id.blog_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false))
        viewholder.cardview.setOnClickListener {
            listener.onBlogClicked(blogs[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blogs = blogs[position]
        holder.title.text = blogs.title
        holder.author.text = blogs.drname
        holder.date.text = blogs.timestamp.toDate().toString()
        Glide.with(context!!).load(blogs.imageurl).into(holder.image)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }
    interface BlogClicked {
        fun onBlogClicked(blog: blogdata){

        }
    }
}


