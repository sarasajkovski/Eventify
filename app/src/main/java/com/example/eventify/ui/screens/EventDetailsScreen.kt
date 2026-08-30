package com.example.eventify.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.eventify.data.Feedback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import java.io.File

@Composable
fun EventDetailsScreen( event: Event, onBack: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit, onShare: () -> Unit, onMapClick: () -> Unit) {

    var currentEvent by remember { mutableStateOf(event) }
    LaunchedEffect(event.id) {
        EventRepository.listenForEvents { events ->
            val updatedEvent = events.find { it.id == event.id }
            if (updatedEvent != null) {
                currentEvent = updatedEvent
            }
        }
    }

    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isOwner = currentUserId == currentEvent.userId
    var showDeleteDialog by remember { mutableStateOf(false) }

    var feedbackList by remember {
        mutableStateOf<List<Feedback>>(emptyList())
    }
    val averageRating =
        if (feedbackList.isNotEmpty()) {
            feedbackList.map { it.rating }.average()
        } else {
            0.0
        }
    var feedbackText by remember { mutableStateOf("") }
    var showFeedback by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableIntStateOf(5) }
    var feedbackError by remember { mutableStateOf("") }
    var isSendingFeedback by remember { mutableStateOf(false) }

    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var localGalleryImages by remember { mutableStateOf(currentEvent.imagePaths) }
    LaunchedEffect(currentEvent.imagePaths) {
        localGalleryImages = currentEvent.imagePaths
    }
    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents()
        ) { uris ->
            try {
                val newPaths = uris.mapNotNull { uri ->
                    try {
                        val inputStream =
                            context.contentResolver
                                .openInputStream(uri)
                                ?: return@mapNotNull null
                        val file = File(
                            context.filesDir,
                            "event_${event.id}_${System.currentTimeMillis()}_${System.nanoTime()}.jpg"
                        )
                        inputStream.use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        file.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                val updatedImages = localGalleryImages + newPaths
                localGalleryImages = updatedImages
                val updatedEvent = currentEvent.copy(imagePaths = updatedImages)
                EventRepository.updateEvent(updatedEvent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun deleteGalleryImage(imagePath: String) {
        try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            }
            val updatedGallery =
                localGalleryImages.filter {
                    it != imagePath
                }
            localGalleryImages = updatedGallery
            val updatedEvent = currentEvent.copy(
                imagePaths = updatedGallery
            )
            EventRepository.updateEvent(updatedEvent)
            selectedImagePath = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(currentEvent.id) {
        EventRepository.getFeedback(
            eventId = currentEvent.id,
            onSuccess = { feedbacks ->
                feedbackList = feedbacks },
            onError = { error ->
                feedbackError = error }
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F)),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹",
                    color = Color.White,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable {
                            onBack()
                        }
                )
                Text(
                    text = currentEvent.title,
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Text(
                    text = "⇧",
                    color = Color.White,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable {
                        onShare()
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentEvent.imagePaths.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = File(currentEvent.imagePaths.first())
                        ),
                        contentDescription = "Glavna fotografija događaja",
                        modifier = Modifier
                            .width(145.dp)
                            .height(125.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable {
                                selectedImagePath = currentEvent.imagePaths.first()
                            },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .width(145.dp)
                            .height(125.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFF1A102B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nema fotografije",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(18.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentEvent.date,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(19.dp))
                    Text(
                        text = currentEvent.location,
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "★",
                            color = Color(0xFFFFC107),
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (feedbackList.isNotEmpty()) {
                                "%.1f".format(averageRating)
                            } else {
                                "Nema recenzija"
                            },
                            color = Color.LightGray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(),
            ) {
                EventTab(
                    text = "O događaju",
                    selected = selectedTab == 0,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedTab = 0
                }
                EventTab(
                    text = "Fotografije",
                    selected = selectedTab == 1,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedTab = 1
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectedTab == 0) {
            item {
                Text(
                    text = "Opis događaja",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = currentEvent.description.ifEmpty {
                        "Nema opisa za ovaj događaj."
                    },
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Text(
                    text = "Lokacija",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(15.dp))

                val eventLocation = LatLng(
                    currentEvent.latitude,
                    currentEvent.longitude
                )
                val eventCameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        eventLocation,
                        14f
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            onMapClick()
                        }
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = eventCameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false,
                            mapToolbarEnabled = false
                        )
                    ) {
                        Marker(
                            state = rememberUpdatedMarkerState(position = eventLocation),
                            title = currentEvent.title

                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Color.Black.copy(alpha = 0.6f)
                            )
                            .padding(12.dp),
                    ) {
                        Text(
                            text = "Otvori kartu",
                            color = Color.White,
                            fontSize = 17.sp,
                            modifier = Modifier.clickable {
                                onMapClick()
                            }
                        )
                    }
                }
            }

            item {
                ActionBox(
                    backgroundColor = Color(0xFF6C2FF2),
                    text = if (showFeedback)
                        "Feedback posjetitelja  ˄"
                    else
                        "Feedback posjetitelja  ˅"
                ) {
                    showFeedback = !showFeedback
                }
            }

            if (showFeedback) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tvoja ocjena",
                        color = Color.LightGray,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (star in 1..5) {
                            Text(
                                text = "★",
                                color =
                                    if (star <= selectedRating)
                                        Color(0xFFFFC107)
                                    else
                                        Color(0xFF4A4055),
                                fontSize = 28.sp,
                                modifier = Modifier.clickable {
                                    selectedRating = star
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = {
                            feedbackText = it
                            feedbackError = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                text = "Napiši svoj dojam...",
                                color = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6C2FF2),
                            unfocusedBorderColor = Color(0xFF2C203A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (feedbackError.isNotEmpty()) {
                        Text(
                            text = feedbackError,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            if (feedbackText.isBlank()) {
                                feedbackError = "Napiši svoj dojam."
                                return@Button
                            }
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user == null) {
                                feedbackError = "Moraš biti prijavljen/a."
                                return@Button
                            }
                            isSendingFeedback = true
                            feedbackError = ""

                            val feedback = Feedback(
                                eventId = currentEvent.id,
                                userId = user.uid,
                                userName = user.displayName
                                    ?: user.email
                                    ?: "Korisnik",
                                text = feedbackText.trim(),
                                rating = selectedRating
                            )

                            EventRepository.addFeedback(
                                feedback = feedback,
                                onSuccess = {
                                    isSendingFeedback = false
                                    feedbackText = ""
                                    selectedRating = 5

                                    EventRepository.getFeedback(
                                        eventId = currentEvent.id,
                                        onSuccess = { feedbacks ->
                                            feedbackList = feedbacks
                                        },
                                        onError = { error ->
                                            feedbackError = error
                                        }
                                    )
                                },
                                onError = { error ->
                                    isSendingFeedback = false
                                    feedbackError = error
                                }
                            )
                        },
                        enabled = !isSendingFeedback,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C2FF2)
                        )
                    ) {
                        if (isSendingFeedback) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Pošalji feedback"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                }
                items(feedbackList) { feedback ->
                    FeedbackCard(
                        feedback = feedback,
                        currentUserId = currentUserId,
                        onDelete = {
                            EventRepository.deleteFeedback(
                                eventId = currentEvent.id,
                                feedbackId = feedback.id,
                                onSuccess = {
                                    feedbackList = feedbackList.filter {
                                        it.id != feedback.id
                                    }
                                },
                                onError = { error ->
                                    feedbackError = error
                                }
                            )
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                ActionBox(
                    text = "Dodaj u kalendar",
                    textColor = Color.Gray
                ) {
                    val intent =
                        Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI

                            putExtra(
                                CalendarContract.Events.TITLE,
                                currentEvent.title
                            )

                            putExtra(
                                CalendarContract.Events.EVENT_LOCATION,
                                currentEvent.location
                            )

                            putExtra(
                                CalendarContract.Events.DESCRIPTION,
                                currentEvent.description
                            )
                        }

                    context.startActivity(intent)
                }
            }

            item {
                ActionBox(
                    text = "Podsjeti me",
                    textColor = Color.Gray
                ) {
                    NotificationHelper.createNotificationChannel(context)

                    NotificationHelper.showNotification(
                        context,
                        "Podsjetnik za događaj",
                        "${currentEvent.title} uskoro počinje!"
                    )
                }
            }

            if (isOwner) {
                item {
                    ActionBox(
                        text = "Uredi događaj"
                    ) {
                        onEdit()
                    }
                }
                item {
                    ActionBox(
                        text = "Obriši događaj",
                        textColor = Color.Red
                    ) {
                        showDeleteDialog = true
                    }
                }
            }
        } else {
            item {
                if (localGalleryImages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Color(0xFF1A102B),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Još nema fotografija",
                            color = Color.Gray
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        localGalleryImages
                            .chunked(2)
                            .forEach { rowImages ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowImages.forEach { imagePath ->
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = File(imagePath)
                                            ),
                                            contentDescription = "Fotografija događaja",
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(150.dp)
                                                .clip(
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .clickable {
                                                    selectedImagePath = imagePath
                                                },
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (rowImages.size == 1) {
                                        Spacer(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(150.dp)
                                        )
                                    }
                                }
                            }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        imagePicker.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C2FF2)
                    )
                ) {
                    Text(
                        text = "Dodaj fotografije",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Brisanje događaja")
            },
            text = {
                Text("Jeste li sigurni da želite obrisati ovaj događaj?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        EventRepository.deleteEvent(currentEvent.id)
                        onDelete()
                    }
                ) {
                    Text(
                        text = "Obriši",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Odustani")
                }
            }
        )
    }

    if (selectedImagePath != null) {
        Dialog(
            onDismissRequest = { selectedImagePath = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = File(selectedImagePath!!)
                    ),
                    contentDescription = "Uvećana fotografija",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { selectedImagePath = null },
                    contentScale = ContentScale.Fit
                )
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Obriši fotografiju",
                        color = Color.White
                    )
                }
            }
        }
    }
    if (showDeleteDialog && selectedImagePath != null) {
       AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Obriši fotografiju?",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Jesi li siguran/na da želiš obrisati ovu fotografiju?",
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteGalleryImage(
                            selectedImagePath!!
                        )
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C203A)
                    )
                ) {
                    Text("Odustani")
                }
            },
            containerColor = Color(0xFF1A102B)
        )
    }
}

@Composable
fun EventTab( text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {

    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (selected)
                Color.White
            else
                Color.LightGray,
            fontSize = 15.sp,
            fontWeight = if (selected)
                FontWeight.Bold
            else
                FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(2.dp)
                    .background(
                        Color(0xFF6C2FF2),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun FeedbackCard(feedback: Feedback, currentUserId: String?, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFF120A1F),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feedback.userName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    for (star in 1..5) {
                        Text(
                            text = "★",
                            color = if (star <= feedback.rating)
                                Color(0xFFFFC107)
                            else
                                Color(0xFF4A4055),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feedback.text,
                color = Color.LightGray,
                fontSize = 14.sp,
                lineHeight = 19.sp
            )
            if (feedback.userId == currentUserId) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Obriši",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        onDelete()
                    }
                )
            }
        }
    }
}
@Composable
fun ActionBox(text: String, textColor: Color = Color.White, backgroundColor: Color = Color(0xFF1A102B) ,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp)
    }
}