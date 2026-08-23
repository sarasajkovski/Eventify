package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import com.example.eventify.ui.components.EventCard

@Composable
fun EventsScreen(onEventClick: (Event) -> Unit) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var favoriteIds by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        EventRepository.listenForEvents { firebaseEvents ->
            events = firebaseEvents
            isLoading = false
        }
        EventRepository.getFavoriteEvents { ids -> favoriteIds = ids }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Text(
            text = "Popis događaja",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(35.dp))
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFF6C2FF2)
            )
        } else if (events.isEmpty()) {
            Text(
                text = "Nema događaja.",
                color = Color.LightGray
            )
        } else {
            LazyColumn {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onClick = onEventClick,
                        isFavorite = favoriteIds.contains(event.id),
                        onFavoriteClick = { selectedEvent ->
                            if (favoriteIds.contains(selectedEvent.id)) {
                                EventRepository.removeFavorite(selectedEvent.id)
                                favoriteIds =
                                    favoriteIds - selectedEvent.id
                            } else {
                                EventRepository.addFavorite(selectedEvent.id)
                                favoriteIds =
                                    favoriteIds + selectedEvent.id
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}