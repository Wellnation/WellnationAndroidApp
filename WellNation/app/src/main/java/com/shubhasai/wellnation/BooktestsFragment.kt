package com.shubhasai.wellnation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentBooktestsBinding

class BooktestsFragment : Fragment(),TestAdapter.TestClicked {
    private lateinit var binding:FragmentBooktestsBinding
    val testlist:ArrayList<tests> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooktestsBinding.inflate(layoutInflater)
        getavailabletests()
        return binding.root
    }
    fun getavailabletests(){
        binding.rvtests.layoutManager = GridLayoutManager(activity,2)
        val db = Firebase.firestore
        val collectionRef = db.collection("tests")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tests = document.toObject(tests::class.java)
                    testlist.add(tests)
                }
                binding.rvtests.adapter = TestAdapter(activity,testlist,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
    override fun onbooknowtestclicked(testlist: tests) {
        val db = FirebaseFirestore.getInstance().collection("testHistory")
        val testdata = testbookingdata(hid = testlist.hid, hname = testlist.hospitalname, patientid = Userinfo.userid, pname = Userinfo.uname, tid = testlist.testid, tname = testlist.testname)
        db.document().set(testdata)
    }
}