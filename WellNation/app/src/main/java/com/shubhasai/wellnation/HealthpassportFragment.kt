package com.shubhasai.wellnation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.shubhasai.wellnation.databinding.FragmentHealthpassportBinding
import java.io.File
import java.io.FileOutputStream

class HealthpassportFragment : Fragment() {
    private lateinit var binding: FragmentHealthpassportBinding
    private var firebaseauth: FirebaseAuth?=null
    var qrdata: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHealthpassportBinding.inflate(inflater, container, false)
        readUserDetails()
        binding.shareButton.setOnClickListener {
            sharehealthpassport()
        }
        return binding.root
    }

    private fun generateQRCode(data: String, size: Int, color: Int, context: Context): Bitmap? {
        try {
            val writer = QRCodeWriter()
            val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height

            // Create a bitmap with transparent background
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.TRANSPARENT)

            val logoDrawable = ContextCompat.getDrawable(context, R.drawable.wn_circle)
            val logoBitmap = logoDrawable?.let {
                val logoSize = size / 4 // Adjust the logo size as desired
                Bitmap.createScaledBitmap(drawableToBitmap(it), logoSize, logoSize, false)
            }
            val color = ContextCompat.getColor(context, R.color.Navy_Blue)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (bitMatrix.get(x, y)) {
                        bitmap.setPixel(x, y, color)
                    }

                    // Check if the pixel is within the logo area
                    if (logoBitmap != null && x >= width / 2 - logoBitmap.width / 2 &&
                        x < width / 2 + logoBitmap.width / 2 &&
                        y >= height / 2 - logoBitmap.height / 2 &&
                        y < height / 2 + logoBitmap.height / 2
                    ) {
                        // Calculate the corresponding position inside the logo
                        val logoPixelX = x - (width / 2 - logoBitmap.width / 2)
                        val logoPixelY = y - (height / 2 - logoBitmap.height / 2)

                        // Set the pixel color from the logo bitmap
                        bitmap.setPixel(x, y, logoBitmap.getPixel(logoPixelX, logoPixelY))
                    }
                }
            }

            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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
                    binding.userName.text = userdata?.name
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
                     qrdata = "name="+userdata?.name+"|"+"email="+userdata?.email+"|"+"phone="+userdata?.phone+"|"+"emergencynumber="+userdata?.emergencyNumber+"|"+"state="+userdata?.address?.state+"|"+"district="+userdata?.address?.district+"|"+"locality="+userdata?.address?.locality+"|"+"pincode="+userdata?.address?.pincode
                    userDetailsCollectionRef.document(Userinfo.userid).collection("vitals").document("info").get().addOnSuccessListener { documents ->
                        if (documents.exists()) {
                            val vitals = documents.toObject(vitals::class.java)
                            qrdata = qrdata+"|"+"bloodgroup="+vitals?.bloodgroup+"|"+"height="+vitals?.height+"|"+"weight="+vitals?.weight+"|"+"birthmark="+vitals?.birthmark+"|"+"diseases="+vitals?.diseases.toString()+"|"+"UserId="+Userinfo.userid

                            val bitmap =
                                activity?.let {
                                    generateQRCode(qrdata, 1000, Color.rgb(255,193,7),
                                        it
                                    )
                                }
                            binding.qrCode.setImageBitmap(bitmap)
                        }

                    }.addOnFailureListener { exception ->
                        Log.w("Firebase Execption", "Error getting user details", exception)
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("Firebase Execption", "Error getting user details", exception)
            }
        }
    }
    private fun getImageUri(bitmap: Bitmap): Uri {
        val imageFile = File(activity!!.cacheDir, "qr_image.png")
        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        return FileProvider.getUriForFile(activity!!, "com.shubhasai.wellnation.fileprovider", imageFile)
    }
    private fun sharehealthpassport(){
        //val qrBitmap = (binding.qrCode.drawable as BitmapDrawable).bitmap
        val qrBitmap = createBitmapFromView(binding.qrcardview)

// Create the share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*" // Set the MIME type to image
            putExtra(Intent.EXTRA_STREAM, getImageUri(qrBitmap)) // Add the QR code image as an attachment
            putExtra(Intent.EXTRA_TEXT, "Here's My Details: $qrdata") // Add some text as the message body
        }

// Show the share sheet
        startActivity(Intent.createChooser(shareIntent, "Share Your Wellnation Health Passport"))
    }
    fun createBitmapFromView(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}