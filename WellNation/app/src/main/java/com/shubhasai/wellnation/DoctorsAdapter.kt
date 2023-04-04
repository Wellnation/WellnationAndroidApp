package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DoctorsAdapter(private val context: Context?, val doctorslist: ArrayList<DoctorInfo>):
    RecyclerView.Adapter<DoctorsAdapter.ViewHolder>() {
    class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview) {
        val cardview: CardView = itemView.findViewById(R.id.cardViewdoctor)
        val nameTextView: TextView = itemView.findViewById(R.id.tvdrname)
        val speciality: TextView = itemView.findViewById(R.id.dr_specialitty)
        val drrating: TextView = itemView.findViewById(R.id.tv_drrating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsAdapter.ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.doctors_itemview, parent, false))
        viewholder.cardview.setOnClickListener {

        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dr = doctorslist[position]
        holder.speciality.text = dr.speciality
        holder.nameTextView.text = dr.name
        holder.drrating.text = dr.ratings.toString()
    }

    override fun getItemCount(): Int {
        return doctorslist.size
    }

}