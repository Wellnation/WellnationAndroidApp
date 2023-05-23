package com.shubhasai.wellnation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.shubhasai.wellnation.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var firebaseauth: FirebaseAuth?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseauth = FirebaseAuth.getInstance()
        binding.btnGotoregister.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            loginuser()
        }
        setContentView(binding.root)
    }
    fun loginuser(){
        val email = binding.etemail.text.toString()
        val password = binding.etPass.text.toString()
        if(email.isEmpty() ){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        }
        else if( password.isEmpty()){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
        }
        else{
            Log.d("called","geting called")
            firebaseauth?.signInWithEmailAndPassword(email,password)?.addOnCompleteListener{
                if(it.isSuccessful){
                    Log.d("called","Authentication Successful")
                    val curuser = firebaseauth!!.uid
                    if (curuser != null) {
                        Userinfo.userid = curuser
                    }
                    varifyemail()
//                        finish()
                }
                else{
                    Log.d("called","Authentication failed: ${it.exception.toString()}")
                    Toast.makeText(this, "Enter Correct Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun varifyemail(){
        Log.d("called","email verifing")
        val firebaseuser = FirebaseAuth.getInstance().currentUser
        val vmail = firebaseuser!!.isEmailVerified
        if(vmail){
            startActivity(Intent(this,MainActivity::class.java))
        }
        else{
            Toast.makeText(this,"Verify Email", Toast.LENGTH_SHORT).show()
            firebaseauth?.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}