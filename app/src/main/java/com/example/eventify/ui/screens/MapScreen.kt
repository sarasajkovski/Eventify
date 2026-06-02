package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }

    LaunchedEffect(Unit) {
        EventRepository.listenForEvents {
            events = it
        }
    }
    val osijek = LatLng(45.5549, 18.6955)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(osijek, 13f)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(Color(0xFF120A1F), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Karta događaja u Osijeku",
                color = Color.White
            )
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = osijek),
                title = "Osijek",
                snippet = "Centar događaja"
            )
            events.forEach { event ->
                Marker(
                    state = MarkerState(position = osijek),
                    title = event.title,
                    snippet = event.location
                )
            }
        }
    }
}