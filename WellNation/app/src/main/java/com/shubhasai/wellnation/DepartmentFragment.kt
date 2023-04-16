package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentDepartmentBinding


class DepartmentFragment : Fragment(),DepartmentAdapter.DeptClicked{
    private lateinit var binding: FragmentDepartmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDepartmentBinding.inflate(layoutInflater)
        val deptList: ArrayList<DepartmentData> = ArrayList()
        binding.rvhospitaldepartament.layoutManager = LinearLayoutManager(activity)
        val db = Firebase.firestore
        Log.d("hid",Userinfo.hospitalclicked)
        val collectionRef =
            db.collection("users").document(Userinfo.hospitalclicked).collection("departments")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val hospital = document.toObject(DepartmentData::class.java)
                    deptList.add(hospital)
                }
                binding.rvhospitaldepartament.adapter =
                    DepartmentAdapter(activity as Context?, deptList, this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
        return binding.root
    }

    override fun onDeptClicked(dept: DepartmentData) {
        getdoctorsdata(dept)
    }

    override fun onbooknowclicked(dept: DepartmentData) {
        Toast.makeText(activity,"Book Now Clicked",Toast.LENGTH_SHORT).show()
        val directions = HospitaldetailsFragmentDirections.actionHospitaldetailsFragmentToBookappointmentFragment(Userinfo.hospitalclicked,"")
        findNavController().navigate(directions)
    }
    fun getdoctorsdata(dept: DepartmentData){
        val doctordata: ArrayList<DoctorInfo> = ArrayList()
        val doctorIds:ArrayList<String> = arrayListOf()
        for (doctor in dept.doctors){
            doctorIds.add(doctor.uid)
        }
        val db = Firebase.firestore
        val collectionRef = db.collection("doctors")
        if (doctorIds.isNotEmpty()){
            val query = collectionRef.whereIn(FieldPath.documentId(), doctorIds)

            query.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val doctorsdetails = document.toObject(DoctorInfo::class.java)
                    if (doctorsdetails != null) {
                        doctordata.add(doctorsdetails)
                    }
                }
                doctorslist(doctordata)
            }.addOnFailureListener { exception ->
                // Handle any errors that occur
            }
            Toast.makeText(activity,"Available Doctors",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(activity,"No Available Doctors",Toast.LENGTH_SHORT).show()
        }

    }
    fun doctorslist(doctors:ArrayList<DoctorInfo>){
        val builder = activity?.let { BottomSheetDialog(it) }
        val  inflate = layoutInflater
        val dialogLayout = inflate.inflate(R.layout.departmentwisedoctorlist,null)
        val rv = dialogLayout.findViewById<RecyclerView>(R.id.rvdrdrawerlist)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = DoctorsAdapter(activity,doctors)
        builder?.setContentView(dialogLayout)
        builder?.show()
    }

}