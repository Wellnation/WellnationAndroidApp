package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.GravityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var userData: HashMap<String, Any> = HashMap()
    private lateinit var binding: FragmentProfileBinding
    private var firebaseauth: FirebaseAuth?=null
    var curuser : FirebaseUser? = firebaseauth?.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        val adapter = ProfilepagerAdapter(childFragmentManager,lifecycle)
        binding.profileViewpager.adapter = adapter
        TabLayoutMediator(binding.profiletab,binding.profileViewpager){tab,position->
            when(position){
                0->{
                    tab.text="Vitals"
                }
                1->{
                    tab.text="Medicines"
                }
                2->{
                    tab.text="Appointments"
                }
            }
        }.attach()
        readUserDetails()
        return binding.root
    }

    fun readUserDetails(){
        val firestore = FirebaseFirestore.getInstance()

        val userDetailsCollectionRef = firestore.collection("publicusers")
        firebaseauth = FirebaseAuth.getInstance()
        var curuser : FirebaseUser? = firebaseauth?.currentUser
        if (curuser!=null){
            Userinfo.userid = curuser.uid
            userDetailsCollectionRef.document(Userinfo.userid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val userdata = document.toObject<userdetails>()
                    binding.userName.text = userdata?.name
                    binding.userAddress.text = userdata?.address?.state+", "+userdata?.address?.district+", "+userdata?.address?.locality+", "+userdata?.address?.pincode
                    binding.userContactNumber.text = "Mobile: "+userdata?.phone
                    binding.userEmergencyContact.text = "Emergency Contact: "+userdata?.emergencyNumber
                    binding.userEmail.text = userdata?.email
                    if (userdata?.gender=="male"){
                        binding.profileIcon.setAnimation(R.raw.maleicon)
                        binding.profileIcon.playAnimation()
                    }
                    else if (userdata?.gender == "female"){
                        binding.profileIcon.setAnimation(R.raw.femaleicon)
                        binding.profileIcon.playAnimation()
                    }
                    else{
                        binding.profileIcon.setAnimation(R.raw.profileicon)
                        binding.profileIcon.playAnimation()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("Firebase Execption", "Error getting user details", exception)
            }
        }


    }
}
