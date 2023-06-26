package com.shubhasai.wellnation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.shubhasai.wellnation.databinding.FragmentAmbulanceBinding
import com.shubhasai.wellnation.utils.DialogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class AmbulanceFragment : Fragment() {
    private lateinit var binding: FragmentAmbulanceBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private  lateinit var locationRequest: LocationRequest
    private  val pERMISSION_CODE = 100
    private var dist:Double = 1.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("fragment called","ambulance")
        fusedLocationProviderClient = activity?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }!!
        binding = FragmentAmbulanceBinding.inflate(layoutInflater)
        binding.radiusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the user's selection from the Spinner here
                val selectedRadius = parent?.getItemAtPosition(position).toString().replace(" km", "")
                dist = selectedRadius.toDouble()
                getLastLocation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
                getLastLocation()
            }
        }

        getLastLocation()
        binding.myFab.setOnClickListener {
            getLastLocation()
        }
        return binding.root
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

                        getNewLocation()

                    } else {
                        getambulancedata(location)
                    }

                }
            }else Toast.makeText(activity,"Please enable the Location Services", Toast.LENGTH_SHORT).show()
        }else RequestPermission()
    }

    //Function to check the user permissions
    private fun checkPermissions() :Boolean{
        return activity?.let { ActivityCompat.checkSelfPermission(it,android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity!!,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
    }

    //Function to check if location service of the device is enabled
    private fun isLocationEnabled(): Boolean {
        var locationManager : LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)

    }

    //Function that will allow us to get user permissions
    private fun RequestPermission(){
        activity?.let { ActivityCompat.requestPermissions(it, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),pERMISSION_CODE) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==pERMISSION_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the permission")
            }
        }
    }

    //Function to get new user location
    @SuppressLint("MissingPermission")
    private fun getNewLocation(){
        Log.d("fragment called","Newlocationrequested")
        locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval=1*1000
        locationRequest.interval = 5*1000
        locationRequest.numUpdates= 2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }
    //create locationCallback variable
    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation : Location? = p0.lastLocation
            if (lastLocation != null) {
                Log.d("get ambulance","with new location")
                getambulancedata(lastLocation)

            }
            else{
                Log.d("get ambulance",p0.toString())
            }
        }
    }
    fun getambulancedata(loca: Location){
        val db = FirebaseFirestore.getInstance().collection("ambulance")
        val mapView = binding.ambulancemapview
        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync { googleMap ->
            // Remove all existing markers from the map
            googleMap.clear()

            db.get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Ambulance",document.toString())
                    val lat = document.getGeoPoint("currentlocation")?.latitude
                    val long = document.getGeoPoint("currentlocation")?.longitude
                    val id = document.getString("id")
                    var cost = document.get("cost").toString().toInt()
                    val status = document.getBoolean("status")
                    val vechiclenumber = document.getString("vechilenumber")
                    val drivername = document.getString("driverName")
                    val contact = document.getString("contact")
                    val location = GeoPoint(lat!!,long!!)
                    val distance = distance(location.latitude,location.longitude,loca.latitude,loca.longitude)
                    Log.d("data",distance.toString())
                    if (distance<dist && status==true){
                        googleMap.clear()
                        Log.d("data","ambulance available")
                        cost *= distance.toInt()
                        val text = "Status: Available, Contact Number: $contact \n Vechicle Number: $vechiclenumber \n Cost: $cost"

                        // Add a new marker to the map
                        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.ambulance)

// Define the desired width and height of the resized bitmap
                        val width = 80
                        val height = 80

// Create a new bitmap with the desired width and height
                        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

// Create a BitmapDescriptor from the resized bitmap
                        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                        val placeLatLng = LatLng(location.latitude, location.longitude)
                        val userlocation = LatLng(loca.latitude, loca.longitude)
                        val markerOptions = MarkerOptions()
                            .position(placeLatLng)
                            .icon(bitmapDescriptor)
                        val circleOptions = CircleOptions()
                            .center(userlocation)
                            .radius(dist*1000)
                            .fillColor(R.color.Baby_Blue)
                        googleMap.addCircle(circleOptions)
                        googleMap.addMarker(markerOptions)
                       googleMap.setOnMarkerClickListener {marker->
                            val dialog = activity?.let { BottomSheetDialog(it) }
                            dialog?.setContentView(R.layout.ambulance_dialoglayout)
                            val vNumber = dialog?.findViewById<TextView>(R.id.tvambulancenumber)
                            val dName = dialog?.findViewById<TextView>(R.id.tvPilotname)
                            val tvdistance = dialog?.findViewById<TextView>(R.id.tvDistance)
                            val book = dialog?.findViewById<Button>(R.id.btnBookAmbulance)
                            val tvcost = dialog?.findViewById<TextView>(R.id.tvCost)
                            val call = dialog?.findViewById<ImageView>(R.id.btnCall)
                            vNumber?.text = vechiclenumber
                            dName?.text = drivername
                            tvdistance?.text = distance.roundToInt().toString() + " Km Approx"
                            val price = cost*distance
                            tvcost?.text = price.roundToInt().toString() + " Rs Approx"
                            call?.setOnClickListener{
                                val phoneNumber = "tel:${contact}"
                                val dialerIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
                                startActivity(dialerIntent)
                            }
                            book?.setOnClickListener {
                                val db = id?.let { it1 ->
                                    FirebaseFirestore.getInstance().collection("ambulance").document(
                                        it1
                                    )
                                }

                                db?.update("pickuplocation",GeoPoint(loca.latitude,loca.longitude))
                                db?.update("status",false)
                                db?.update("pid",Userinfo.userid)
                                db?.update("pidcontact",Userinfo.phonenumber)
                                db?.update("pfcmToken",Userinfo.fcmToken)
                                activity?.let { it1 -> DialogUtils.showLottieBottomSheetDialog(it1,R.raw.ambulance,"Booked Ambulance Successfully") }
                                if (id != null) {
                                    plotrode(id)
                                }
                            }
                            dialog?.show()
                            true
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15f))
                    }
                    else{
                        val userlocation = LatLng(loca.latitude, loca.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 15f))
                    }
                }
            }
        }
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val d = R * c // Distance in km
        return d
    }

    fun deg2rad(deg: Double): Double {
        return deg * (Math.PI /180)
    }
    private fun plotrode(ambulanceId:String){
        val db = FirebaseFirestore.getInstance().collection("ambulance").document(ambulanceId)
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
                                val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                                mapView.onCreate(null)
                                mapView.onResume()
                                mapView.getMapAsync { googleMap ->
                                    googleMap.clear()
                                    val markerOptions = MarkerOptions()
                                        .position(origin)
                                        .icon(bitmapDescriptor)
                                    googleMap.addMarker(markerOptions)
                                    val polylineOptions = PolylineOptions()
                                        .addAll(points)
                                        .color(Color.BLUE)
                                        .width(10f)
                                    googleMap.addPolyline(polylineOptions)
                                    googleMap.setMapStyle(activity?.let { it1 ->
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