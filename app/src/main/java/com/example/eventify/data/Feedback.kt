package com.example.eventify.data

data class Feedback(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "Korisnik",
    val text: String = "",
    val rating: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
)