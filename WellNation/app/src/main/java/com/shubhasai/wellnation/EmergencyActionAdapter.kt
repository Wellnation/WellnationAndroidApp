package com.shubhasai.wellnation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shubhasai.wellnation.emergencyactiondata
import com.shubhasai.wellnation.R
import com.shubhasai.wellnation.utils.dateandtimeformat.formatFirebaseDateTime

class EmergencyActionAdapter(private val emergencyActions: ArrayList<emergencyactiondata>) :
    RecyclerView.Adapter<EmergencyActionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emergency_action_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emergencyAction = emergencyActions[position]
        holder.bind(emergencyAction)
    }

    override fun getItemCount(): Int {
        return emergencyActions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAction: TextView = itemView.findViewById(R.id.tvAction)
        private val tvActionBy: TextView = itemView.findViewById(R.id.tvActionBy)
        private val tvActionAt: TextView = itemView.findViewById(R.id.tvActionAt)

        fun bind(emergencyAction: emergencyactiondata) {
            tvAction.text = emergencyAction.action
            tvActionBy.text = "By ${emergencyAction.senderName}"
            tvActionAt.text = formatFirebaseDateTime(emergencyAction.timestamp) // Adjust the timestamp format as needed
        }
    }
}
