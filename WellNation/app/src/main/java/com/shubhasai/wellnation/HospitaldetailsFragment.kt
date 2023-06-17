package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentHospitaldetailsBinding

class HospitaldetailsFragment : Fragment() {
    private lateinit var binding: FragmentHospitaldetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = HospitaldetailspageAdapter(childFragmentManager,lifecycle)
        binding = FragmentHospitaldetailsBinding.inflate(layoutInflater)
        binding.hospitalViewpager.adapter = adapter
        TabLayoutMediator(binding.hospitaltab,binding.hospitalViewpager){tab,position->
            when(position){
                0->{
                    tab.text="Services Available"
                }
                1->{
                    tab.text="Doctors"
                }
//                2->{
//                    tab.text="Bloodbank"
//                }
//                3->{
//                    tab.text="Rooms"
//                }
//                4->{
//                    tab.text="Doctors"
//                }
            }
        }.attach()
        gethospitaldetails()
        return binding.root
    }

    fun gethospitaldetails(){
        val db = Firebase.firestore
        val collectionRef = db.collection("users")
        collectionRef.document(Userinfo.hospitalclicked).get().addOnSuccessListener {
            if (it.exists()){
                val hospitaldetails = it.toObject(HospitalList::class.java)
                if (hospitaldetails != null) {
                    binding.tvhospitalName.text = hospitaldetails.name
//                    binding.hospitalRatingbar.rat = hospitaldetails.rating
                    binding.tvhospitaladdress.text = hospitaldetails.address.locality+", "+hospitaldetails.address.district+", "+hospitaldetails.address.state+", "+hospitaldetails.address.pincode
                    binding.tvContactnumber.text = hospitaldetails.phone
                }
            }
        }
    }

}