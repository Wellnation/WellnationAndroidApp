package com.shubhasai.wellnation

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
import com.shubhasai.wellnation.databinding.FragmentTestbookingBinding

class TestbookingFragment : Fragment(),TestAdapter.TestClicked {
    private lateinit var binding: FragmentTestbookingBinding
    val testlist:ArrayList<tests> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTestbookingBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onbooknowtestclicked(testlist: tests) {
        val db = FirebaseFirestore.getInstance().collection("testHistory")
        val testdata = testbookingdata(hid = testlist.hid, hname = testlist.hospitalname, patientid = Userinfo.userid, pname = Userinfo.uname, tid = testlist.testid, tname = testlist.testname)
        db.document().set(testdata)
    }
}