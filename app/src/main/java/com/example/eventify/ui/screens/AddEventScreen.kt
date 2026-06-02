package com.example.eventify.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@Composable
fun AddEventScreen() {

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate =
                "$dayOfMonth.${month + 1}.$year."
            val timePicker = TimePickerDialog(
                context,
                { _, hour, minute ->
                    date =
                        "$selectedDate ${
                            String.format("%02d:%02d", hour, minute)
                        }"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Text(
            text = "Dodaj događaj",
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        EventTextField(
            title,
            { title = it },
            "Naziv događaja"
        )
        EventTextField(
            location,
            { location = it },
            "Lokacija"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(
                    Color(0xFF1A102B),
                    RoundedCornerShape(16.dp)
                )
                .clickable {
                    datePicker.show()
                }
                .padding(16.dp)
        ) {
            Text(
                text =
                    date.ifEmpty { "Odaberi datum i vrijeme" },
                color =
                    if (date.isEmpty())
                        Color.LightGray
                    else
                        Color.White
            )
        }
        EventTextField(
            category,
            { category = it },
            "Kategorija"
        )
        EventTextField(
            description,
            { description = it },
            "Opis događaja"
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF1A102B),
                    RoundedCornerShape(16.dp)
                )
                .clickable {
                    imagePicker.launch("image/*")
                }
                .padding(16.dp)
        ) {
            Text(
                text = "Dodaj fotografiju",
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
            onClick = {
                if (
                    title.isBlank() ||
                    location.isBlank() ||
                    date.isBlank() ||
                    category.isBlank()
                ) {
                    errorMessage = "Molimo ispuni sva obavezna polja."
                    return@Button
                }
                val currentUserId =
                    FirebaseAuth.getInstance()
                        .currentUser?.uid ?: ""
                isLoading = true
                EventRepository.addEvent(
                    Event(
                        title = title,
                        date = date,
                        location = location,
                        category = category,
                        description = description,
                        userId = currentUserId
                    )
                )
                isLoading = false
                errorMessage = ""
                title = ""
                location = ""
                date = ""
                category = ""
                description = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C2FF2)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Objavi događaj",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun EventTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6C2FF2),
            unfocusedBorderColor = Color(0xFF2C203A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        )
    )
}