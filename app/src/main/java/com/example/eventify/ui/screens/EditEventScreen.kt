package com.example.eventify.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import java.util.Calendar

@Composable
fun EditEventScreen(event: Event, onSave: () -> Unit) {
    var title by remember { mutableStateOf(event.title) }
    var location by remember { mutableStateOf(event.location) }
    var date by remember { mutableStateOf(event.date) }
    var category by remember { mutableStateOf(event.category) }
    var description by remember { mutableStateOf(event.description) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth.${month + 1}.$year."
            val timePicker = TimePickerDialog(
                context,
                { _, hour, minute ->
                    date = "$selectedDate ${String.format("%02d:%02d", hour, minute)}"
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
            text = "Uredi događaj",
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        EventTextField(title, { title = it }, "Naziv događaja")
        EventTextField(location, { location = it }, "Lokacija")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(Color(0xFF1A102B), RoundedCornerShape(16.dp))
                .clickable {
                    datePicker.show()
                }
                .padding(16.dp)
        ) {
            Text(
                text = if (date.isEmpty()) "Odaberi datum i vrijeme" else date,
                color = if (date.isEmpty()) Color.LightGray else Color.White
            )
        }
        EventTextField(category, { category = it }, "Kategorija")
        EventTextField(description, { description = it }, "Opis događaja")
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                EventRepository.updateEvent(
                    event.copy(
                        title = title,
                        location = location,
                        date = date,
                        category = category,
                        description = description
                    )
                )
                onSave()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C2FF2)
            )
        ) {
            Text(
                text = "Spremi promjene",
                fontSize = 16.sp
            )
        }
    }
}