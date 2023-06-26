package com.shubhasai.wellnation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shubhasai.wellnation.utils.dateandtimeformat

class DoctorsLogAdapter(private val doctors: ArrayList<doc>) :
    RecyclerView.Adapter<DoctorsLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.doctorslog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.bind(doctor)
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doctorsNameTextView: TextView = itemView.findViewById(R.id.tvDoctorsName)
        private val doctorsRemarkTextView: TextView = itemView.findViewById(R.id.tvDoctorsRemark)
        private val timeTextView: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(doctor: doc) {
            doctorsNameTextView.text = doctor.doctorName
            doctorsRemarkTextView.text = doctor.remark
            timeTextView.text = dateandtimeformat.formatFirebaseDateTime(doctor.time)
        }
    }
}
