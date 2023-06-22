package com.shubhasai.wellnation

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentAppointmentBinding
import com.shubhasai.wellnation.databinding.FragmentDepartmentBinding
import com.shubhasai.wellnation.databinding.FragmentMedicineBinding
import de.coldtea.smplr.smplralarm.alarmNotification
import de.coldtea.smplr.smplralarm.channel
import de.coldtea.smplr.smplralarm.smplrAlarmSet


class AppointmentFragment : Fragment(),AppointmentAdapter.ApptClicked {
    private lateinit var binding: FragmentAppointmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBinding.inflate(layoutInflater)
        getmyappointment()
        return binding.root
    }

    fun getmyappointment(){
        binding.rvMyappointments.layoutManager = LinearLayoutManager(activity)
        val db = Firebase.firestore
        val appotlists : ArrayList<AppointmentData> =  ArrayList()
        val collectionRef = db.collection("appointments").orderBy("shldtime", Query.Direction.DESCENDING)
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val appointment = document.toObject(AppointmentData::class.java)
                    if (appointment.pid == Userinfo.userid){
                        appotlists.add(appointment)
                    }
                }
                binding.rvMyappointments.adapter = AppointmentAdapter(activity as Context?,appotlists,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }

    override fun onviewmoreclicked(appt: AppointmentData) {
        if(appt.status){
            val directions = AppointmentFragmentDirections.actionAppointmentFragmentToAppointmentdetailsFragment(appt.apptId)
            findNavController().navigate(directions)
        }
        else if(appt.shldtime.toDate().after(Timestamp.now().toDate()) && !appt.onlinemode){
            Toast.makeText(activity,"UPCOMING",Toast.LENGTH_SHORT).show()
        }
        else if(appt.shldtime.toDate().after(Timestamp.now().toDate()) && appt.onlinemode){
            Toast.makeText(activity,"Meet Code has been sent via Notification",Toast.LENGTH_SHORT).show()
            val url = "https://wellnation.vercel.app/patients/${Userinfo.userid}/chat" // Replace with your desired URL

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the exception as needed
            }
        }
        else if (appt.shldtime.toDate().before(Timestamp.now().toDate()) && !appt.status){
            Toast.makeText(activity,"TO BE SCHEDULED",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(activity,"MISSED",Toast.LENGTH_SHORT).show()
        }
    }
}