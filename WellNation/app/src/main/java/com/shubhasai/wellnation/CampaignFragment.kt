package com.shubhasai.wellnation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.FragmentCampaignBinding

class CampaignFragment : Fragment(),CampaignAdapter.CampaignClicked {
   private lateinit var binding: FragmentCampaignBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCampaignBinding.inflate(inflater, container, false)
        getcampaigns()
        return binding.root
    }
    fun getcampaigns(){
        val db = FirebaseFirestore.getInstance().collection("campaign")
        db.get().addOnSuccessListener {
            val campaigns = ArrayList<campaigndata>()
            for (document in it){
                val campaign = document.toObject(campaigndata::class.java)
                campaigns.add(campaign)
            }
            binding.campaignRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.campaignRecyclerView.adapter = CampaignAdapter(activity,campaigns,this)
        }
    }

    override fun onCampaignClicked(campaigns: campaigndata) {
        val packageManager = context!!.packageManager
        val uri = "google.navigation:q=${campaigns.location.latitude},${campaigns.location.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(activity, "Google Maps app not found", Toast.LENGTH_SHORT).show()
        }
    }
}