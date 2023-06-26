package com.shubhasai.wellnation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.StructuredQuery.Order
import com.shubhasai.wellnation.databinding.FragmentHelpBinding
import com.shubhasai.wellnation.utils.DialogUtils
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoLocation
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener
import java.lang.Math.*
import kotlin.math.roundToInt

class HelpFragment : Fragment() {
    private lateinit var binding: FragmentHelpBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private  lateinit var locationRequest: LocationRequest
    private  val pERMISSION_CODE = 100
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
        binding = FragmentHelpBinding.inflate(layoutInflater)
        getLastLocation()
        binding.floatingActionButton2.setOnClickListener {
            // Check if location permission is granted
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
                        getemergencylist(location)
                        Toast.makeText(activity,"Your Location: "+location.longitude.toString()+","+location.latitude.toString(), Toast.LENGTH_SHORT).show()
                    }


                }
            }else Toast.makeText(activity,"Please enable the Location Services", Toast.LENGTH_SHORT).show()
        }else RequestPermission()
    }

    //Function to check the user permissions
    private fun checkPermissions() :Boolean{
        return activity?.let { ActivityCompat.checkSelfPermission(it,android.Manifest.permission.ACCESS_FINE_LOCATION) } ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity!!,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
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
    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation : Location? = p0.lastLocation
            if (lastLocation != null) {
                getemergencylist(lastLocation)
                Toast.makeText(activity,lastLocation.latitude.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
    fun getemergencylist(loca:Location){
        val db = FirebaseFirestore.getInstance()
        var emergencylist:ArrayList<EmergencyAlert> = ArrayList()
        db.collection("emergency").orderBy("date",Query.Direction.ASCENDING).get().addOnSuccessListener {
            for (document in it){
                val lat = document.getGeoPoint("location")?.latitude
                val long = document.getGeoPoint("location")?.longitude
                val pid = document.getString("pid")
                val date = document.getTimestamp("date")
                val text = document.getString("text")
                val key = document.getString("emergencyId").toString()
                Log.d("key1",key)
                val location = GeoPoint(lat!!,long!!)
                val distance = distance(location.latitude,location.longitude,loca.latitude,loca.longitude)
                if(date != null && distance<5){
                    val emergency = EmergencyAlert(date,location,pid.toString(),text.toString())
                    emergencylist.add(emergency)
                    val mapView = binding.ambulancemapview
                    mapView.onCreate(null)
                    mapView.onResume()
                    mapView.getMapAsync { googleMap ->
                        // Initialize the Google Map object
                        val placeLatLng = LatLng(location.latitude, location.longitude)
                        val userLatLng = LatLng(loca.latitude, loca.longitude)
                        val circleOptions = CircleOptions()
                            .center(userLatLng)
                            .radius(5.0*1000)
                            .fillColor(R.color.Baby_Blue)
                        val markerOptions = MarkerOptions()
                        .position(placeLatLng)
                        googleMap.addMarker(markerOptions)
                        googleMap.addCircle(circleOptions)
                        googleMap.setMapStyle(activity?.let {
                            MapStyleOptions.loadRawResourceStyle(
                                it,R.raw.map_style)
                        })
                        googleMap.setOnMarkerClickListener {marker->
                            val dialog = activity?.let { BottomSheetDialog(it) }
                            dialog?.setContentView(R.layout.help_emergency_drawer)
                            val id = dialog?.findViewById<TextView>(R.id.emergencyId)
                            id?.text = "Emergency Id: "+key.toString()
                            Log.d("key",key)
                            val rv = dialog?.findViewById<RecyclerView>(R.id.DEmergencyLogRv)
                            val etText = dialog?.findViewById<EditText>(R.id.Detmsgl)
                            val btnSend = dialog?.findViewById<ImageView>(R.id.Dbtn_send)
                            val logs: ArrayList<emergencyactiondata> = ArrayList()
                            btnSend?.setOnClickListener {
                                val firebase = Firebase.firestore.collection("emergency")
                                val msg = etText?.text.toString()
                                val log = emergencyactiondata(Userinfo.userid,msg,Userinfo.uname,
                                    Timestamp.now())
                                firebase.document(key).collection("logs").document().set(log)
                            }
                            val firebase = key?.let { it1 ->
                                Firebase.firestore
                                    .collection("emergency")
                                    .document(it1)
                                    .collection("logs")
                                    .orderBy("timestamp",Query.Direction.ASCENDING)
                            }

                            if (firebase != null) {
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
                                        if (rv != null) {
                                            rv.layoutManager = LinearLayoutManager(activity)
                                            rv.adapter = EmergencyActionAdapter(logs)
                                        }

                                    }
                                }
                            }

                            if (rv != null) {

                            }

                            dialog?.show()
                            true
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15f))
                    }
                }
            }
        }
    }
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a = sin(dLat/2) * sin(dLat/2) +
                cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                sin(dLon/2) * sin(dLon/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        val d = R * c // Distance in km
        return d
    }

    fun deg2rad(deg: Double): Double {
        return deg * (PI/180)
    }
}