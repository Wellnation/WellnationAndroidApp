package com.shubhasai.wellnation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentRoomsBinding

class RoomsFragment : Fragment() {
    private lateinit var binding:FragmentRoomsBinding
    val roomlist:ArrayList<room> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRoomsBinding.inflate(layoutInflater)
        roomdetails()
        return binding.root
    }


    fun roomdetails(){
        binding.rvhospitalrooms.layoutManager = LinearLayoutManager(activity)
        val db = Firebase.firestore
        val collectionRef = db.collection("rooms")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val rooms = document.toObject(room::class.java)
                    if(rooms.hospital == Userinfo.hospitalclicked){
                        roomlist.add(rooms)
                    }
                }
                binding.rvhospitalrooms.adapter = RoomAdapter(activity,roomlist)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }

}