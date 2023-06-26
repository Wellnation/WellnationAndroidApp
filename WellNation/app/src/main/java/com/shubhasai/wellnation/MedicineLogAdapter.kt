package com.shubhasai.wellnation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineLogAdapter(private val medicines: ArrayList<med>) :
    RecyclerView.Adapter<MedicineLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medicinelog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.bind(medicine)
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineNameTextView: TextView = itemView.findViewById(R.id.tvMedicineName)
        private val medicinePriceTextView: TextView = itemView.findViewById(R.id.tvMedicinePrice)

        fun bind(medicine: med) {
            medicineNameTextView.text = medicine.name
            medicinePriceTextView.text = medicine.price
        }
    }
}
