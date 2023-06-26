package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shubhasai.wellnation.databinding.FragmentHospitalisationdetailsBinding
class HospitalisationdetailsFragment : Fragment() {
    private lateinit var binding:FragmentHospitalisationdetailsBinding
    private val arg:HospitalisationdetailsFragmentArgs  by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHospitalisationdetailsBinding.inflate(layoutInflater)
        getlogs()
        return binding.root
    }
    fun getlogs(){
        val logs:ArrayList<hlogs> = ArrayList()
        val db  = FirebaseFirestore.getInstance().collection("admissions").document(arg.id).collection("logs").orderBy("logDate",Query.Direction.DESCENDING).get().addOnSuccessListener {
            for (log in it.documents){
                val data = log.toObject(hlogs::class.java)
                if (data != null) {
                    logs.add(data)
                }
            }
            binding.logsList.layoutManager = LinearLayoutManager(activity)
            binding.logsList.adapter = activity?.let { it1 -> HospitalisationLogsAdapter(it1,logs) }
        }
    }
}