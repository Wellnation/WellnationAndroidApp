package com.shubhasai.wellnation

data class HospitalList(
    val name: String = "",
    val address:address = address(),
    val phone: String = "",
    val lat:String = "",
    val long:String = "",
    val uid:String = "",
    val rating:Float = 0.0f
) {

}
