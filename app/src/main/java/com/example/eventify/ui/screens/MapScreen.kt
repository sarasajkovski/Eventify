package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventify.data.Event
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(
    event: Event,
    onBack: () -> Unit
) {
    val eventPosition = LatLng(
        event.latitude,
        event.longitude
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            eventPosition,
            15f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            Marker(
                state = rememberMarkerState(position = eventPosition),
                title = event.title,
                snippet = event.location
            )
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp
                )
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF120A1F))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Natrag",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF120A1F))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.location,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}