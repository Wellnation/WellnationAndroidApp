package com.shubhasai.wellnation

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class HospitalisationLogsAdapter(private val context: Context, private val hLogs: ArrayList<hlogs>) :
    RecyclerView.Adapter<HospitalisationLogsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.hospitalisationlog_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hLog = hLogs[position]
        holder.logdate.text = hLog.logDate.toDate().toString()
        // Clear existing rows and headers
        holder.tableLayout1.removeAllViews()
        holder.tableLayout1.removeAllViewsInLayout()
        holder.tableLayout2.removeAllViews()
        holder.tableLayout2.removeAllViewsInLayout()
        holder.tableLayout3.removeAllViews()
        holder.tableLayout3.removeAllViewsInLayout()

        // Add header for TableLayout1 (Doctors)
        val header1 = TableRow(context)
        val header1TextView1 = TextView(context)
        header1TextView1.text = "Doctor Name"
        header1TextView1.gravity = Gravity.CENTER
        header1TextView1.setTypeface(null, Typeface.BOLD) // Make the text bold
        val header1TextView2 = TextView(context)
        header1TextView2.text = "Remark"
        header1TextView2.gravity = Gravity.CENTER
        header1TextView2.setTypeface(null, Typeface.BOLD) // Make the text bold
        header1.addView(header1TextView1)
        header1.addView(header1TextView2)
        header1.setPadding(0, 0, 16, 0) // Add spacing between columns
        holder.tableLayout1.addView(header1)

// Add rows for doctors
        for (doctor in hLog.doctors) {
            val row = TableRow(context)

            val doctorNameTextView = TextView(context)
            doctorNameTextView.text = doctor.doctorName
            doctorNameTextView.gravity = Gravity.CENTER
            val remarkTextView = TextView(context)
            remarkTextView.text = doctor.remark
            remarkTextView.gravity = Gravity.CENTER
            row.addView(doctorNameTextView)
            row.addView(remarkTextView)
            row.setPadding(0, 0, 16, 0) // Add spacing between columns
            holder.tableLayout1.addView(row)
        }

// Add header for TableLayout2 (Medicines)
        val header2 = TableRow(context)
        val header2TextView1 = TextView(context)
        header2TextView1.text = "Name"
        header2TextView1.gravity = Gravity.CENTER
        header2TextView1.setTypeface(null, Typeface.BOLD) // Make the text bold
        val header2TextView2 = TextView(context)
        header2TextView2.text = "Price"
        header2TextView2.gravity = Gravity.CENTER
        header2TextView2.setTypeface(null, Typeface.BOLD) // Make the text bold
        header2.addView(header2TextView1)
        header2.addView(header2TextView2)
        header2.setPadding(0, 0, 16, 0) // Add spacing between columns
        holder.tableLayout2.addView(header2)

// Add rows for meds
        for (med in hLog.meds) {
            val row = TableRow(context)

            val nameTextView = TextView(context)
            nameTextView.text = med.name
            nameTextView.gravity = Gravity.CENTER
            val priceTextView = TextView(context)
            priceTextView.text = med.price
            priceTextView.gravity = Gravity.CENTER
            row.addView(nameTextView)
            row.addView(priceTextView)
            row.setPadding(0, 0, 16, 0) // Add spacing between columns
            holder.tableLayout2.addView(row)
        }

// Add header for TableLayout3 (Tests)
        val header3 = TableRow(context)
        val header3TextView1 = TextView(context)
        header3TextView1.text = "Price"
        header3TextView1.gravity = Gravity.CENTER
        header3TextView1.setTypeface(null, Typeface.BOLD) // Make the text bold
        val header3TextView2 = TextView(context)
        header3TextView2.text = "Type"
        header3TextView2.gravity = Gravity.CENTER
        header3TextView2.setTypeface(null, Typeface.BOLD) // Make the text bold
        header3.addView(header3TextView1)
        header3.addView(header3TextView2)
        header3.setPadding(0, 0, 16, 0) // Add spacing between columns
        holder.tableLayout3.addView(header3)

// Add rows for tests
        for (test in hLog.tests) {
            val row = TableRow(context)

            val priceTextView = TextView(context)
            priceTextView.text = test.price
            priceTextView.gravity = Gravity.CENTER
            val typeTextView = TextView(context)
            typeTextView.text = test.type
            typeTextView.gravity = Gravity.CENTER
            row.addView(priceTextView)
            row.addView(typeTextView)
            row.setPadding(0, 0, 16, 0) // Add spacing between columns
            holder.tableLayout3.addView(row)
        }

    }


    override fun getItemCount(): Int {
        return hLogs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableLayout1: TableLayout = itemView.findViewById(R.id.tableLayout1)
        val tableLayout2: TableLayout = itemView.findViewById(R.id.tableLayout2)
        val tableLayout3: TableLayout = itemView.findViewById(R.id.tableLayout3)
        val logdate:TextView = itemView.findViewById(R.id.logdate)
    }
}
