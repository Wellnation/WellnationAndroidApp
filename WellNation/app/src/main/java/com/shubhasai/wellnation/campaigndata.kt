package com.shubhasai.wellnation

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class campaigndata(
    var name:String = "",
    var hid:String= "",
    var hospitalname:String="",
    var location:GeoPoint= GeoPoint(0.0,0.0),
    var cid:String="",
    var start:Timestamp = Timestamp.now(),
    var end:Timestamp = Timestamp.now()
)
