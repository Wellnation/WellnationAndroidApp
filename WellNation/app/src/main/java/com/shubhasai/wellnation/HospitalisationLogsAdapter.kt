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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shubhasai.wellnation.utils.dateandtimeformat

class HospitalisationLogsAdapter(private val context: Context, private val hLogs: ArrayList<hlogs>) :
    RecyclerView.Adapter<HospitalisationLogsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.hospitalisationlog_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hLog = hLogs[position]
        holder.drlog.layoutManager = LinearLayoutManager(context)
        holder.drlog.adapter = DoctorsLogAdapter(hLog.doctors)
        holder.medlog.layoutManager = LinearLayoutManager(context)
        holder.medlog.adapter = MedicineLogAdapter(hLog.meds)
        holder.testlog.layoutManager = LinearLayoutManager(context)
        holder.testlog.adapter = TestsLogAdapter(hLog.tests)
        holder.logdate.text = dateandtimeformat.formatFirebaseDateTime(hLog.logDate)

    }


    override fun getItemCount(): Int {
        return hLogs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drlog: RecyclerView = itemView.findViewById(R.id.doctorslogrv)
        val medlog: RecyclerView = itemView.findViewById(R.id.medicinelogrv)
        val testlog: RecyclerView = itemView.findViewById(R.id.testlogrv)
        val logdate:TextView = itemView.findViewById(R.id.logdate)
    }
}
