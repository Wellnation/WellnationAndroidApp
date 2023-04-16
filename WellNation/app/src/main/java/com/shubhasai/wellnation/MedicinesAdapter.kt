package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MedicinesAdapter(private val context: Context?, val medicinelist:ArrayList<medicines> = ArrayList()) : RecyclerView.Adapter<MedicinesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val medicinecardview: CardView = itemView.findViewById(R.id.cardViewmedicine)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_medicinename)
        val medicinetime: TextView = itemView.findViewById(R.id.medicine_time)
        val medicine_remark: TextView = itemView.findViewById(R.id.tv_remark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.medicine_item, parent, false))
        viewholder.medicinecardview.setOnClickListener {

        }
        return viewholder
    }

    override fun getItemCount(): Int {
        return medicinelist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = medicinelist[position]
        holder.nameTextView.text = medicine.name
        holder.medicine_remark.text = medicine.remark
        holder.medicinetime.text = ""
        for (time in medicine.time){
            holder.medicinetime.text = holder.medicinetime.text as String + time.hr + ":"+ time.min + " "
        }
    }

}
