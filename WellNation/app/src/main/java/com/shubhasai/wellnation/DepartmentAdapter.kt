package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DepartmentAdapter(private val context: Context?,val hospitalsdep: ArrayList<DepartmentData>,val hids:ArrayList<HospitalList>, val listener: DeptClicked) : RecyclerView.Adapter<DepartmentAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardview: CardView = itemView.findViewById(R.id.cardViewdepartment)
        val nameTextView: TextView = itemView.findViewById(R.id.tvdepartment_name)
        val addressTextView: TextView = itemView.findViewById(R.id.tv_deptdoctorsnumber)
        val phoneTextView: TextView = itemView.findViewById(R.id.tv_deptrating)
        val hname: TextView = itemView.findViewById(R.id.hname)
        val bookbtn:Button = itemView.findViewById(R.id.btn_booknow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.department_item, parent, false))
        viewholder.cardview.setOnClickListener {
           listener.onDeptClicked(hospitalsdep[viewholder.adapterPosition],hids[viewholder.adapterPosition])
        }
        viewholder.bookbtn.setOnClickListener {
            listener.onbooknowclicked(hospitalsdep[viewholder.adapterPosition],hids[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun getItemCount(): Int {
        return hospitalsdep.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dept = hospitalsdep[position]
        if (hids.size!=0){
            val hospital = hids[position]
            holder.hname.text = hospital.name
        }
        holder.nameTextView.text = dept.name
        holder.addressTextView.text = dept.doctors.size.toString() + " Doctors"
        holder.phoneTextView.text = "4.5"
    }
    interface DeptClicked {
        fun onDeptClicked(dept: DepartmentData,hids: HospitalList){

        }
        fun onbooknowclicked(dept: DepartmentData,hids:HospitalList){

        }
    }
}