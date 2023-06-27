package com.shubhasai.wellnation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.birjuvachhani.locus.Locus
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.shubhasai.wellnation.databinding.ActivityEmergencyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmergencyActivity : AppCompatActivity() {
    private var firebaseauth: FirebaseAuth?=null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private  lateinit var locationRequest: LocationRequest
    private  val pERMISSION_CODE = 100
    private lateinit var binding:ActivityEmergencyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(this)
        Log.d("userid",Userinfo.userid.toString())
        firebaseauth = FirebaseAuth.getInstance()
        var curuser : FirebaseUser? = firebaseauth?.currentUser
        if (curuser!=null){
            Userinfo.userid = curuser.uid
        }
        readUserDetails()
        binding.btnSend.setOnClickListener {
            val firebase = Firebase.firestore.collection("emergency")
            val msg = binding.etmsgl.text.toString()
            val log = emergencyactiondata(Userinfo.userid,msg,Userinfo.uname,Timestamp.now())
            firebase.document(Userinfo.emergencykey).collection("logs").document().set(log)
        }
        binding.btnSwitchOff.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                val firebase = Firebase.firestore.collection("emergency").document(Userinfo.emergencykey)
                firebase.update("isLive",false)
            }
            else{
                val firebase = Firebase.firestore.collection("emergency").document(Userinfo.emergencykey)
                firebase.update("isLive",true)
            }
        }

        //getlogs(Userinfo.emergencykey)
    }

    fun readUserDetails() {
        val firestore = FirebaseFirestore.getInstance()
        val userDetailsCollectionRef = firestore.collection("publicusers")
        userDetailsCollectionRef.document(Userinfo.userid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(userdetails::class.java)
                if (user != null) {
                    Userinfo.userid = user.userid
                    Userinfo.email = user.email
                    Userinfo.uname = user.name
                    Userinfo.phonenumber = user.phone
                }
                getLastLocation()
            }
        }.addOnFailureListener { exception ->
            Log.w("Firebase Execption", "Error getting user details", exception)
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        //check for the permissions
        if (checkPermissions()){
            //check if location service is enabled
            if (isLocationEnabled()){
                //lets get the location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        //if location is null we will get new user location
                        //add new location function here
                        getNewLocation()

                    } else {
                        sendalert(location)
                    }


                }
            }else Toast.makeText(this,"Please enable the Location Services",Toast.LENGTH_SHORT).show()
        }else RequestPermission()
    }

    //Function to check the user permissions
    private fun checkPermissions() :Boolean{
        return ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }

    //Function to check if location service of the device is enabled
    private fun isLocationEnabled(): Boolean {
        var locationManager :LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    //Function that will allow us to get user permissions
    private fun RequestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),pERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==pERMISSION_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the permission")
            }
        }
    }

    //Function to get new user location
    @SuppressLint("MissingPermission")
    private fun getNewLocation(){

        locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval=60*1000
        locationRequest.interval = 5*1000
        locationRequest.numUpdates= 2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }
    //create locationCallback variable
    private val locationCallback = object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation : Location? = p0.lastLocation
            if (lastLocation != null) {
                Toast.makeText(this@EmergencyActivity,lastLocation.latitude.toString(),Toast.LENGTH_SHORT).show()
                sendalert(lastLocation)

            }
        }
    }
    fun sendalert(location:Location){
        val firebase = Firebase.firestore.collection("emergency")
        val key = firebase.document().id
        val alert = EmergencyAlert(Timestamp.now(), GeoPoint(location.latitude,location.longitude),Userinfo.userid,"Help Needed","", emergencyId = key, liveStatus = true)
        firebase.document(key).set(alert).addOnSuccessListener {
            Toast.makeText(this,"Alert has Been Sent",Toast.LENGTH_LONG).show()
            val log = emergencyactiondata(Userinfo.userid,"I am in Emergency",Userinfo.uname,Timestamp.now())
            firebase.document(key).collection("logs").document().set(log)
            Userinfo.emergencykey = key
            getlogs(key)
            getambulance(key)
        }

    }
    fun getlogs(key: String) {
        val logs: ArrayList<emergencyactiondata> = ArrayList()
        val firebase = Firebase.firestore
            .collection("emergency")
            .document(key)
            .collection("logs")
            .orderBy("timestamp",Query.Direction.ASCENDING)

        firebase.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                logs.clear()
                for (log in querySnapshot.documents) {
                    val data = log.toObject(emergencyactiondata::class.java)
                    data?.let { logs.add(it) }
                }
                binding.EmergencyLogRv.layoutManager = LinearLayoutManager(this)
                binding.EmergencyLogRv.adapter = EmergencyActionAdapter(logs)
            }
        }
    }

    fun getambulance(key: String){
        val firebase = Firebase.firestore
            .collection("emergency")
            .document(key).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                   val ambulanceid =  querySnapshot.get("ambulanceId").toString()
                    if (ambulanceid!=""){
                        Log.d("emergencymap",ambulanceid)
                        plotrode(ambulanceid)
                    }

                }
            }
    }
    fun plotrode(ambulanceId:String){
        val db = FirebaseFirestore.getInstance().collection("ambulance").document(ambulanceId)
        Log.d("emergencymap",ambulanceId)
        db.get().addOnSuccessListener {
            var origin = LatLng(37.422, -122.084) // Replace with your origin
            var destination = LatLng(37.7749, -122.4194) // Replace with your destination
            if (it != null) {
                val pickupLocation = it.getGeoPoint("currentlocation")
                if (pickupLocation != null) {
                    destination = LatLng(pickupLocation.latitude,pickupLocation.longitude)
                }
                var mylocation = it.getGeoPoint("pickuplocation")
                if (mylocation != null) {
                    origin = LatLng(mylocation.latitude,mylocation.longitude)
                }
                val status = it.getBoolean("status")
                if (!status!!){
                    val apiKey = "AIzaSyAOjr36nWK1pfruFvU8w49Pb_BKZSmWlYk" // Replace with your Google Maps API key
                    val geoApiContext = GeoApiContext.Builder()
                        .apiKey(apiKey)
                        .build()

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val directionsResult: DirectionsResult = DirectionsApi
                                .newRequest(geoApiContext)
                                .origin(origin.latitude.toString() + "," + origin.longitude)
                                .destination(destination.latitude.toString() + "," + destination.longitude)
                                .await()

                            withContext(Dispatchers.Main) {
                                val distanceInMeters = directionsResult.routes[0].legs[0].distance.inMeters
                                val distanceInKm = distanceInMeters / 1000
                                val durationInSeconds = directionsResult.routes[0].legs[0].duration.inSeconds
                                val durationInMinutes = durationInSeconds / 60
                                val points = mutableListOf<LatLng>()
                                for (step in directionsResult.routes[0].legs[0].steps) {
                                    points.addAll(PolyUtil.decode(step.polyline.encodedPath))
                                }
                                Log.d("lists",points.toString())
                                val mapView = binding.ambulancemapview
                                val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.ambulance)

// Define the desired width and height of the resized bitmap
                                val width = 80
                                val height = 80

// Create a new bitmap with the desired width and height
                                val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

// Create a BitmapDescriptor from the resized bitmap
                                //val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                                mapView.onCreate(null)
                                mapView.onResume()
                                mapView.getMapAsync { googleMap ->
                                    googleMap.clear()
                                    val markerOptions = MarkerOptions()
                                        .position(origin)
                                    googleMap.addMarker(markerOptions)
                                    val polylineOptions = PolylineOptions()
                                        .addAll(points)
                                        .color(Color.BLUE)
                                        .width(10f)
                                    googleMap.addPolyline(polylineOptions)
                                    googleMap.setMapStyle(this@EmergencyActivity?.let { it1 ->
                                        MapStyleOptions.loadRawResourceStyle(
                                            it1,R.raw.map_style)
                                    })
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.d("Error",e.toString())
                        }
                    }
                }
                else{

                }

            }
        }
    }
}