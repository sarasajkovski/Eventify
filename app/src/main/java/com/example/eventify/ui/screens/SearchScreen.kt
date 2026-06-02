package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository
import com.example.eventify.ui.components.EventCard

@Composable
fun SearchScreen(onEventClick: (Event) -> Unit) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Svi") }
    val categories = listOf("Svi", "Koncert", "Predavanje", "Sport", "Kultura", "Meetup")

    LaunchedEffect(Unit) {
        EventRepository.listenForEvents {
            events = it
        }
    }
    val filteredEvents = events.filter { event ->
        val matchSearch =
            event.title.contains(searchText, true) ||
                    event.location.contains(searchText, true)
        val matchCategory =
            selectedCategory == "Svi" || event.category == selectedCategory
        matchSearch && matchCategory
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Text(
            text = "Pretraga i filtriranje",
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Pretraži događaje...", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6C2FF2),
                unfocusedBorderColor = Color(0xFF2C203A),
                cursorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Kategorije", color = Color.White, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6C2FF2),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF120A1F),
                        labelColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lokacija",
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            listOf("Sve", "Osijek", "FERIT", "Centar", "Kampus").forEach { location ->
                FilterChip(
                    selected = searchText.contains(location, ignoreCase = true),
                    onClick = {
                        searchText = if (location == "Sve") "" else location
                    },
                    label = {
                        Text(location)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6C2FF2),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF120A1F),
                        labelColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(filteredEvents) { event ->
                EventCard(
                    event = event,
                    onClick = onEventClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}