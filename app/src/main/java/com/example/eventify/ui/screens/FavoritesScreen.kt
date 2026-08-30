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
fun FavoritesScreen(onEventClick: (Event) -> Unit) {
    var favoriteIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        EventRepository.getFavoriteEvents { ids ->
            favoriteIds = ids
        }
        EventRepository.getEvents {
            events = it
            isLoading = false
        }
    }
    val favoriteEvents = events.filter {
        favoriteIds.contains(it.id)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Text(
            text = "Omiljeno",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(18.dp))
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFF6C2FF2)
            )
        } else if (favoriteEvents.isEmpty()) {
            Text(
                text = "Još nemaš spremljenih događaja.",
                color = Color.LightGray,
                fontSize = 16.sp
            )
        } else {
            LazyColumn {
                items(favoriteEvents) { event ->
                    EventCard(
                        event = event,
                        onClick = {onEventClick(event)},
                        isFavorite = true,
                        onFavoriteClick = { selectedEvent ->
                            EventRepository.removeFavorite(selectedEvent.id)
                            favoriteIds = favoriteIds - selectedEvent.id
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}