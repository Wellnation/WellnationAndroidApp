package com.shubhasai.wellnation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.FragmentProfileeditBinding

class ProfileeditFragment : Fragment() {
    private lateinit var binding: FragmentProfileeditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileeditBinding.inflate(layoutInflater)
        readUserDetails()
        binding.btnSaveVitals.setOnClickListener {
            saveuserdata()
        }
        binding.btnAddDiseases.setOnClickListener {
            setdiseases()
        }
        return binding.root
    }
    fun readUserDetails(){
        val firestore = FirebaseFirestore.getInstance()

        val userDetailsCollectionRef = firestore.collection("publicusers")
        userDetailsCollectionRef.document(Userinfo.userid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(userdetails::class.java)
                if (user != null) {
                    Userinfo.userid = user.userid
                    Userinfo.email = user.email
                    Userinfo.uname = user.name
                    Userinfo.phonenumber = user.phone
                }
                binding.etemergencycontact.setText(user?.emergencyNumber)
                binding.etState.setText(user?.address?.state)
                binding.etDistrict.setText(user?.address?.district)
                binding.etLocality.setText(user?.address?.locality)
                binding.etPincode.setText(user?.address?.pincode)
                binding.etGender.setText(user?.gender)
                binding.etDob.setText(user?.dob)

            }
        }.addOnFailureListener { exception ->
            Log.w("Firebase Execption", "Error getting user details", exception)
        }
        userDetailsCollectionRef.document(Userinfo.userid).collection("vitals").document("info").get().addOnSuccessListener { documents ->
            if (documents.exists()) {
                val vitals = documents.toObject(vitals::class.java)
                binding.etBloodGroup.setText(vitals?.bloodgroup)
                binding.Birthmark.setText(vitals?.birthmark)
                binding.etHeight.setText(vitals?.height)
                binding.etWeight.setText(vitals?.weight)
                getdiseaseslist()
            }
        }.addOnFailureListener { exception ->
            Log.w("Firebase Execption", "Error getting user details", exception)
        }

    }
    fun saveuserdata(){
        val bloodGroup = binding.etBloodGroup.text.toString().uppercase()
        val birthmark = binding.Birthmark.text.toString()
        val height = binding.etHeight.text.toString()
        val weight = binding.etWeight.text.toString()
        val emergencyContact = binding.etemergencycontact.text.toString()
        val state = binding.etState.text.toString()
        val district = binding.etDistrict.text.toString()
        val locality = binding.etLocality.text.toString()
        val pincode = binding.etPincode.text.toString()
        val gender = binding.etGender.text.toString().lowercase()
        val dob = binding.etDob.text.toString()
        val firestore = FirebaseFirestore.getInstance()
        val userDetailsCollectionRef = firestore.collection("publicusers")
        val vitals = vitals(height, weight, bloodGroup, birthmark)
        val address = address(state,district,locality,pincode)
        val hashMap:HashMap<String,Any> = HashMap()
        val userdetails = userdetails(name = Userinfo.uname, email = Userinfo.email, phone = Userinfo.phonenumber,gender = gender, dob = dob, emergencyNumber = emergencyContact, address = address, userid = Userinfo.userid, familyId = Userinfo.familyId)
        userDetailsCollectionRef.document(Userinfo.userid).collection("vitals").document("info").set(vitals)
        userDetailsCollectionRef.document(Userinfo.userid).set(userdetails)

    }
    fun getdiseaseslist(){
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info")
        db.get().addOnSuccessListener {
            val diseases = it.toObject(vitals::class.java)?.diseases
            if (diseases != null) {
                for (disease in diseases) {
                    binding.tvDiseases.append(disease.nameofdisease + ",")
                }
            }
        }
    }
    fun setdiseases(){
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info")
        val diseases = ArrayList<disease>()
        val text = binding.tvDiseases.text.toString()
        text.split(",").forEach {
            diseases.add(disease(it))
        }
        db.update("diseases", diseases)
    }
}