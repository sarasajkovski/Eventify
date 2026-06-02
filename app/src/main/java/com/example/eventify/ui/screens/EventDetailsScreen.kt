package com.example.eventify.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import com.example.eventify.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@Composable
fun EventDetailsScreen( event: Event, onDelete: () -> Unit, onEdit: () -> Unit) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isOwner = currentUserId == event.userId

    val galleryImages = listOf(
        "https://picsum.photos/300/300?1",
        "https://picsum.photos/300/300?2",
        "https://picsum.photos/300/300?3",
        "https://picsum.photos/300/300?4"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                event.imageUrl.ifEmpty { "https://picsum.photos/500/300" }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(22.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = event.title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(event.date, color = Color.LightGray)
        Text(event.location, color = Color.LightGray)
        Spacer(modifier = Modifier.height(18.dp))

        Text("0 događaju", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = event.description.ifEmpty { "Nema opisa za ovaj događaj." },
            color = Color.LightGray,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Fotografije",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(galleryImages) { image ->
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C2FF2)
            )
        ) {
            Text("Dodaj fotografiju")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Feedback posjetitelja",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A102B), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Korisnici ovdje mogu dodavati slike i dojmove s događaja.",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        ActionBox("Dodaj u kalendar") {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            }
            context.startActivity(intent)
        }
        Spacer(modifier = Modifier.height(12.dp))
        ActionBox("Podsjeti me") {
            NotificationHelper.createNotificationChannel(context)
            NotificationHelper.showNotification(
                context,
                "Podsjetnik za događaj",
                "${event.title} uskoro počinje!"
            )
        }
        if (isOwner) {
            Spacer(modifier = Modifier.height(12.dp))
            ActionBox("Uredi događaj") {
                onEdit()
            }
            Spacer(modifier = Modifier.height(12.dp))
            ActionBox("Obriši događaj", textColor = Color.Red) {
                EventRepository.deleteEvent(event.id)
                onDelete()
            }
        }
    }
}
@Composable
fun ActionBox(text: String, textColor: Color = Color.White, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A102B), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 16.sp)
    }
}