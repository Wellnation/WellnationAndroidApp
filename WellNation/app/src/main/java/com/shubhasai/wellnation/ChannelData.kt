package com.shubhasai.wellnation

import com.google.firebase.Timestamp

data class Channel(

val message: String = "",
val createdBy: String = "",
val createdAt: Timestamp = Timestamp.now(),
val docId: String ="",
val name:String = ""
)
