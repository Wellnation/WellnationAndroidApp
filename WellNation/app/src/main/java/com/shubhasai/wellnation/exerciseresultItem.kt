package com.shubhasai.wellnation

data class exerciseresultItem(
    val Aka: String,
    val Category: String,
    val Difficulty: String,
    val Force: String,
    val Grips: String,
    val details: String,
    val exercise_name: String,
    val id: Int,
    val steps: List<String>,
    val target: TargetX,
    val videoURL: List<String>,
    val youtubeURL: String
)