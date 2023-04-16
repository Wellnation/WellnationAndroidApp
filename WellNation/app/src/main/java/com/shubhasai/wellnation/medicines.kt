package com.shubhasai.wellnation

import java.sql.Time
import java.sql.Timestamp

data class medicines(
    var name:String = "",
    var remark:String = "",
    var time:ArrayList<time> = ArrayList(),
)

data class time(
    var hr:String = "0",
    var min:String = "0",
)
