package com.shubhasai.wellnation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.shubhasai.wellnation.databinding.FragmentAmbulanceBinding

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
                        //if location is null we will get new user location
                        //add new location function here
                        getNewLocation()

                    } else {
                        getambulancedata(location)
                        Toast.makeText(activity,"Your Location: "+location.longitude.toString()+","+location.latitude.toString(), Toast.LENGTH_SHORT).show()
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

        locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval=60*1000
        locationRequest.interval = 5*1000
        locationRequest.numUpdates= 2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }
    //create locationCallback variable
    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation : Location? = p0.lastLocation
            if (lastLocation != null) {
                getambulancedata(lastLocation)
                Toast.makeText(activity,lastLocation.latitude.toString(), Toast.LENGTH_SHORT).show()

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
                    val lat = document.getGeoPoint("currentlocation")?.latitude
                    val long = document.getGeoPoint("currentlocation")?.longitude
                    val id = document.getString("id")
                    var cost = document.get("cost").toString().toInt()
                    val status = document.getBoolean("status")
                    val vechiclenumber = document.getString("vechiclenumber")
                    val contact = document.getString("contact")
                    val location = GeoPoint(lat!!,long!!)
                    val distance = distance(location.latitude,location.longitude,loca.latitude,loca.longitude)
                    Log.d("data",distance.toString())
                    if (distance<dist && status==true){
                        googleMap.setOnInfoWindowClickListener { marker ->
                            val phoneNumber = "tel:${contact}"
                            val dialerIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
                            startActivity(dialerIntent)
                        }

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
                        val markerOptions = MarkerOptions()
                            .position(placeLatLng)
                            .title(text)
                            .icon(bitmapDescriptor)
                        googleMap.addMarker(markerOptions)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15f))
                    }
                    else{
                        Toast.makeText(activity,"No Ambulance Available", Toast.LENGTH_SHORT).show()
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
}