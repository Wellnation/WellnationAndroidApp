package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TestAdapter(private val context: Context?, val testlist: ArrayList<tests>):RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    class ViewHolder(itemview: View):RecyclerView.ViewHolder(itemview) {
        val testcardview: CardView = itemView.findViewById(R.id.cardViewtest)
        val nameTextView: TextView = itemView.findViewById(R.id.tvtestname)
        val hospitalname: TextView = itemView.findViewById(R.id.test_hospitalname)
        val testprice: TextView = itemView.findViewById(R.id.tv_testprice)
        val booktest:Button = itemview.findViewById(R.id.btn_booktest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = TestAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.test_itemview, parent, false)
        )
        viewholder.testcardview.setOnClickListener {

        }
        return viewholder
    }

    override fun getItemCount(): Int {
        return testlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tests = testlist[position]
        holder.nameTextView.text = tests.testname
        holder.hospitalname.text = tests.hospitalname
        holder.testprice.text = "Rs. "+tests.testprice.toString()
    }
}