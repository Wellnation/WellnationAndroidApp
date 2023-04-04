package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class HospitalAdapter(private val context: Context?,val hospitals: ArrayList<HospitalList>, val listener:HospitalClicked) :
    RecyclerView.Adapter<HospitalAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardview:CardView = itemView.findViewById(R.id.cardViewsiglehospital)
        val nameTextView: TextView = itemView.findViewById(R.id.tvhospital_name)
        val addressTextView: TextView = itemView.findViewById(R.id.tvhospitaladdress)
        val phoneTextView: TextView = itemView.findViewById(R.id.tv_contactnumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.hospital_item, parent, false))
        viewholder.cardview.setOnClickListener {
            listener.onHospitalClicked(hospitals[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hospital = hospitals[position]
        holder.nameTextView.text = hospital.name
        holder.addressTextView.text = hospital.address.state + ", " + hospital.address.district + ", "+hospital.address.locality+"," + hospital.address.pincode
        holder.phoneTextView.text = hospital.phone
    }

    override fun getItemCount(): Int {
        return hospitals.size
    }
    interface HospitalClicked {
        fun onHospitalClicked(hospital: HospitalList){

        }
    }
}


