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
import com.shubhasai.wellnation.databinding.FragmentMedicineBinding

class MedicineFragment : Fragment() {
    private lateinit var binding:FragmentMedicineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicineBinding.inflate(layoutInflater)
        getmedicinedata()
        return binding.root
    }
    fun getmedicinedata(){
        if(Userinfo.userid != ""){
            binding.rvMedicine.layoutManager = LinearLayoutManager(activity)
            val medicinelist:ArrayList<medicines> = ArrayList()
            val db = Firebase.firestore
            val collectionRef = db.collection("appointments")
            collectionRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val appointment = document.toObject(AppointmentData::class.java)
                        if (appointment.pid == Userinfo.userid){
                            for (medicine in appointment.medicine){
                                medicinelist.add(medicine)
                            }
                        }
                    }
                    binding.rvMedicine.adapter = MedicinesAdapter(activity as Context?,medicinelist)
                }
                .addOnFailureListener { exception ->
                    Log.d("Firebase", "Error getting Hospitals documents: ", exception)
                }
        }

    }
}