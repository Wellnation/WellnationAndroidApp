package com.shubhasai.wellnation

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.shubhasai.wellnation.databinding.FragmentAppointmentdetailsBinding
import de.coldtea.smplr.smplralarm.alarmNotification
import de.coldtea.smplr.smplralarm.channel
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class AppointmentdetailsFragment : Fragment() {
    private lateinit var binding:FragmentAppointmentdetailsBinding
    private lateinit var sharedPreferences: SharedPreferences
    val navarg:AppointmentdetailsFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = requireContext().getSharedPreferences("medicinealarm", Context.MODE_PRIVATE)
        binding = FragmentAppointmentdetailsBinding.inflate(layoutInflater)
        getappointmentdetails()
        binding.textViewPatientName.text = "Patient Name: "+Userinfo.uname
        binding.textViewPhoneNumber.text = "Patient Contact: "+Userinfo.phonenumber
        binding.textViewEmailAddress.text = "Patient EmailId: "+Userinfo.email
        binding.btnShareappt.setOnClickListener {
//            createPdfFromView(binding.cardviewPrescription)
            createPdfAndShare(binding.cardviewPrescription)
        }
        return binding.root
    }
    fun getappointmentdetails(){
        val db = FirebaseFirestore.getInstance().collection("appointments")
        db.document(navarg.appId).get().addOnSuccessListener {
            val docdata = it.toObject(AppointmentData::class.java)
            if (docdata != null) {
                var mode = ""
                binding.textViewAppointmentid.text = "AppointmentID: " + docdata.apptId
                if (docdata.onlinemode){
                    mode = "Online Mode"
                }
                else{
                    mode = "Offline Mode"
                }
                getdocname(docdata.drid)
                gethospitalname(docdata.hid)
                binding.textRemark.text = binding.textRemark.text.toString() + docdata.remark
                binding.textDepartment.text = binding.textDepartment.text.toString() + docdata.dept
                binding.textScheduledTime.text = binding.textScheduledTime.text.toString() + docdata.shldtime.toDate().toString()
                binding.textOnlineMode.text = binding.textOnlineMode.text.toString() + mode
                val medicines:ArrayList<medicines> = ArrayList()
                for (medicine in docdata.medicine){
                    medicines.add(medicine)
                    for (time in medicine.time){
                        schedulealarm(time.hr.toInt(),time.min.toInt(),navarg.appId,medicine.name)
                    }
                }
                binding.recyclerViewMedicines.layoutManager = LinearLayoutManager(activity)
                binding.recyclerViewMedicines.adapter = MedicinesAdapter(activity,medicines)
            }
        }
    }
    fun getdocname(drid:String){
        val db = FirebaseFirestore.getInstance().collection("doctors")
        db.document(drid).get().addOnSuccessListener {
            val doctordetails = it.toObject(DoctorInfo::class.java)
            if (doctordetails != null) {
                binding.textDoctorname.text = binding.textDoctorname.text.toString() + doctordetails.name
            }
        }
    }
    fun gethospitalname(hospid:String){
        val db = FirebaseFirestore.getInstance().collection("users")
        db.document(hospid).get().addOnSuccessListener {
            val doctordetails = it.toObject(DoctorInfo::class.java)
            if (doctordetails != null) {
                binding.textHospitalname.text = binding.textHospitalname.text.toString() + doctordetails.name
            }
        }
    }
