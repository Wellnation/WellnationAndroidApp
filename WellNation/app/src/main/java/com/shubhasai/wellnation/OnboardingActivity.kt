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
    private var promptsarray = arrayListOf<String>("Browse through a list of nearby hospitals, clinics and Doctors and See their ratings, reviews, and specialties","No stress of losing any health documents. Save all your Record at one place", "Sign Up and Enter to world to stay healthy")
    private var current_position:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseauth = FirebaseAuth.getInstance()
        var curuser : FirebaseUser? = firebaseauth?.currentUser
        if (curuser!=null){
            curuser.uid
            getuserdetails(curuser.uid)
        }
        binding.pbar.curValue = current_position+1
        binding.onboardingPrompts.text = promptsarray[current_position]
        binding.btnContinue.setOnClickListener {
            if(current_position<2){
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
    }
    fun getuserdetails(userId:String){
        val firestore = FirebaseFirestore.getInstance()
        // Replace "collectionPath" with your actual Firestore collection path
        val usersCollectionRef = firestore.collection("publicusers")

        usersCollectionRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Convert the document data to a User object
                    val user = document.toObject(userdetails::class.java)
                    Userinfo.email = user?.email.toString()
                    Userinfo.uname = user?.name.toString()
                    Userinfo.phonenumber = user?.phonenumber.toString()
                    Userinfo.userid = user?.userid.toString()
                    startActivity(Intent(this,MainActivity::class.java))
                } else {
                    Log.d("Firebase Execption", "No such document")
                }

            }
            .addOnFailureListener { exception ->
                Log.d("Firebase Execption", "Error getting document: $exception")
            }

    }
}

