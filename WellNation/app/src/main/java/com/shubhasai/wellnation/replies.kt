package com.shubhasai.wellnation

import com.google.firebase.Timestamp

data class replies(
    val createdAt:Timestamp = Timestamp.now(),
    val createdBy:String = "",
    val message:String = "",
    val name:String = ""

)
