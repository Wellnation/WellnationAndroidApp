package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentBloodbankBinding

class BloodbankFragment : Fragment() {
    private lateinit var binding:FragmentBloodbankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBloodbankBinding.inflate(layoutInflater)
        getbloodbankdetails()
        return binding.root
    }
    fun getbloodbankdetails(){
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.document(Userinfo.hospitalclicked).collection("Bloodbank").document("Available").get().addOnSuccessListener {
            if (it.exists()){
                val bloodbankdata = it.toObject(bloodbankdata::class.java)
                if (bloodbankdata != null) {
                    binding.tvaplus.text = "A+: " + bloodbankdata.aplus
                    binding.tvaminus.text = "A-: " + bloodbankdata.aminus
                    binding.tvbplus.text = "B+: " + bloodbankdata.bplus
                    binding.tvbminus.text = "B-: " + bloodbankdata.bminus
                    binding.tvoplus.text = "O+: " + bloodbankdata.oplus
                    binding.tvominus.text = "O-: " + bloodbankdata.ominus
                    binding.tvabplus.text = "AB+: " + bloodbankdata.abplus
                    binding.tvabminus.text = "AB-: " + bloodbankdata.abminus

                }
            }
        }
    }
}