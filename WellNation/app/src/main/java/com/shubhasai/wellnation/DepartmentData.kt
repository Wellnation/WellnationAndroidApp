package com.shubhasai.wellnation

import com.google.firebase.Timestamp

data class DepartmentData(
    var name: String = "",
    var doctors:ArrayList<doctordata> = ArrayList()
)

data class doctordata (
    var uid: String = "",
    var arrTime: Timestamp = Timestamp.now() ,
    var depTime: Timestamp = Timestamp.now()
        )
