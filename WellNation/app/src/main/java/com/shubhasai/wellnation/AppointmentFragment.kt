package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentAppointmentBinding
import com.shubhasai.wellnation.databinding.FragmentDepartmentBinding
import com.shubhasai.wellnation.databinding.FragmentMedicineBinding


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
        val collectionRef = db.collection("appointments")
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

    }
}