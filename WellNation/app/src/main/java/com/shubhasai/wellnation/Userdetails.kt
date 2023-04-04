package com.shubhasai.wellnation


data class userdetails(
    var userid:String = "",
    var name:String = "",
    var email:String= "",
    var phone:String="",
    var gender:String="",
    var dob:String="",
    var emergencyNumber: String="",
    var address: address = address(),

)
data class address(
    var state:String = "",
    var district:String = "",
    var locality:String= "",
    var pincode:String="",
)