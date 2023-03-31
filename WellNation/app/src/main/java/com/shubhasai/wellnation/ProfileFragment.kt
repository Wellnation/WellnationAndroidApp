package com.shubhasai.wellnation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.GravityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubhasai.wellnation.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var userData: HashMap<String, Any> = HashMap()
    private lateinit var binding: FragmentProfileBinding
    private var firebaseauth: FirebaseAuth?=null
    var curuser : FirebaseUser? = firebaseauth?.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        readUserDetails()
        binding.profileDob.text = "Date of Birth: "+userData["dob"].toString()
        binding.profilePhone.text = "Phone: " + userData["phone"].toString()
        binding.profileAddress.text = "Address: " +userData["state"].toString() +","+ userData["district"].toString() +","+ userData["locality"].toString() +","+ userData["pincode"].toString()
        binding.profileEmail.text = "EmailId: " +Userinfo.email
        binding.profileName.text = Userinfo.uname
        binding.profileGender.text = "Gender: " + userData["gender"].toString()
        binding.profileBloodGroup.text = "Blood Group: " + userData["bloodgroup"].toString()
        binding.profileEmergencyContact.text = "Emergency Number: " +userData["emergencynumber"].toString()
        binding.profileBirthmark.text ="Birth Mark: " + userData["birthmark"].toString()
        binding.profileDiseases.text ="Diseases: " + userData["diseases"].toString()
        binding.editButton.setOnClickListener {
            openDrawerlayout()
        }
        return binding.root
    }

    fun readUserDetails(){
        val firestore = FirebaseFirestore.getInstance()

        val userDetailsCollectionRef = firestore.collection("publicusers")

        curuser?.let {
            userDetailsCollectionRef.document(Userinfo.userid).collection("userdetails").document("info").get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val dob = document.getString("dob") ?: " "
                    val phone = document.getString("phone") ?: " "
                    val state = document.getString("state") ?: " "
                    val district = document.getString("district") ?: " "
                    val locality = document.getString("locality") ?: " "
                    val pincode = document.getString("pincode") ?: " "
                    val gender = document.getString("gender") ?: " "
                    val bloodGroup = document.getString("bloodgroup") ?: " "
                    val emergencyNumber = document.getString("emergencynumber") ?: " "
                    val birthmark = document.getString("birthmark") ?: " "
                    val diseases = document.getString("diseases") ?: " "
                    userData.put("dob",dob)
                    userData.put("phone",phone)
                    userData.put("state",state)
                    userData.put("district",district)
                    userData.put("locality",locality)
                    userData.put("pincode",pincode)
                    userData.put("gender",gender)
                    userData.put("bloodgroup",bloodGroup)
                    userData.put("emergencynumber",emergencyNumber)
                    userData.put("birthmark",birthmark)
                    userData.put("diseases",diseases)
                }
            }.addOnFailureListener { exception ->
                Log.w("Firebase Execption", "Error getting user details", exception)
            }
        }

    }
    fun openDrawerlayout(){
        val builder = BottomSheetDialog(activity as Context)
        val  inflate = layoutInflater
        val dialogLayout = inflate.inflate(R.layout.editprofile_drawerlayout,null)
        val dob = dialogLayout.findViewById<EditText>(R.id.dob_edit_text)
        val phone = dialogLayout.findViewById<EditText>(R.id.phone_edit_text)
        val state = dialogLayout.findViewById<EditText>(R.id.state_edit_text)
        val dist = dialogLayout.findViewById<EditText>(R.id.district_edit_text)
        val locality = dialogLayout.findViewById<EditText>(R.id.locality_edit_text)
        val pincode = dialogLayout.findViewById<EditText>(R.id.pincode_edit_text)
        val gender = dialogLayout.findViewById<EditText>(R.id.gender_edit_text)
        val bloodgroup = dialogLayout.findViewById<EditText>(R.id.edit_bloodgroup)
        val emergencynumber = dialogLayout.findViewById<EditText>(R.id.emergency_contact_edit_text)
        val birthmark = dialogLayout.findViewById<EditText>(R.id.birthmark_edit_text)
        val diseases = dialogLayout.findViewById<EditText>(R.id.diseases_edit_text)
        val save = dialogLayout.findViewById<Button>(R.id.save_button)
        dob.setText(userData["dob"].toString())
        phone.setText(userData["phone"].toString())
        state.setText(userData["state"].toString())
        dist.setText(userData["district"].toString())
        locality.setText(userData["locality"].toString())
        pincode.setText(userData["pincode"].toString())
        gender.setText(userData["gender"].toString())
        bloodgroup.setText(userData["bloodgroup"].toString())
        emergencynumber.setText(userData["emergencynumber"].toString())
        birthmark.setText(userData["birthmark"].toString())
        diseases.setText(userData["diseases"].toString())
        save.visibility = View.INVISIBLE
        if (dob?.text.isNullOrEmpty() || phone?.text.isNullOrEmpty() || state?.text.isNullOrEmpty() ||
            dist?.text.isNullOrEmpty() || locality?.text.isNullOrEmpty() || pincode?.text.isNullOrEmpty() ||
            gender?.text.isNullOrEmpty() || bloodgroup?.text.isNullOrEmpty() ||
            emergencynumber?.text.isNullOrEmpty() || birthmark?.text.isNullOrEmpty() || diseases?.text.isNullOrEmpty()
        ) {
            save.visibility = View.INVISIBLE
        } else {
            save.visibility = View.VISIBLE
        }
        save.setOnClickListener {
            val firebasedatabase = Firebase.firestore.collection("publicusers")
            val userDetails = hashMapOf(
                "dob" to (dob?.text?.toString() ?: " "),
                "phone" to (phone?.text?.toString() ?: " "),
                "state" to (state?.text?.toString() ?: " "),
                "district" to (dist?.text?.toString() ?: " "),
                "locality" to (locality?.text?.toString() ?: " "),
                "pincode" to (pincode?.text?.toString() ?: " "),
                "gender" to (gender?.text?.toString() ?: " "),
                "bloodgroup" to (bloodgroup?.text?.toString() ?: " "),
                "emergencynumber" to (emergencynumber?.text?.toString() ?: " "),
                "birthmark" to (birthmark?.text?.toString() ?: " "),
                "diseases" to (diseases?.text?.toString() ?: " ")
            )
            firebasedatabase.document(Userinfo.userid).collection("UserDetails").document("info").set(userDetails).addOnSuccessListener {
                builder.dismiss()
            }.addOnFailureListener { exception ->
                Log.w("Firebase", "Error adding user details", exception)
            }
        }
        builder.setContentView(dialogLayout)
        builder.show()
    }
}
