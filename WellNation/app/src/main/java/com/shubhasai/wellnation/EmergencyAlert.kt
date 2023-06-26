package com.shubhasai.wellnation

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class EmergencyAlert(
   val date:Timestamp = Timestamp.now(),
   val location:GeoPoint = GeoPoint(0.00,0.00),
   val pid:String = "",
   val text:String = "Help Needed",
   val ambulanceId:String = "",
   val emergencyId:String = " ",
   val liveStatus:Boolean =true
)
