package com.shubhasai.wellnation

import com.google.firebase.firestore.GeoPoint

data class ambulancedata(
    val authpass: String = "",
    val contact: String = "",
    val cost: Int = 0,
    val currentlocation: GeoPoint = GeoPoint(0.0, 0.0),
    val driverName: String = "",
    val dropLocation: GeoPoint = GeoPoint(0.0, 0.0),
    val id: String = "",
    val pickupStatus: Boolean = false,
    val pickuplocation: GeoPoint = GeoPoint(0.0, 0.0),
    val status: Boolean = false,
    val vechilenumber: String = ""
)
