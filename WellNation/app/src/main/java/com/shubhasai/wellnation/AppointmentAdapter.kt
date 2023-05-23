package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppointmentAdapter(private val context: Context?, val appointment: ArrayList<AppointmentData>,val listener:ApptClicked ): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val apptcardview: CardView = itemView.findViewById(R.id.cardViewappt)
        val hospitaname: TextView = itemView.findViewById(R.id.tv_HospitalName)
        val doctorname: TextView = itemView.findViewById(R.id.tv_doctorname)
        val mode: TextView = itemView.findViewById(R.id.tv_mode)
        val timestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        val status:TextView = itemView.findViewById(R.id.status)
        val viewmore: Button = itemView.findViewById(R.id.btn_viewapptdetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = AppointmentAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false)
        )
        viewholder.viewmore.setOnClickListener {
            listener.onviewmoreclicked(appointment[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun getItemCount(): Int {
        return appointment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appt = appointment[position]
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.document(appt.hid).get().addOnSuccessListener {
            if (it.exists()){
                val hospitaldetails = it.toObject(HospitalList::class.java)
                if (hospitaldetails != null) {
                    holder.hospitaname.text = hospitaldetails.name
                }
            }
        }
        val dbdoc = Firebase.firestore
        if (appt.drid!=""){
            val doccollectionRef = dbdoc.collection("doctors")
            doccollectionRef.document(appt.drid).get().addOnSuccessListener {
                if (it.exists()){
                    val doctordetails = it.toObject(DoctorInfo::class.java)
                    if (doctordetails != null) {
                        holder.doctorname.text = doctordetails.name
                    }
                }
            }
        }
        if(appt.status){
            holder.status.text = "COMPLETED"
        }
        else if(appt.shldtime.toDate().after(Timestamp.now().toDate())){
            holder.status.text = "UPCOMING"
        }
        else if (appt.shldtime.toDate().before(Timestamp.now().toDate()) && !appt.status){
            holder.status.text = "TO BE SCHEDULED"
        }
        else{
            holder.status.text = "MISSED"
        }
        var mode = ""
        if (appt.onlinemode){
            mode = "Online Mode"
        }
        else{
            mode = "Offline Mode"
        }
        holder.mode.text = mode
        holder.timestamp.text = appt.shldtime.toDate().toString()

    }
    interface ApptClicked {
        fun onviewmoreclicked(appt: AppointmentData){

        }
    }
}