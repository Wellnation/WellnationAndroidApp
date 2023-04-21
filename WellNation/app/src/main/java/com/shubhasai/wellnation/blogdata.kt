package com.shubhasai.wellnation

import com.google.firebase.Timestamp

data class blogdata(
    val title: String = "",
    val body: String = "",
    val drid: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val imageurl: String = "",
    val drname: String = "",
    val blogid: String = ""
)
