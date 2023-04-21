package com.shubhasai.wellnation

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CampaignAdapter(private val context: Context?,val campaigns: ArrayList<campaigndata>, val listener:CampaignClicked) :
    RecyclerView.Adapter<CampaignAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvcampaignname)
        val start: TextView = itemView.findViewById(R.id.campaign_startdate)
        val end: TextView = itemView.findViewById(R.id.campaign_enddate)
        val hospital:TextView = itemView.findViewById(R.id.campaign_hospitalname)
        val btn:Button = itemView.findViewById(R.id.btn_navigate_campaign)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.campaign_item, parent, false))
        viewholder.btn.setOnClickListener {
            listener.onCampaignClicked(campaigns[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val campaign = campaigns[position]
        holder.name.text = campaign.name
        holder.start.text = "Start: "+campaign.start.toDate().toString()
        holder.end.text = "End: "+campaign.end.toDate().toString()
        holder.hospital.text = "By: "+campaign.hospitalname

    }

    override fun getItemCount(): Int {
        return campaigns.size
    }
    interface CampaignClicked {
        fun onCampaignClicked(campaigns: campaigndata){

        }
    }
}


