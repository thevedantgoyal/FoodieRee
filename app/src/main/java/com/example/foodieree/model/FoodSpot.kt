package com.example.foodieree.model

data class FoodSpot(
    val name: String,
    val lat: Double,
    val lon: Double,
    var distance: Double = 0.0
)
