package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentBookappointmentBinding

class BookappointmentFragment : Fragment() {
    private lateinit var binding:FragmentBookappointmentBinding
    val args:BookappointmentFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentBookappointmentBinding.inflate(layoutInflater)
        binding.btnbookappointment.setOnClickListener {
            bookappointment()
        }
        return binding.root
    }

    fun bookappointment(){
        val medicine:ArrayList<medicines> = ArrayList()
        val booking_details = AppointmentData("",args.hospitalid,medicine,Userinfo.userid, remark = "", reqtime = Timestamp.now(), shldtime = Timestamp.now(), status = false, symptoms = binding.etsymptoms.text.toString())
        val db =  Firebase.firestore.collection("appointments")
        db.document().set(booking_details)
    }
}