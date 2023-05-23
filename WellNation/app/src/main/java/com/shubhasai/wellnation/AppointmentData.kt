package com.shubhasai.wellnation

import com.google.firebase.Timestamp
import java.sql.Time

data class AppointmentData(
    var apptId:String = "",
    var drid:String = "",
    var hid:String = "",
    var medicine:ArrayList<medicines> = ArrayList(),
    var pid:String = "",
    var remark:String = "",
    var reqtime:Timestamp = Timestamp.now(),
    var shldtime:Timestamp = Timestamp.now(),
    var status:Boolean = false,
    var symptoms:String = "",
    var dept:String = "",
    var onlinemode:Boolean = false
) {
}