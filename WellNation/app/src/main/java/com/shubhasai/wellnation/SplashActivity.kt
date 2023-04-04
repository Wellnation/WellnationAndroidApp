package com.shubhasai.wellnation

import android.animation.*
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var firebaseauth: FirebaseAuth?=null
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_splash)
        firebaseauth = FirebaseAuth.getInstance()
        var curuser : FirebaseUser? = firebaseauth?.currentUser
        if (curuser!=null){
            curuser.uid
            getuserdetails(curuser.uid)
        }
        Handler().postDelayed({
            // Start your main activity after the delay
            if (curuser==null){
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            else{
                Userinfo.userid = curuser.uid
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

        }, 4000)
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
                    Userinfo.phonenumber = user?.phone.toString()
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