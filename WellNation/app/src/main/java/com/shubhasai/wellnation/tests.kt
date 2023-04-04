package com.shubhasai.wellnation

import com.google.firebase.firestore.GeoPoint

data class tests(
    val testname: String ="",
    val testprice: String ="0",
    val testid: String = "",
    val hid: String = "",
    val location: GeoPoint = GeoPoint(0.0,0.0),
    var hospitalname: String = ""
)
