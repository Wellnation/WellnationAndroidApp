package com.shubhasai.wellnation

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentTestBinding

class TestFragment : Fragment(),MyTestAdapter.ApptClicked {
    private lateinit var binding:FragmentTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTestBinding.inflate(layoutInflater)
        binding.pasttestsrv.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        getmytest()
        return binding.root
    }
    fun getmytest() {
        val db = Firebase.firestore
        val appotlists: ArrayList<testbookingdata> = ArrayList()
        val collectionRef =
            db.collection("testHistory").orderBy("shldtime", Query.Direction.ASCENDING)
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val appointment = document.toObject(testbookingdata::class.java)
                    if (appointment.patientid == Userinfo.userid && (appointment.shldtime.toDate()
                            .before(
                                Timestamp.now().toDate()
                            )) && appointment.attachment != ""
                    ) {
                        appotlists.add(appointment)
                    }
                }
                binding.pasttestsrv.adapter = MyTestAdapter(activity as Context?, appotlists, this)
                binding.testReportSummary.text = appotlists[0].llmOutput
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }

    override fun onupcomingTestClicked(test: testbookingdata) {
        binding.testReportSummary.text = test.llmOutput
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(test.attachment)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the exception as needed
        }
    }

    override fun onopenTestClicked(test: testbookingdata) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(test.attachment)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the exception as needed
        }
    }
}