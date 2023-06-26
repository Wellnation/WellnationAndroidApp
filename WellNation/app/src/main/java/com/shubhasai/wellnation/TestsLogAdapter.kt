package com.shubhasai.wellnation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TestsLogAdapter(private val tests: ArrayList<testlog>) : RecyclerView.Adapter<TestsLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.testslog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = tests[position]
        holder.bind(test)
    }

    override fun getItemCount(): Int {
        return tests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val testNameTextView: TextView = itemView.findViewById(R.id.tvTestName)
        private val testPriceTextView: TextView = itemView.findViewById(R.id.tvTestPrice)

        fun bind(test: testlog) {
            testNameTextView.text = test.type
            testPriceTextView.text = test.price
        }
    }
}
