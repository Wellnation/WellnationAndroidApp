package com.shubhasai.wellnation

import java.sql.Timestamp

data class bmirecord(
    val weight:Float = 0f,
    val height:Float = 0f,
    val time:com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
