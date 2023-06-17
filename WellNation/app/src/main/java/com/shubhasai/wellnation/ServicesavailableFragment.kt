package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentServicesavailableBinding

class ServicesavailableFragment : Fragment(),DoctorsAdapter.DrClicked {
    private lateinit var binding: FragmentServicesavailableBinding
    val testlist:ArrayList<DoctorInfo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServicesavailableBinding.inflate(layoutInflater)
        getavailabledocs()
        return binding.root
    }
    fun getavailabledocs(){
        binding.rvhospitaltests.layoutManager = LinearLayoutManager(activity)
        val db = Firebase.firestore
        val collectionRef = db.collection("doctors")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tests = document.toObject(DoctorInfo::class.java)
                    for (hid in tests.hids){
                        if(hid==Userinfo.hospitalclicked){
                            testlist.add(tests)
                            break
                        }
                    }
                }
                binding.rvhospitaltests.adapter = DoctorsAdapter(activity,testlist,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }

}