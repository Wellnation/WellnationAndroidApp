package com.shubhasai.wellnation

data class bloodpressuredata(
    val sys:Float = 0f,
    val dia:Float = 0f,
    val time:com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()

)
