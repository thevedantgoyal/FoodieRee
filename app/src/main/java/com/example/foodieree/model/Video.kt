package com.example.foodieree.model

data class Video(
    val url : String,
    val title : String,
    val location : String,
    var liked: Boolean = false
)
