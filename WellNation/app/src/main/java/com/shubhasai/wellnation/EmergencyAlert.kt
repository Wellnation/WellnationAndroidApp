package com.shubhasai.wellnation

data class EmergencyAlert(
    val userid: String = "",
    val latitute: String = "",
    val longitude: String = "",
    val time: String = "",
    val date: String = "",
    val status: String = "",
    val emergencyNumber: String = "",
    val address: String = "",
    val name: String = "",
    val phone: String = "",
    val splashActivity: SplashActivity = SplashActivity(),
)
