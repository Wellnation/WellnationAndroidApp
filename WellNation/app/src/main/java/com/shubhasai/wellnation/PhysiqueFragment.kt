package com.shubhasai.wellnation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.shubhasai.wellnation.databinding.FragmentPhysiqueBinding
import java.util.Calendar
import java.util.Date


class PhysiqueFragment : Fragment() {
    private lateinit var binding:FragmentPhysiqueBinding
    val weightdata:ArrayList<bmirecord> = ArrayList()
    val bpdata:ArrayList<bloodpressuredata> = ArrayList()
    val sugardata:ArrayList<sugarrecord> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPhysiqueBinding.inflate(layoutInflater)
        getBMI()
        binding.buttonbmi.setOnClickListener {
            addweight()
        }
        binding.buttonbp.setOnClickListener {
            addbp()
        }
        binding.buttonsugar.setOnClickListener {
            addsugar()
        }
        return binding.root
    }
    fun getBMI(){
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info").collection("bmirecord")
        db.get().addOnSuccessListener {
            for (doc in it.documents){
                val weight = doc.toObject(bmirecord::class.java)
                if (weight != null) {
                    weightdata.add(weight)
                }
            }
            val db2 = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info").collection("bloodpressure")
            db2.get().addOnSuccessListener {
                for (doc in it.documents){
                    val weight = doc.toObject(bloodpressuredata::class.java)
                    if (weight != null) {
                        bpdata.add(weight)
                    }
                }
                plotgraph(weightdata, bpdata, sugardata)
                binding.chart.notifyDataSetChanged()
                Log.d("data weight",weightdata.toString())
                val db3 = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info").collection("sugarrecord")
                db3.get().addOnSuccessListener {
                    for (doc in it.documents){
                        val weight = doc.toObject(sugarrecord::class.java)
                        if (weight != null) {
                            sugardata.add(weight)
                        }
                    }
                    plotgraph(weightdata, bpdata, sugardata)
                    binding.chart.notifyDataSetChanged()
                    Log.d("data sugar",sugardata.toString())
                }
            }
            plotgraph(weightdata, bpdata, sugardata)
            binding.chart.notifyDataSetChanged()
            Log.d("data bp",bpdata.toString())
        }


    }
    fun plotgraph(weightdata:ArrayList<bmirecord>,bpdata:ArrayList<bloodpressuredata>,sugardata:ArrayList<sugarrecord>){
        Log.d("data sugar2",sugardata.toString())
        val systolicEntries = ArrayList<Entry>()
        val diastolicEntries = ArrayList<Entry>()
        val fastingEntries = ArrayList<Entry>()
        val postmealEntries = ArrayList<Entry>()
        val weightEntries = ArrayList<Entry>()
        val heightEntries = ArrayList<Entry>()

        for (bp in bpdata) {
            val date = Date(bp.time.seconds * 1000 + bp.time.nanoseconds / 1000000)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            systolicEntries.add(Entry(dayOfYear.toFloat(), bp.sys.toFloat()))
            diastolicEntries.add(Entry(dayOfYear.toFloat(), bp.dia.toFloat()))
        }

        for (sugar in sugardata) {
            val date = Date(sugar.time.seconds * 1000 + sugar.time.nanoseconds / 1000000)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            fastingEntries.add(Entry(dayOfYear.toFloat(), sugar.fasting.toFloat()))
            postmealEntries.add(Entry(dayOfYear.toFloat(), sugar.postmeal.toFloat()))
        }
        for (bmi in weightdata) {
            val date = Date(bmi.time.seconds * 1000 + bmi.time.nanoseconds / 1000000)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            weightEntries.add(Entry(dayOfYear.toFloat(), bmi.weight.toFloat()))
            heightEntries.add(Entry(dayOfYear.toFloat(), bmi.height.toFloat()))
        }

        val systolicDataSet = LineDataSet(systolicEntries, "Systolic")
        systolicDataSet.color =  Color.BLUE
        systolicDataSet.lineWidth = 4f
        val diastolicDataSet = LineDataSet(diastolicEntries, "Diastolic")
        diastolicDataSet.color = Color.CYAN
        diastolicDataSet.lineWidth = 4f
        val fasting = LineDataSet(fastingEntries, "Sugar Fasting")
        fasting.color = Color.DKGRAY
        fasting.lineWidth = 3f
        val postmeal = LineDataSet(postmealEntries, "Sugar Post Meal")
        postmeal.color = Color.GREEN
        postmeal.lineWidth = 3f
        val weightd = LineDataSet(weightEntries, "Weight")
        weightd.color =Color.YELLOW
        weightd.lineWidth = 2f
        val heightd = LineDataSet(heightEntries, "Height")
        heightd.color =Color.RED
        heightd.lineWidth = 2f
        val data = LineData(systolicDataSet, diastolicDataSet, fasting, postmeal,weightd,heightd)
        binding.chart.axisLeft.setDrawGridLines(false)
        binding.chart.axisRight.setDrawGridLines(false)
        binding.chart.xAxis.setDrawGridLines(false)
        binding.chart.xAxis.axisMinimum = 0f
        binding.chart.xAxis.axisMaximum = 366f
        binding.chart.data = data
        binding.chart.invalidate()
        binding.chart.xAxis.setDrawLabels(false) // hide bottom label
        binding.chart.axisLeft.setDrawLabels(false) // hide left label
        binding.chart.axisRight.setDrawLabels(false)
        binding.chart.legend.textColor = Color.WHITE
        binding.chart.setBackgroundColor(Color.BLACK)

    }
    fun addweight(){
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info").collection("bmirecord")
        db.document().set(bmirecord(binding.editText1.text.toString().toFloat(),binding.editText2.text.toString().toFloat()))
    }
    fun addbp(){
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info").collection("bloodpressure")
        db.document().set(bloodpressuredata(binding.editText3.text.toString().toFloat(),binding.editText4.text.toString().toFloat()))
    }
    fun addsugar(){
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("vitals").document("info").collection("sugarrecord")
        db.document().set(sugarrecord(binding.editText5.text.toString().toFloat(),binding.editText6.text.toString().toFloat()))
    }
}