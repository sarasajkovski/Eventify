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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(event: Event, onSave: () -> Unit) {
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var location by remember { mutableStateOf(event.location) }
    var category by remember { mutableStateOf(event.category) }
    val categories = listOf("Koncert", "Predavanje", "Sport", "Kultura", "Meetup", "Ostalo")
    val locations = listOf("Osijek", "FERIT", "Centar", "Kampus", "Ostalo")

    var date by remember { mutableStateOf(event.date) }
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth.${month + 1}.$year."
            val timePicker = TimePickerDialog(
                context,
                { _, hour, minute ->
                    date = "$selectedDate ${String.format(Locale.getDefault(), "%02d:%02d", hour, minute)}"
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

        var locationExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = locationExpanded,
            onExpandedChange = {
                locationExpanded = !locationExpanded
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = location,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Lokacija")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = locationExpanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C2FF2),
                    unfocusedBorderColor = Color(0xFF2C203A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFF9B6CFF),
                    unfocusedLabelColor = Color.LightGray
                )
            )

            ExposedDropdownMenu(
                expanded = locationExpanded,
                onDismissRequest = {
                    locationExpanded = false
                }
            ) {

                locations.forEach { item ->

                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },
                        onClick = {

                            location = item
                            locationExpanded = false
                        }
                    )
                }
            }
        }
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
                text = date.ifEmpty { "Odaberi datum i vrijeme" },
                color = if (date.isEmpty()) Color.LightGray else Color.White
            )
        }
        var categoryExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = {
                categoryExpanded = !categoryExpanded
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Kategorija")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoryExpanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C2FF2),
                    unfocusedBorderColor = Color(0xFF2C203A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFF9B6CFF),
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                }
            ) {
                categories.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },
                        onClick = {
                            category = item
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        EventTextField(description, { description = it }, "Opis događaja")
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                val updatedEvent = event.copy(
                        title = title,
                        location = location,
                        date = date,
                        category = category,
                        description = description
                    )
                EventRepository.updateEvent(updatedEvent)
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