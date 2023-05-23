package com.shubhasai.wellnation

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.shubhasai.wellnation.databinding.FragmentProfileBinding
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {
    private var userData: HashMap<String, Any> = HashMap()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var bottomDrawer: BottomSheetDialog
    private lateinit var scannerView: DecoratedBarcodeView
    private var firebaseauth: FirebaseAuth?=null
    private val REQUEST_CAMERA_PERMISSION = 200
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
        val adapter = ProfilepagerAdapter(childFragmentManager,lifecycle)
        binding.profileViewpager.adapter = adapter
        TabLayoutMediator(binding.profiletab,binding.profileViewpager){tab,position->
            when(position){
                0->{
                    tab.text="Vitals"
                }
                1->{
                    tab.text="Medicines"
                }
                2->{
                    tab.text="Appointments"
                }
            }
        }.attach()
        readUserDetails()
        binding.btnMyfamily.setOnClickListener {
            if(Userinfo.familyId == ""){
                showAlertDialog()
            }
            else{
                Log.d("family","else")
                loadfamily()
            }
        }
        return binding.root
    }

    private fun loadfamily() {
        Log.d("family","loadfamily")
        val db = FirebaseFirestore.getInstance().collection("publicusers")
        val members:ArrayList<userdetails> = ArrayList()
        db.get().addOnSuccessListener {
            for (document in it.documents){
                val member = document.toObject(userdetails::class.java)
                if (member != null) {
                    if (member.userid !=Userinfo.userid && member.familyId == Userinfo.familyId){
                        members.add(member)
                    }
                }
            }
            showbuttomsheet(members)
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(activity)

        alertDialogBuilder.setTitle("No family Added")
        alertDialogBuilder.setMessage("Do you want to")

        // Set positive button and its click listener
        alertDialogBuilder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            // Do something when the positive button is clicked
            val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
            db.update("familyId",Userinfo.userid+"x")
        })

        // Set negative button and its click listener
        alertDialogBuilder.setNegativeButton("Join", DialogInterface.OnClickListener { dialog, which ->
            // Do something when the negative button is clicked
            permission()
            startCameraPreview()

        })

        alertDialogBuilder.show()
    }


    private fun permission() {
        // Check if permission for camera is granted
        if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) }
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission for camera is already granted
            // Initialize and start the camera preview here
        } else {
            // Request permission for camera
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }
    }

    private fun startCameraPreview() {
        bottomDrawer = activity?.let { BottomSheetDialog(it) }!!
        val bottomDrawerView =
            LayoutInflater.from(activity).inflate(R.layout.buttom_qrscanner, null)
        bottomDrawer.setContentView(bottomDrawerView)

        scannerView = bottomDrawerView.findViewById(R.id.scannerView)
        scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    Log.d("QRCodeScanner", "Result: ${result.text}")
                    Toast.makeText(
                        activity,
                        "Scanned QR code Scanned Successfully. Adding You to the Family",
                        Toast.LENGTH_SHORT
                    ).show()
                    val userIdRegex = Regex("UserId=(\\w+)")
                    val matchResult = userIdRegex.find(result.text)

                    val userId = matchResult?.groupValues?.getOrNull(1)
                    if (userId != null) {
                        Log.d("text",userId)
                        val db = FirebaseFirestore.getInstance().collection("publicusers").document(userId)
                        db.get().addOnSuccessListener {
                            if(it!=null){
                                val userdata = it.toObject<userdetails>()
                                val familyId = userdata?.familyId
                                val dbrecp = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
                                dbrecp.update("familyId",familyId)
                                Toast.makeText(
                                    activity,
                                    "Added You to the Family Successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        bottomDrawer.dismiss()
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                // Do nothing for now
            }
        })

        scannerView.resume()

        bottomDrawer.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission for camera is granted
                // Initialize and start the camera preview here
                startCameraPreview()
            } else {
                Toast.makeText(
                    activity,
                    "Camera permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun readUserDetails(){
        val firestore = FirebaseFirestore.getInstance()

        val userDetailsCollectionRef = firestore.collection("publicusers")
        firebaseauth = FirebaseAuth.getInstance()
        var curuser : FirebaseUser? = firebaseauth?.currentUser
        if (curuser!=null){
            Userinfo.userid = curuser.uid
            userDetailsCollectionRef.document(Userinfo.userid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val userdata = document.toObject<userdetails>()
                    Userinfo.familyId = userdata?.familyId.toString()
                    binding.userName.text = userdata?.name
                    Log.d("familyId",Userinfo.familyId)
                    binding.userAddress.text = userdata?.address?.state+", "+userdata?.address?.district+", "+userdata?.address?.locality+", "+userdata?.address?.pincode
                    binding.userContactNumber.text = "Mobile: "+userdata?.phone
                    binding.userEmergencyContact.text = "Emergency Contact: "+userdata?.emergencyNumber
                    binding.userEmail.text = userdata?.email
                    if (userdata?.gender=="male"){
                        binding.profileIcon.setAnimation(R.raw.maleicon)
                        binding.profileIcon.playAnimation()
                    }
                    else if (userdata?.gender == "female"){
                        binding.profileIcon.setAnimation(R.raw.femaleicon)
                        binding.profileIcon.playAnimation()
                    }
                    else{
                        binding.profileIcon.setAnimation(R.raw.profileicon)
                        binding.profileIcon.playAnimation()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("Firebase Execption", "Error getting user details", exception)
            }
        }


    }
    fun showbuttomsheet(members:ArrayList<userdetails>){
        Log.d("family","buttom sheet called")
        val dialog = activity?.let { BottomSheetDialog(it) }
        dialog?.setContentView(R.layout.family_buttom_sheet)
        val rv = dialog?.findViewById<RecyclerView>(R.id.familyrv)
        if (rv != null) {
            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = FamilyMemberAdapter(members)
        }
        dialog?.show()
        true
    }
}
