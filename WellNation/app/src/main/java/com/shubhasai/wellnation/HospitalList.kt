package com.shubhasai.wellnation

data class HospitalList(
    val name: String = "",
    val address:address = address(),
    val phone: String = "",
    val lat:String = "",
    val long:String = "",
    val uid:String = "",
    val rating:Float = 0.0f,
    val roomTypes:ArrayList<rooms> = ArrayList()
) {

}

data class rooms(
    val bed:Int = 0,
    val cost:String = "",
    val description:String = "",
    val type:String = "",

) {

}
