package com.shubhasai.wellnation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FamilyMemberAdapter(private val familyMembers: ArrayList<userdetails>) :
    RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textContact: TextView = itemView.findViewById(R.id.textContact)
        val call: Button = itemView.findViewById(R.id.btnEdit)
        val bookappointment: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.family_member_singleview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val familyMember = familyMembers[position]

        holder.textName.text = familyMember.name
        holder.textContact.text = familyMember.phone

        holder.call.setOnClickListener {
            // Handle edit button click
        }

        holder.bookappointment.setOnClickListener {
            // Handle delete button click
        }
    }

    override fun getItemCount(): Int {
        return familyMembers.size
    }
}