//    private fun createPdfFromView(view: View) {
//        // Create a new PDF document
//        val document = PdfDocument()
//
//        // Create a page info with the desired attributes
//        val printAttrs = PrintAttributes.Builder()
//            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
//            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//            .build()
//        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
//
//        // Start a new page
//        val page = document.startPage(pageInfo)
//
//        // Draw the view on the page's canvas
//        val canvas = page.canvas
//        val paint = Paint()
//        paint.color = Color.WHITE
//        canvas.drawPaint(paint)
//        view.draw(canvas)
//
//        // Finish the page
//        document.finishPage(page)
//
//        // Save the document to a file
//        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
//        val filePath = "$directoryPath/output.pdf"
//        val file = File(filePath)
//
//        try {
//            file.createNewFile()
//            val fos = FileOutputStream(file)
//            document.writeTo(fos)
//            document.close()
//            fos.close()
//            Toast.makeText(activity, "PDF created successfully", Toast.LENGTH_SHORT).show()
//            val shareIntent = Intent(Intent.ACTION_SEND)
//
//            shareIntent.type = "application/pdf"
//            val fileUri = activity?.let { FileProvider.getUriForFile(it, "com.shubhasai.wellnation.fileprovider", file) }
//            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
//            startActivity(Intent.createChooser(shareIntent, "Share Prescription"))
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(activity, "Error creating PDF", Toast.LENGTH_SHORT).show()
//        }
//    }
private fun createPdfAndShare(view: View) {
    // Create a new PDF document
    val document = PdfDocument()

    // Create a page info with the desired attributes
    val printAttrs = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .build()
    val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()

    // Start a new page
    val page = document.startPage(pageInfo)

    // Draw the view on the page's canvas
    val canvas = page.canvas
    val paint = Paint()
    paint.color = Color.WHITE
    canvas.drawPaint(paint)
    view.draw(canvas)

    // Finish the page
    document.finishPage(page)

    try {
        val byteArrayOutputStream = ByteArrayOutputStream()
        document.writeTo(byteArrayOutputStream)
        document.close()

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        val pdfData: ByteArray = byteArrayOutputStream.toByteArray()
        val pdfUri = activity?.let { context ->
            val tempFile = File(context.cacheDir, "prescription_pdf.pdf")
            val fileOutputStream = FileOutputStream(tempFile)
            fileOutputStream.write(pdfData)
            fileOutputStream.close()
            FileProvider.getUriForFile(context, "com.shubhasai.wellnation.fileprovider", tempFile)
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        startActivity(Intent.createChooser(shareIntent, "Share Prescription"))
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(activity, "Error creating PDF", Toast.LENGTH_SHORT).show()
    }
}

    fun schedulealarm(hr:Int,min:Int,id:String,name:String){
        val arrayList = ArrayList<String>()
        val jsonString = sharedPreferences.getString("apptId", null)
        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val value = jsonArray.getString(i)
                arrayList.add(value)
            }
            var check = true
            for (data in arrayList){
                if (data==id){
                    check = false
                }
            }
            if (check){
                activity?.let {
                    smplrAlarmSet(it) {
                        hour { hr }
                        min { min }
                        weekdays {
                            monday()
                            tuesday()
                            wednesday()
                            thursday()
                            friday()
                            saturday()
                            sunday()
                        }
                        notification {
                            alarmNotification {
                                smallIcon { R.drawable.alarm }
                                title { "Medicine Reminder" }
                                message { "" }
                                bigText { "Eat $name" }
                                autoCancel { true }
                            }
                        }
                        notificationChannel {
                            channel {
                                importance { NotificationManager.IMPORTANCE_HIGH }
                                showBadge { false }
                                name { "de.coldtea.smplr.alarm.channel" }
                                description { "This notification channel is created by SmplrAlarm" }
                            }
                        }
                    }
                }
                arrayList.add(id)
                val jsonArray = JSONArray(arrayList)
                val jsonString = jsonArray.toString()
                val editor = sharedPreferences.edit()
                editor.putString("array_key", jsonString)
                editor.apply()
            }
            // Now you can use the 'arrayList'
        } else {
            activity?.let {
                smplrAlarmSet(it) {
                    hour { hr }
                    min { min }
                    weekdays {
                        monday()
                        tuesday()
                        wednesday()
                        thursday()
                        friday()
                        saturday()
                        sunday()
                    }
                    notification {
                        alarmNotification {
                            smallIcon { R.drawable.alarm }
                            title { "Medicine Reminder" }
                            message { "" }
                            bigText { "Eat the medicine" }
                            autoCancel { true }
                        }
                    }
                    notificationChannel {
                        channel {
                            importance { NotificationManager.IMPORTANCE_HIGH }
                            showBadge { false }
                            name { "de.coldtea.smplr.alarm.channel" }
                            description { "This notification channel is created by SmplrAlarm" }
                        }
                    }
                }
            }
            arrayList.add(id)
            val jsonArray = JSONArray(arrayList)
            val jsonString = jsonArray.toString()
            val editor = sharedPreferences.edit()
            editor.putString("array_key", jsonString)
            editor.apply()
            // Handle case when the ArrayList is not found in SharedPreferences
        }
    }

}