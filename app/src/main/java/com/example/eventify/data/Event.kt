package com.example.eventify.data

data class Event(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val category: String = "",
    val description: String  = "",
    val imageUrl: String = "",
)