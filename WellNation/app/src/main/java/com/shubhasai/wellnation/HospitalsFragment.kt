package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentHospitalsBinding

class HospitalsFragment : Fragment(),HospitalAdapter.HospitalClicked{
    private lateinit var binding: FragmentHospitalsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHospitalsBinding.inflate(layoutInflater)
        val hospitalList:ArrayList<HospitalList> = ArrayList()
        binding.hospitalList.layoutManager = LinearLayoutManager(activity)
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val hospital = document.toObject(HospitalList::class.java)
                    hospitalList.add(hospital)
                }
                Log.d("Firebase", "Hospitals documents: ${hospitalList.size}")
                binding.hospitalList.adapter = HospitalAdapter(activity as Context?,hospitalList,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
        return binding.root

    }

    override fun onHospitalClicked(hospital: HospitalList) {
        Userinfo.hospitalclicked = hospital.uid
        val direction = HospitalsFragmentDirections.actionHospitalsFragmentToHospitaldetailsFragment(hospital.uid)
        findNavController().navigate(direction)

    }
}