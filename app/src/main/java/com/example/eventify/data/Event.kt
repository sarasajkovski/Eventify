package com.example.eventify.data

data class Event(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val category: String = "",
    val description: String  = "",
    var imagePaths: List<String> = emptyList(), // event add screen
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)