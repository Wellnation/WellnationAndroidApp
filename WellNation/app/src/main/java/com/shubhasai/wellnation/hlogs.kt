package com.shubhasai.wellnation

import com.google.firebase.Timestamp


data class hlogs(
    val doctors:ArrayList<doc> = ArrayList(),
    val meds:ArrayList<med> = ArrayList(),
    val tests:ArrayList<testlog> = ArrayList(),
    val logDate:Timestamp = Timestamp.now()
)
data class doc(
    val dId: String = "",
    val doctorName: String = "",
    val remark: String = "",
    val time: Timestamp=Timestamp.now(),
)

data class med(
    val name: String="",
    val price: String=""
)

data class testlog(
    val price: String="",
    val testId: String="",
    val type: String=""
)

