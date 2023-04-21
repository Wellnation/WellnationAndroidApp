package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.FragmentDiseasesBinding
class DiseasesFragment : Fragment() {
    private lateinit var binding: FragmentDiseasesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiseasesBinding.inflate(inflater, container, false)
        getdiseaseslist()
        binding.btnAddDiseases.setOnClickListener {
            setdiseases()
        }
        return binding.root
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