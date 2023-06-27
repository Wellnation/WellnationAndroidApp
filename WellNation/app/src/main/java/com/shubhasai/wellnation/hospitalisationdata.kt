package com.shubhasai.wellnation

import com.google.firebase.Timestamp


data class hospitalisationdata(
    val id: String = "",
    val bedId: String="",
    val dateAdmitted: Timestamp=Timestamp.now(),
    val dateReleased: Timestamp=Timestamp.now(),
    val hId: String="",
    val hName: String="",
    val pId: String="",
    val price: Int=0,
    val status: Boolean=true
)
