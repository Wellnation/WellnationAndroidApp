package com.shubhasai.wellnation

import com.google.firebase.Timestamp

data class emergencyactiondata(
    val senderId:String = " ",
    val action:String = " ",
    val senderName:String = " ",
    val timestamp:Timestamp = Timestamp.now()
)
