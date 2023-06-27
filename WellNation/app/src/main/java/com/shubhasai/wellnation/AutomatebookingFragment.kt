package com.shubhasai.wellnation

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.shubhasai.wellnation.databinding.FragmentAutomatebookingBinding
import com.shubhasai.wellnation.utils.DialogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

class AutomatebookingFragment : Fragment(),DepartmentAdapter.DeptClicked {
    private lateinit var binding: FragmentAutomatebookingBinding
    val hids:ArrayList<HospitalList> = ArrayList()
    val depts:ArrayList<DepartmentData> = ArrayList()
    var symptoms:String = ""
    private val RECORD_AUDIO_PERMISSION_CODE = 1
    private lateinit var speechRecognizer: SpeechRecognizer
    var departmentselected:String = ""
    private lateinit var departmentAdapter: DepartmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(SpeechRecognitionListener())
        binding = FragmentAutomatebookingBinding.inflate(inflater, container, false)
        binding.symptoms.visibility = View.VISIBLE
        getdepartments("teeth")
        getdepartments("teeth")
        binding.submit.setOnClickListener {
            symptoms = binding.etSymptoms.text.toString()
            if (symptoms!=""){
                getdepartment(symptoms)
            }
            else{
                Toast.makeText(activity,"Trying After Sometime",Toast.LENGTH_SHORT).show()
            }
        }
        departmentAdapter = DepartmentAdapter(activity as Context?, depts, hids, this)
        binding.rvhospitadepartmentwise.layoutManager = GridLayoutManager(activity,2)
        binding.rvhospitadepartmentwise.adapter = departmentAdapter
        binding.btnMic.setOnClickListener {
            requestAudioPermissions()
        }
        return binding.root
    }
    fun getdepartment(symtomps:String){
        lifecycleScope.launch {
            val okHttpClient = OkHttpClient()
            val payload = symtomps
            val requestBody = payload.toRequestBody()
            val request = Request.Builder()
                .method("POST", requestBody)
                .url("https://api-inference.huggingface.co/models/oyesaurav/dwellbert")
                .build()
            try{
                Toast.makeText(activity,"Trying to get data",Toast.LENGTH_SHORT).show()
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle this
                    }

                    override fun onResponse(call: Call, response: Response) {
                        // Handle this
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.secondquestion.visibility = View.VISIBLE
                            binding.departmentdropdown.visibility =View.VISIBLE
                            val responseBody = response.body?.string()
                            val departmet = Gson().fromJson(responseBody, mlapiresponse::class.java)
                            if (responseBody != null) {
                                Log.d("response",responseBody.toString())
                            }
                            val optionsList = ArrayList<String>()
                            for (department in departmet[0]){
                                optionsList.add(department.label)
                            }
                            departmentselected = optionsList[0]
                            binding.symptomresult.text = "On the basis of your symptoms we suggest you to visit ${optionsList[0]}. If you want to book for any other department select from below."
                            val adapter =
                                activity?.let {
                                    ArrayAdapter(
                                        it,
                                        R.layout.simple_spinner_item,
                                        optionsList
                                    )
                                }
                            adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerOptions.adapter = adapter
                            binding.spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selectedOption = optionsList[position]
                                    // Handle the selected option
                                    departmentselected = selectedOption
                                    binding.secondresponse.visibility = View.VISIBLE
                                    binding.tvselectedDepartment.text = "Selected Department is $departmentselected"
                                    binding.rvhospitadepartmentwise.visibility = View.VISIBLE
                                    gethospitals(departmentselected)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Handle case where no option is selected
                                    departmentselected = optionsList[0]
                                    binding.secondresponse.visibility = View.VISIBLE
                                    binding.tvselectedDepartment.text = "Selected Department is {$departmentselected}"
                                    binding.rvhospitadepartmentwise.visibility = View.VISIBLE
                                    gethospitals(departmentselected)
                                }
                            }
                        }

                    }
                })
            }
            catch (e:Exception){

            }

        }
    }
    fun getdepartments(symtomps:String){
        lifecycleScope.launch {
            val okHttpClient = OkHttpClient()
            val payload = symtomps
            val requestBody = payload.toRequestBody()
            val request = Request.Builder()
                .method("POST", requestBody)
                .url("https://api-inference.huggingface.co/models/oyesaurav/dwellbert")
                .build()
            try{
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle this
                    }

                    override fun onResponse(call: Call, response: Response) {
                        // Handle this
                        CoroutineScope(Dispatchers.Main).launch {

                        }
                    }
                })
            }
            catch (e:Exception){

            }

        }
    }
    fun gethospitals(deparment:String){
        hids.clear()
        depts.clear()
//        hids.add(HospitalList())
//        depts.add(DepartmentData())
        val db = FirebaseFirestore.getInstance().collection("users")
        db.get().addOnCompleteListener {
            for (document in it.result.documents){
                if (document.exists()){
                    val docid = document.id
                    val hospital = document.toObject(HospitalList::class.java)
                    if (hospital != null) {
                        getdepartmentfromdb(hospital)
                    }
                }
            }
            Log.d("check3",depts.size.toString())
            departmentAdapter.notifyDataSetChanged()
        }

        Log.d("check3",depts.size.toString()+"3")

    }

    private fun getdepartmentfromdb(docid:HospitalList) {
        Log.d("check4",depts.size.toString())
        departmentAdapter.notifyDataSetChanged()
        val collectionRef =
            FirebaseFirestore.getInstance().collection("users").document(docid.uid).collection("departments")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val dept = document.toObject(DepartmentData::class.java)
                    if (dept.name == departmentselected){
                        depts.add(dept)
                        hids.add(docid)
                    }
                }
                departmentAdapter.notifyDataSetChanged()
                Log.d("check5",depts.size.toString())
                Log.d("check51",hids.size.toString())
                Log.d("check52",depts.toString())
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
    override fun onbooknowclicked(dept: DepartmentData,hids:HospitalList) {
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
            .Builder
            .datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setTitleText("Select date of Appointment")
            .build()
        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection
            val timePicker: MaterialTimePicker = MaterialTimePicker
                .Builder()
                .setTitleText("Select a time")
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()
            timePicker.show(parentFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener { selection ->
                selectedTime = timePicker.hour * 60 + timePicker.minute
                updateSelectedDateTimeText(hids.uid,dept.name)
            }
        }

    }
    var selectedDate: Long? = null
    var selectedTime: Int? = null
    fun bookappointment(hid:String,deptname:String,timestamp: Timestamp){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Appointment Booking")
        builder.setMessage("Please select the mode of appointment booking:")
        // Online button
        builder.setPositiveButton("Online") { _, _ ->
            // Handle online booking logic here
            val medicine:ArrayList<medicines> = ArrayList()
            val db =  Firebase.firestore.collection("appointments")
            val key = db.document().id
            val booking_details = AppointmentData(apptId = key,"",hid,medicine,Userinfo.userid, remark = "", reqtime = timestamp, shldtime = Timestamp.now(), status = false, symptoms = binding.etSymptoms.text.toString(), dept = deptname,onlinemode = true)
            db.document(key).set(booking_details)
            activity?.let { DialogUtils.showLottieBottomSheetDialog(it,
                com.shubhasai.wellnation.R.raw.done,"Appointment Booked Successfully") }
        }

        // Offline button
        builder.setNegativeButton("Offline") { _, _ ->
            // Handle offline booking logic here
            val medicine:ArrayList<medicines> = ArrayList()
            val db =  Firebase.firestore.collection("appointments")
            val key = db.document().id.toString()
            val booking_details = AppointmentData(key,"",hid,medicine,Userinfo.userid, remark = "", reqtime = timestamp, shldtime = Timestamp.now(), status = false, symptoms = binding.etSymptoms.text.toString(), dept = deptname,onlinemode = false)
            db.document(key).set(booking_details)
            activity?.let { DialogUtils.showLottieBottomSheetDialog(it,
                com.shubhasai.wellnation.R.raw.done,"Appointment Booked Successfully") }
        }

        val dialog = builder.create()
        dialog.show()
    }
    fun updateSelectedDateTimeText(hid:String,dept:String) {
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
            bookappointment(hid,dept,combinedTimestamp)
            // ...
        }
    }
    private fun requestAudioPermissions() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), RECORD_AUDIO_PERMISSION_CODE)
        } else {
            startSpeechToText()
        }
    }

    private fun startSpeechToText() {
        val speechToTextIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechToTextIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechToTextIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        speechRecognizer.startListening(speechToTextIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    inner class SpeechRecognitionListener : android.speech.RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                // Handle the recognized text here
                binding.etSymptoms.setText(recognizedText)
                Toast.makeText(requireContext(), recognizedText, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}