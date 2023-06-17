package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.FragmentHospitalisationBinding

class HospitalisationFragment : Fragment(),HospitalisationAdapter.HospitalisationClicked {
   private lateinit var binding:FragmentHospitalisationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHospitalisationBinding.inflate(layoutInflater)
        getHospitalisationdata()
        return binding.root
    }
    fun getHospitalisationdata(){
        val Hdoc:ArrayList<hospitalisationdata> = ArrayList()
        val db = FirebaseFirestore.getInstance().collection("admissions")
        db.get().addOnSuccessListener {
            for (doc in it.documents){
                val data = doc.toObject(hospitalisationdata::class.java)
                if (data != null) {
                    if (data.pId == Userinfo.userid){
                        Hdoc.add(data)
                    }
                }
            }
            binding.hospitalisationList.layoutManager = GridLayoutManager(activity,2)
            binding.hospitalisationList.adapter =
                activity?.let { it1 -> HospitalisationAdapter(it1,Hdoc,this) }
        }
    }

    override fun onHospitalisationClicked(data:hospitalisationdata) {
        val directions = HospitalisationFragmentDirections.actionHospitalisationFragmentToHospitalisationdetailsFragment(data.apptId)
        findNavController().navigate(directions)
    }
}