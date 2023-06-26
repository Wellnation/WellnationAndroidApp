package com.shubhasai.wellnation

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.utils.dateandtimeformat.formatFirebaseDateTime

class UpcomingAppointmentAdapter(private val context: Context?, val appointment: ArrayList<AppointmentData>, val listener: UpcomingAppointmentAdapter.ApptClicked): RecyclerView.Adapter<UpcomingAppointmentAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val item:CardView = itemView.findViewById(R.id.upcomingAppointmentcardview)
        val hospitaname: TextView = itemView.findViewById(R.id.tvhospitalname)
        val department: TextView = itemView.findViewById(R.id.tvdepartment)
        val mode: TextView = itemView.findViewById(R.id.tvmode)
        val timestamp: TextView = itemView.findViewById(R.id.tvtime)
        val statusImage: ImageView = itemView.findViewById(R.id.statusImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.upcoming_appointment_item, parent, false)
        )
        viewholder.item.setOnClickListener {
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
        holder.department.text = appt.dept
//        val dbdoc = Firebase.firestore
//        if (appt.drid!=""){
//            val doccollectionRef = dbdoc.collection("doctors")
//            doccollectionRef.document(appt.drid).get().addOnSuccessListener {
//                if (it.exists()){
//                    val doctordetails = it.toObject(D                                                                                          octorInfo::class.java)
//                    if (doctordetails != null) {
//                        holder.department.text = doctordetails.name
//                    }
//                }
//            }
//        }
        var mode = ""
        if (appt.onlinemode){
            mode = "Online"
            holder.statusImage.setColorFilter(Color.rgb(41, 225, 56));
        }
        else{
            holder.statusImage.setColorFilter(Color.argb(1, 13, 71, 161))
            mode = "Offline"
        }
        holder.mode.text = mode
        holder.timestamp.text = formatFirebaseDateTime(appt.shldtime)

    }
    interface ApptClicked {
        fun onviewmoreclicked(appt: AppointmentData){

        }
    }
}