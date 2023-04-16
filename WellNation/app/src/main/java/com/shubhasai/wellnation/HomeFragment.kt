package com.shubhasai.wellnation

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.shubhasai.wellnation.databinding.FragmentHomeBinding


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
}