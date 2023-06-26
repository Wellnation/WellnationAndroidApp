package com.shubhasai.wellnation

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.shubhasai.wellnation.utils.dateandtimeformat

class MyTestAdapter(private val context: Context?, val appointment: ArrayList<testbookingdata>, val listener: MyTestAdapter.ApptClicked): RecyclerView.Adapter<MyTestAdapter.ViewHolder>() {
    private var selectedItemPosition: Int = -1
    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val item: CardView = itemView.findViewById(R.id.upcomingAppointmentcardview)
        val hospitaname: TextView = itemView.findViewById(R.id.tvhospitalname)
        val testname: TextView = itemView.findViewById(R.id.tvtestname)
        val timestamp: TextView = itemView.findViewById(R.id.tvtime)
        val next: ImageView = itemView.findViewById(R.id.btn_opentest)
    }

    interface ApptClicked {
        fun onupcomingTestClicked(test:testbookingdata){

        }
        fun onopenTestClicked(test:testbookingdata){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewholder = MyTestAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.upcoming_tests_item, parent, false)
        )

        viewholder.item.setOnClickListener {
            selectedItemPosition = viewholder.adapterPosition
            listener.onupcomingTestClicked(appointment[viewholder.adapterPosition])
            notifyDataSetChanged()
        }
        viewholder.next.setOnClickListener {
            listener.onopenTestClicked(appointment[viewholder.adapterPosition])
        }

        return viewholder
    }

    override fun getItemCount(): Int {
        return appointment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appt = appointment[position]
        holder.hospitaname.text = appt.hname
        holder.testname.text = appt.tname
        holder.timestamp.text = dateandtimeformat.formatFirebaseDateTime(appt.shldtime)
        if(position == selectedItemPosition){
            Log.d("elevation","elevated")
            holder.item.elevation = 100f
        }
        else{
            Log.d("elevation","reverted")
            holder.item.elevation = 5f
        }
    }
}