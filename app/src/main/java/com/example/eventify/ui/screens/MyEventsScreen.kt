package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import com.example.eventify.ui.components.EventCard

@Composable
fun MyEventsScreen(onEventClick: (Event) -> Unit) {

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    val currentUserId =
        FirebaseAuth.getInstance()
            .currentUser
            ?.uid

    LaunchedEffect(Unit) {
        EventRepository.listenForEvents { allEvents ->
            events = allEvents.filter { event ->
                event.userId == currentUserId
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Text(
            text = "Moji događaji",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Još nisi dodao/la nijedan događaj.",
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            }

        } else {
            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onClick = onEventClick
                    )
                }
            }
        }
    }
}