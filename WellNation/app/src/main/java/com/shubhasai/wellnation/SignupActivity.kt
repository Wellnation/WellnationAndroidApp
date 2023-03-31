package com.shubhasai.wellnation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var firebaseregister: FirebaseAuth?=null
    private var uemail:String?=null
    private var umobile:String?=null
    private var upass:String?=null
    private var ucpass:String?=null
    private var uname:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            firebaseregister = FirebaseAuth.getInstance()
            uemail = binding.etremail.text.toString()
            uname = binding.etrname.text.toString()
            upass = binding.etrPass.text.toString()
            ucpass = binding.etrcPass.text.toString()
            umobile = binding.etrphonenumber.text.toString()
            if(upass != ucpass){
                Toast.makeText(this,"Password Doesn't Match", Toast.LENGTH_SHORT).show()
            }
            if(upass.isNullOrBlank()||ucpass.isNullOrBlank()||umobile.isNullOrBlank()||uemail.isNullOrBlank()||uname.isNullOrBlank()){
                Toast.makeText(this,"Enter every fields", Toast.LENGTH_SHORT).show()
            }
            else{
                register()
            }
        }
    }
    private fun register() {
        val name:String?= uname
        val email: String? = uemail
        val password: String? = upass
        val phone: String? = umobile
        if(email!!.isEmpty() || password!!.isEmpty() || name!!.isEmpty() ){
            Toast.makeText(this,"Enter All Fields", Toast.LENGTH_SHORT).show()
        }
        else{
            firebaseregister!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task->
                    if(task.isSuccessful){
                        val userdata =
                            phone?.let {
                                userdetails(firebaseregister!!.uid.toString(),name,email,
                                    it
                                )
                            }
                        val firebasedatabase = Firebase.firestore.collection("publicusers")
                        if (userdata != null) {
                            val user = hashMapOf(
                                "name" to userdata.name,
                                "userid" to userdata.userid,
                                "email" to userdata.email,
                                "phone" to userdata.phonenumber
                            )
                            firebasedatabase.document(userdata.userid).set(user).addOnCompleteListener {
                                checkemail()
                            }
                        }
                    } else{
                        Toast.makeText(this,email.toString()+password.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("SG","RG error:"+(task.exception!!.message))
                    }
                }L
        }
    }
    private fun checkemail(){
        val firebaseuser = Firebase.auth.currentUser
        firebaseuser?.sendEmailVerification()?.addOnCompleteListener { task->
            if(task.isSuccessful){
                Toast.makeText(this,"Verification Mail Sent. Check Your Inbox or Spam Folder",
                    Toast.LENGTH_LONG).show()
                firebaseregister?.signOut()
                startActivity(Intent(this,LoginActivity::class.java))
            }
            else{
                Toast.makeText(this,"Verification Mail Not Sent. Something Went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}