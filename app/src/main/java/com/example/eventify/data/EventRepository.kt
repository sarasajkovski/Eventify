package com.example.eventify.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

object EventRepository {
    private val db = FirebaseFirestore.getInstance()
    fun addEvent(event: Event) {
        db.collection("events")
            .add(event)
    }
    fun getEvents(onSuccess: (List<Event>) -> Unit) {
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val events = result.map { document ->
                    document.toObject(Event::class.java).copy(id = document.id)
                }
                onSuccess(events)
            }
    }
    fun deleteEvent(eventId: String) {
        db.collection("events")
            .document(eventId)
            .delete()
    }
    fun updateEvent(event: Event) {
        db.collection("events")
            .document(event.id)
            .set(event)
    }
    fun addFavorite(eventId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favoriteId = "${userId}_$eventId"
        db.collection("favorites")
            .document(favoriteId)
            .set(
                mapOf(
                    "userId" to userId,
                    "eventId" to eventId
                )
            )
    }
    fun removeFavorite(eventId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favoriteId = "${userId}_$eventId"
        db.collection("favorites")
            .document(favoriteId)
            .delete()
    }
    fun getFavoriteEvents(onSuccess: (List<String>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ids = result.mapNotNull {
                    it.getString("eventId")
                }
                onSuccess(ids)
            }
    }
    fun listenForEvents(
        onChange: (List<Event>) -> Unit
    ) {
        db.collection("events")
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val events = value.documents.map { document ->

                        document.toObject(Event::class.java)!!
                            .copy(id = document.id)
                    }
                    onChange(events)
                }
            }
    }



}