package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.shubhasai.wellnation.utils.dateandtimeformat

class HospitalisationAdapter(private val context: Context, private val hospitalisations: ArrayList<hospitalisationdata>,val listener:HospitalisationClicked) :
    RecyclerView.Adapter<HospitalisationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewHolder(LayoutInflater.from(context).inflate(R.layout.hospitalisation_item, parent, false))
        view.hcard.setOnClickListener {
            listener.onHospitalisationClicked(hospitalisations[view.adapterPosition])
        }
        return view
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hospitalisation = hospitalisations[position]

        holder.hosName.text = hospitalisation.hName
        holder.sdate.text = "From: "+dateandtimeformat.formatFirebaseDateTime(hospitalisation.dateAdmitted)
        holder.edate.text = "Till: "+dateandtimeformat.formatFirebaseDateTime(hospitalisation.dateReleased)
    }

    override fun getItemCount(): Int {
        return hospitalisations.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hosName: TextView = itemView.findViewById(R.id.hosName)
        val sdate: TextView = itemView.findViewById(R.id.sdate)
        val hcard:CardView = itemView.findViewById(R.id.hcard)
        val edate: TextView = itemView.findViewById(R.id.edate)
    }
    interface HospitalisationClicked {
        fun onHospitalisationClicked(data:hospitalisationdata){

        }
    }
}
