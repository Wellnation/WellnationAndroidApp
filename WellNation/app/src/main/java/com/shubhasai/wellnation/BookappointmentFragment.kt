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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.shubhasai.wellnation.databinding.FragmentBookappointmentBinding
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class BookappointmentFragment : Fragment() {
    private lateinit var binding:FragmentBookappointmentBinding
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
            bookappointment()
            //getdepartment()
        }
        return binding.root
    }

    fun bookappointment(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Appointment Booking")
        builder.setMessage("Please select the mode of appointment booking:")

        // Online button
        builder.setPositiveButton("Online") { _, _ ->
            Toast.makeText(activity, "Online booking selected", Toast.LENGTH_SHORT).show()
            // Handle online booking logic here
            val medicine:ArrayList<medicines> = ArrayList()
            val db =  Firebase.firestore.collection("appointments")
            val key = db.id.toString()
            val booking_details = AppointmentData(key,"",args.hospitalid,medicine,Userinfo.userid, remark = "", reqtime = Timestamp.now(), shldtime = Timestamp.now(), status = false, symptoms = binding.etsymptoms.text.toString(), dept = "",onlinemode = true)
            db.document().set(booking_details)
        }

        // Offline button
        builder.setNegativeButton("Offline") { _, _ ->
            Toast.makeText(activity, "Offline booking selected", Toast.LENGTH_SHORT).show()
            // Handle offline booking logic here
            val db =  Firebase.firestore.collection("appointments")
            val key = db.id.toString()
            val medicine:ArrayList<medicines> = ArrayList()
            val booking_details = AppointmentData(key,"",args.hospitalid,medicine,Userinfo.userid, remark = "", reqtime = Timestamp.now(), shldtime = Timestamp.now(), status = false, symptoms = binding.etsymptoms.text.toString(),dept = "",onlinemode = false)
            db.document().set(booking_details)
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