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


class HomeFragment : Fragment() {
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
        binding.EmergencyContact.setOnClickListener {
            val sosPhoneNumber = Userinfo.emergencyphonenumber
            val sosMessage = "Help! I need assistance!"
            sendSOSMessage(sosPhoneNumber, sosMessage)
            makeCall(sosPhoneNumber)
        }
        binding.Exercise.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToExerciseFragment()
            findNavController().navigate(directions)
        }
        binding.Ambulance.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToAmbulanceFragment()
            findNavController().navigate(directions)
        }
        binding.tvUsername.text = "Hey, "+Userinfo.uname + "!"
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
        binding.btnDiseases.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToDiseasesFragment()
            view?.findNavController()?.navigate(direction)
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
        return binding.root
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