package com.example.eventify.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

object EventRepository {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    fun addEvent(event: Event, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("events")
            .add(event)
            .addOnSuccessListener {onSuccess()}
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                onError(
                    exception.message ?: "Greška pri dodavanju događaja."
                )
            }

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
    fun listenForEvents(onChange: (List<Event>) -> Unit) {
        db.collection("events")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                if (value != null) {
                    val events = value.documents.mapNotNull { document ->
                        try {
                            document.toObject(Event::class.java)?.copy(id = document.id)
                        } catch(e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                    onChange(events)
                }
            }
    }

    fun addFeedback(feedback: Feedback, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val feedbackData = mapOf(
            "eventId" to feedback.eventId,
            "userId" to feedback.userId,
            "userName" to feedback.userName,
            "text" to feedback.text,
            "rating" to feedback.rating,
            "createdAt" to feedback.createdAt
        )
        db.collection("events")
            .document(feedback.eventId)
            .collection("feedback")
            .add(feedbackData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(
                    exception.message
                        ?: "Greška pri spremanju feedbacka."
                )
            }
    }
    fun getFeedback(eventId: String, onSuccess: (List<Feedback>) -> Unit, onError: (String) -> Unit) {
        db.collection("events")
            .document(eventId)
            .collection("feedback")
            .get()
            .addOnSuccessListener { result ->
                val feedbackList = result.documents.mapNotNull { document ->
                        Feedback(
                            id = document.id,
                            eventId = document.getString("eventId") ?: "",
                            userId = document.getString("userId") ?: "",
                            userName = document.getString("userName") ?: "Korisnik",
                            text = document.getString("text") ?: "",
                            rating = document.getLong("rating")?.toInt() ?: 5,
                            createdAt = document.getLong("createdAt") ?: 0L
                        )
                    }.sortedByDescending {
                        it.createdAt
                    }
                onSuccess(feedbackList)
            }
            .addOnFailureListener { exception ->
                onError(
                    exception.message
                        ?: "Greška pri učitavanju feedbacka."
                )
            }
    }

    fun deleteFeedback(eventId: String, feedbackId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("events")
            .document(eventId)
            .collection("feedback")
            .document(feedbackId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(
                    exception.message ?: "Greška pri brisanju feedbacka."
                )
            }
    }
}