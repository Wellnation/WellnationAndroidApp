package com.shubhasai.wellnation

import com.google.firebase.Timestamp

data class testbookingdata(
    var attatchment:String = "",
    var hid:String = "",
    var hname:String = "",
    var patientid:String = "",
    var pname:String = "",
    var reqtime:Timestamp = Timestamp.now(),
    var shldtime:Timestamp = Timestamp.now(),
    var status:Boolean = false,
    var tid:String = "",
    var tname:String = "",
)
