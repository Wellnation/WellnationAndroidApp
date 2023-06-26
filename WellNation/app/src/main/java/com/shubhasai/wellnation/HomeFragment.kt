package com.shubhasai.wellnation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.shubhasai.wellnation.databinding.FragmentHomeBinding
import android.Manifest
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment(),UpcomingAppointmentAdapter.ApptClicked,MyTestAdapter.ApptClicked {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        backbuttonpressed()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        Log.d("height",height.toString())
        val isSmallScreen = width <= 480 || height <= 1280
//        if (isSmallScreen) {
//            binding.imageView.visibility = View.GONE
//            binding.imageView2.visibility = View.GONE
//            binding.imageView3.visibility = View.GONE
//        }
        binding.btnHospitalisation.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToHospitalisationFragment()
            findNavController().navigate(direction)
        }
        binding.Exercise.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToExerciseFragment()
            findNavController().navigate(directions)
        }
        binding.Ambulance.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToAmbulanceFragment()
            findNavController().navigate(directions)
        }

        val firstName = getFirstName(Userinfo.uname)
        val timeOfDay = getTimeOfDay()
        val greeting = getGreeting(firstName, timeOfDay)
        binding.tvUsername.text = greeting
        binding.btnAppointments.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToAppointmentFragment()
            findNavController().navigate(directions)
        }
        binding.btnHospitals.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToHospitalsFragment()
            view?.findNavController()?.navigate(direction)
        }
        binding.btnTests.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToBooktestsFragment()
            view?.findNavController()?.navigate(direction)
        }
        binding.btnCommunity.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToCommunityFragment()
            findNavController().navigate(directions)
        }
        binding.btnHelp.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToHelpFragment()
            view?.findNavController()?.navigate(direction)
        }
        binding.btnHealthpassport.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToHealthpassportFragment()
            view?.findNavController()?.navigate(direction)
        }
        binding.btnBlogs.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToBlogFragment()
            view?.findNavController()?.navigate(direction)
        }
        binding.rvUpcomingAppointment.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        getmyappointment()
        binding.rvUpcomingTests.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        getmytest()
        binding.profileIcon.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToHealthpassportFragment()
            view?.findNavController()?.navigate(direction)
        }
        carosel()
        binding.btnTestreports.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToTestFragment()
            view?.findNavController()?.navigate(direction)
        }
        return binding.root
    }
    fun carosel(){
        val carouselItems = listOf(
            carouseldata(R.drawable.carouselhealthscore),
            carouseldata(R.drawable.carouselhelp)
        )
        val carouselRecyclerView = binding.carouselRecyclerView
        carouselRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        val carouselAdapter = CarouselAdapter(carouselItems) { position ->
            // Handle item click
            val selectedItem = carouselItems[position]
            when(position){
                0->{
                    val direction = HomeFragmentDirections.actionHomeFragmentToPhysiqueFragment()
                    findNavController().navigate(direction)
                }
                1->{
                    val direction = HomeFragmentDirections.actionHomeFragmentToHelpFragment()
                    findNavController().navigate(direction)
                }
            }
        }
        carouselRecyclerView.adapter = carouselAdapter
        
    }
    fun getmyappointment(){
        val db = Firebase.firestore
        val appotlists : ArrayList<AppointmentData> =  ArrayList()
        val collectionRef = db.collection("appointments").orderBy("shldtime", Query.Direction.ASCENDING)
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val appointment = document.toObject(AppointmentData::class.java)
                    if (appointment.pid == Userinfo.userid && (appointment.shldtime.toDate().after(
                            Timestamp.now().toDate()))){
                        appotlists.add(appointment)
                    }
                }
                binding.rvUpcomingAppointment.adapter = UpcomingAppointmentAdapter(activity as Context?,appotlists,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
    fun getmytest(){
        val db = Firebase.firestore
        val appotlists : ArrayList<testbookingdata> =  ArrayList()
        val collectionRef = db.collection("testHistory").orderBy("shldtime", Query.Direction.ASCENDING)
        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val appointment = document.toObject(testbookingdata::class.java)
                    if (appointment.patientid == Userinfo.userid && (appointment.shldtime.toDate().after(
                            Timestamp.now().toDate()))){
                        appotlists.add(appointment)
                    }
                }
                binding.rvUpcomingTests.adapter = MyTestAdapter(activity as Context?,appotlists,this)
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting Hospitals documents: ", exception)
            }
    }
    fun getFirstName(username: String): String {
        val firstName = username.trim().split(" ")[0]
        return firstName
    }
    fun getTimeOfDay(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTimeString = dateFormat.format(currentTime)
        val currentTimeValue = dateFormat.parse(currentTimeString)
        val morningTime = dateFormat.parse("00:00")
        val afternoonTime = dateFormat.parse("12:00")
        val nightTime = dateFormat.parse("18:00")

        return when (currentTimeValue) {
            in morningTime..afternoonTime -> "morning"
            in afternoonTime..nightTime -> "afternoon"
            else -> "Evening"
        }
    }

    fun getGreeting(firstName: String, timeOfDay: String): String {
        return "Good $timeOfDay, $firstName !"
    }
    private fun backbuttonpressed(){
        val shouldInterceptBackPress = true
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(shouldInterceptBackPress){
                    activity!!.finishAffinity()
                }
                else{
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        })
    }
    private val REQUEST_SMS_PERMISSION = 123
    private val REQUEST_CALL_PERMISSION = 456

    // Function to send SMS with SOS message
    private fun sendSOSMessage(phoneNumber: String, message: String) {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
        }
    }

    // Function to make a call
    private fun makeCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
        }
    }



}