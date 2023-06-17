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
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentDepartmentBinding


class DepartmentFragment : Fragment(),DepartmentAdapter2.DeptClicked,DoctorsAdapter.DrClicked,TestAdapter.TestClicked {
    private lateinit var binding: FragmentDepartmentBinding
    val testlist:ArrayList<tests> = ArrayList()
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
        binding.rvhospitaldepartament.layoutManager = GridLayoutManager(activity,2)
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
                val hid:ArrayList<HospitalList> = ArrayList()
                binding.rvhospitaldepartament.adapter =
                    DepartmentAdapter2(activity as Context?, deptList,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
        getavailabletests()
        getroomdetails()
        val headerRow = TableRow(activity)
        headerRow.addView(createHeaderTextView("Name"))
        headerRow.addView(createHeaderTextView("Cost"))
        headerRow.addView(createHeaderTextView("Availability"))
        headerRow.addView(createHeaderTextView("Description"))
        binding.tableLayout.addView(headerRow)
        return binding.root
    }
    private fun createHeaderTextView(text: String): TextView {
        val textView = TextView(activity)
        textView.text = text
        textView.setPadding(16, 16, 16, 16)
        // Optional: Customize the header background
        return textView
    }

    private fun createDataTextView(text: String): TextView {
        val textView = TextView(activity)
        textView.text = text
        textView.setPadding(16, 16, 16, 16)
        // Optional: Customize the data cell background

        // Set the layout parameters
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        textView.layoutParams = layoutParams

        return textView
    }
    fun getavailabletests(){
        binding.rvtests.layoutManager = GridLayoutManager(activity,2)
        val db = Firebase.firestore
        val collectionRef = db.collection("tests")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tests = document.toObject(tests::class.java)
                    if(tests.hid == Userinfo.hospitalclicked){
                        testlist.add(tests)
                    }
                }
                binding.rvtests.adapter = TestAdapter(activity,testlist,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
    override fun onDeptClicked(dept: DepartmentData) {
        Userinfo.departmentclicked = dept.name
        getdoctorsdata(dept)
    }

    override fun onbooknowclicked(dept: DepartmentData) {
        Userinfo.departmentclicked = dept.name
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
        rv.adapter = DoctorsAdapter(activity,doctors,this)
        builder?.setContentView(dialogLayout)
        builder?.show()
    }

    override fun onbooknowclicked(dr: DoctorInfo) {
        val directions = HospitaldetailsFragmentDirections.actionHospitaldetailsFragmentToBookappointmentFragment(Userinfo.hospitalclicked,dr.uid)
        findNavController().navigate(directions)
    }

    override fun onbooknowtestclicked(testlist: tests) {
        val db = FirebaseFirestore.getInstance().collection("testHistory")
        val testdata = testbookingdata(hid = testlist.hid, hname = testlist.hospitalname, patientid = Userinfo.userid, pname = Userinfo.uname, tid = testlist.testid, tname = testlist.testname)
        db.document().set(testdata)
    }
    fun getroomdetails(){
        val typesArray:ArrayList<String> = ArrayList()
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.document(Userinfo.hospitalclicked).get().addOnSuccessListener{
            if (it.exists()){
                val hospitaldetails = it.toObject(HospitalList::class.java)
                if (hospitaldetails != null) {
                    for (type in hospitaldetails.roomTypes){
                        getavailability(type)
                    }
                }

            }
        }
    }
    fun getavailability(type:rooms){
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.document(Userinfo.hospitalclicked).collection("beds").whereEqualTo("type",type.type).whereEqualTo("status",true).get().addOnSuccessListener{
            val dataRow = TableRow(activity)
            dataRow.addView(createDataTextView(type.type))
            dataRow.addView(createDataTextView(type.cost))
            dataRow.addView(createDataTextView(it.documents.size.toString()))
            dataRow.addView(createDataTextView(type.description))
            binding.tableLayout.addView(dataRow)
        }
    }
}