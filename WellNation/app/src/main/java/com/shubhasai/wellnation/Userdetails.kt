package com.shubhasai.wellnation


data class userdetails(
    var userid:String = "",
    var name:String = "",
    var email:String= "",
    var phone:String="",
    var gender:String="",
    var dob:String="",
    var familyId:String ="",
    var emergencyNumber: String="",
    var wellcoin:Int = 0,
    var address: address = address(),
    var NormalizedHeartRate:Double = 0.0,
    var NormalizedHeartScore:Double = 0.0,
    var NormalizedSteps:Double = 0.0,
    var NormalizedLifestyle:Double = 0.0,
    var HealthScore:Double = 0.0
)
data class address(
    var state:String = "",
    var district:String = "",
    var locality:String= "",
    var pincode:String="",
)