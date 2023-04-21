package com.shubhasai.wellnation

import com.google.firebase.firestore.GeoPoint

data class ambulancedata(
    val cost:Int = 0,
    val currentlocation: GeoPoint = GeoPoint(0.0,0.0),
    val id:String = "",
    val status:Boolean = false,
    val vechilenumber:String = ""
)
