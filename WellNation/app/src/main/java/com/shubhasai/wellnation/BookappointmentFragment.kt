package com.shubhasai.wellnation

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.shubhasai.wellnation.databinding.FragmentBookappointmentBinding
import com.shubhasai.wellnation.utils.DialogUtils
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookappointmentFragment : Fragment() {
    private lateinit var binding:FragmentBookappointmentBinding
    var selectedDate: Long? = null
    var selectedTime: Int? = null
    val args:BookappointmentFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentBookappointmentBinding.inflate(layoutInflater)
        binding.btnbookappointment.setOnClickListener {
            binding.btnSelectDate.performClick()
            //getdepartment()
        }
        binding.btnSelectDate.setOnClickListener {
            val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setTitleText("Select date of Appointment")
                .build()
            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedDate = selection
                binding.btnSelectTime.performClick()
            }
        }

        binding.btnSelectTime.setOnClickListener {
            val timePicker: MaterialTimePicker = MaterialTimePicker
                .Builder()
                .setTitleText("Select a time")
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()
            timePicker.show(parentFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener { selection ->
                selectedTime = timePicker.hour * 60 + timePicker.minute
                updateSelectedDateTimeText()
            }
        }
        return binding.root
    }
    fun updateSelectedDateTimeText() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = selectedDate?.let { sdf.format(it) } ?: ""
        val time = selectedTime?.let { String.format("%02d:%02d", it / 60, it % 60) } ?: ""

        if (selectedDate != null && selectedTime != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate!!
            calendar.set(Calendar.HOUR_OF_DAY, selectedTime!! / 60)
            calendar.set(Calendar.MINUTE, selectedTime!! % 60)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val combinedTimestamp = Timestamp(calendar.time)
            // Save the combinedTimestamp to Firebase Firestore
            bookappointment(combinedTimestamp)
            // ...
        }
    }

    fun bookappointment(timestamp:Timestamp){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Appointment Booking")
        builder.setMessage("Please select the mode of appointment booking:")

        // Online button
        builder.setPositiveButton("Online") { _, _ ->
            // Handle online booking logic here
            val medicine:ArrayList<medicines> = ArrayList()
            val db =  Firebase.firestore.collection("appointments")
            val key = db.document().id.toString()
            val booking_details = AppointmentData(key,Userinfo.drclickedclicked,args.hospitalid,medicine,Userinfo.userid, remark = "", reqtime = timestamp, shldtime = Timestamp.now(), status = false, symptoms = binding.etsymptoms.text.toString(), dept = Userinfo.departmentclicked,onlinemode = true, sharerecord = binding.sharerecord.isChecked)
            db.document(key).set(booking_details)
            activity?.let { DialogUtils.showLottieBottomSheetDialog(it,R.raw.done,"Appointment Booked Successfully") }

        }

        // Offline button
        builder.setNegativeButton("Offline") { _, _ ->
            // Handle offline booking logic here
            val db =  Firebase.firestore.collection("appointments")
            val key = db.document().id.toString()
            val medicine:ArrayList<medicines> = ArrayList()
            val booking_details = AppointmentData(key,Userinfo.drclickedclicked,args.hospitalid,medicine,Userinfo.userid, remark = "", reqtime = timestamp, shldtime = Timestamp.now(), status = false, symptoms = binding.etsymptoms.text.toString(),dept = Userinfo.departmentclicked,onlinemode = false,sharerecord = binding.sharerecord.isChecked)
            db.document(key).set(booking_details)
            activity?.let { DialogUtils.showLottieBottomSheetDialog(it,R.raw.done,"Appointment Booked Successfully") }
        }
        val dialog = builder.create()
        dialog.show()
    }
//  fun getdepartment(){
//        lifecycleScope.launch {
//            val okHttpClient = OkHttpClient()
//            val payload = "fever"
//            val requestBody = payload.toRequestBody()
//            val request = Request.Builder()
//                .method("POST", requestBody)
//                .url("https://api-inference.huggingface.co/models/oyesaurav/dwellbert")
//                .build()
//            okHttpClient.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    // Handle this
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    // Handle this
//                    val responseBody = response.body?.string()
//                    val departmet = Gson().fromJson(responseBody, departmentResponse::class.java)
//                    if (responseBody != null) {
//                        val department = departmet[0][0].label
//                        val departmentMap = mapOf(
//                            "LABEL_0" to "Gastroenterology",
//                            "LABEL_1" to "Neurology",
//                            "LABEL_2" to "Orthopedic",
//                            "LABEL_3" to "Radiology",
//                            "LABEL_4" to "Urology"
//                        )
//                        Log.d("api respose",department)
//                        val level = department // The received level from the API
//                        val departmentName = departmentMap[level]
//                        if (departmentName != null) {
//                            Log.d("api respose",departmentName)
//                        }
//                    }
//                }
//            })
//
//        }
//    }
}