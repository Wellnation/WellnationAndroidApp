package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RoomAdapter(private val context: Context?, val roomlist: ArrayList<room>): RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val roomcardview: CardView = itemView.findViewById(R.id.cardViewroom)
        val roomtype: TextView = itemView.findViewById(R.id.tv_roomtype)
        val roomavailable: TextView = itemView.findViewById(R.id.tv_available)
        val roomprice: TextView = itemView.findViewById(R.id.tv_roomprice)
        val roomcapacity: TextView = itemView.findViewById(R.id.tv_capacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = RoomAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.room_item, parent, false)
        )
        viewholder.roomcardview.setOnClickListener {

        }
        return viewholder
    }

    override fun getItemCount(): Int {
        return roomlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = roomlist[position]
        holder.roomtype.text = "Room Type: "+room.type
        holder.roomcapacity.text = "Capacity: "+room.capacity.toString()
        holder.roomavailable.text = "Available: "+room.currAvail.toString()
        holder.roomprice.text = "Rs. "+room.cost.toString()
    }
}