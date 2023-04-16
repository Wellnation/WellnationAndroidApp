package com.shubhasai.wellnation

data class room(
    var available: Boolean = false,
    var capacity: Int = 0,
    var cost: Int = 7000,
    var currAvail: Int = 10,
    var hospital: String = "FOrXDIPlOiNmLvezN77XvgPgP323",
    var patient: String = "",
    var total: Int = 15,
    var type: String = "Type A"
)
