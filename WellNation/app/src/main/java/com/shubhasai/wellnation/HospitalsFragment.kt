package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentHospitalsBinding

class HospitalsFragment : Fragment(),HospitalAdapter.HospitalClicked{
    private lateinit var binding: FragmentHospitalsBinding
    val hospitalList:ArrayList<HospitalList> = ArrayList()
    private lateinit var adapter :HospitalAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        adapter = HospitalAdapter(activity as Context?,hospitalList,this)
        binding = FragmentHospitalsBinding.inflate(layoutInflater)
        binding.hospitalList.layoutManager = LinearLayoutManager(activity)
        binding.hospitalList.adapter = adapter
        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
        }
        getallhospitals()
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (query != null) {
                    gethospital(query.lowercase())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                if (newText != null) {
                    gethospital(newText.lowercase())
                }
                return false
            }
        })
        return binding.root

    }

    override fun onHospitalClicked(hospital: HospitalList) {
        Userinfo.hospitalclicked = hospital.uid
        val direction = HospitalsFragmentDirections.actionHospitalsFragmentToHospitaldetailsFragment(hospital.uid)
        findNavController().navigate(direction)

    }
    fun getallhospitals(){
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val hospital = document.toObject(HospitalList::class.java)
                    hospitalList.add(hospital)
                }
                Log.d("Firebase", "Hospitals documents: ${hospitalList.size}")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
    fun gethospital(text:String){
        hospitalList.clear()
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val hospital = document.toObject(HospitalList::class.java)
                    if(hospital.name.lowercase().contains(text)||hospital.address.locality.lowercase().contains(text)||hospital.address.district.lowercase().contains(text)||hospital.address.state.lowercase().contains(text)){
                        hospitalList.add(hospital)
                    }
                }
                adapter.notifyDataSetChanged()
                Log.d("Firebase", "Hospitals documents: ${hospitalList.size}")

            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
}