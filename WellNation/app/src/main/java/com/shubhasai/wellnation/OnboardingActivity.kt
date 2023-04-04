package com.shubhasai.wellnation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private var firebaseauth: FirebaseAuth?=null
    private lateinit var binding:ActivityOnboardingBinding
    private var promptsarray = arrayListOf<String>("Connect with the care you need, when you need it","Book appointments, tests, and consultations with ease", "Stay on top of your health with personalized medicine reminders!","Discover insightful health blogs and stay informed!","Empower yourself with a digital health passport and take control of your health!")
    private var current_position:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pbar.curValue = current_position+1
        binding.onboardingPrompts.text = promptsarray[current_position]
        binding.btnContinue.setOnClickListener {
            if(current_position<4){
                current_position++
                UpdateUnboardingPrompts()
            }
            else{
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
    fun UpdateUnboardingPrompts(){
        binding.pbar.curValue = current_position+1
        binding.onboardingPrompts.text = promptsarray[current_position]
        when(current_position){
            1 -> {
                binding.animationView.setAnimation(R.raw.sscreentwo)
                binding.animationView.playAnimation()
            }
            2 -> {
                binding.animationView.setAnimation(R.raw.sscreenthree)
                binding.animationView.playAnimation()
            }
            3 -> {
                binding.animationView.setAnimation(R.raw.sscreenfour)
                binding.animationView.playAnimation()
            }
            4 -> {
                binding.animationView.setAnimation(R.raw.sscreenfive)
                binding.animationView.playAnimation()
            }
        }
    }

}

