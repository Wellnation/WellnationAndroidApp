package com.shubhasai.wellnation

data class sugarrecord(
    val fasting:Float = 0f,
    val postmeal:Float = 0f,
    val time:com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
