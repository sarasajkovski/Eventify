package com.example.eventify.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import com.example.eventify.utils.saveImageLocally
import com.google.maps.android.compose.GoogleMap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.collections.emptyList

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen() {

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var locationExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var errorMessage by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val locations = listOf("Tvrđa", "Centar Osijeka", "Kampus", "Gradski vrt", "Ostalo")
    val categories = listOf("Koncert", "Predavanje", "Radionica", "Sport", "Kultura", "Ostalo")

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris
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

    val initialLocation = LatLng(45.5550, 18.6955)
    var selectedLatitude by remember { mutableDoubleStateOf(initialLocation.latitude) }
    var selectedLongitude by remember { mutableDoubleStateOf(initialLocation.longitude) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialLocation,
            15f
        )
    }
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            selectedLatitude = cameraPositionState.position.target.latitude
            selectedLongitude = cameraPositionState.position.target.longitude
        }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            snackbarHostState.showSnackbar(
                message = "Event uspješno dodan! 🎉",
                duration = SnackbarDuration.Short
            )
            showSuccessMessage = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF05010F))
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(innerPadding)
                .padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Naziv događaja",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            EventTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Unesite naziv"
            )

            Text(
                text = "Opis događaja",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            EventTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Unesite opis"
            )

            Text(
                text = "Datum i vrijeme",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .background(
                        Color(0xFF0D0718)
                    )
                    .clickable {
                        datePicker.show()
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = date.ifEmpty {
                            "Odaberite datum i vrijeme"
                        },
                        color = if (date.isEmpty())
                            Color.Gray
                        else
                            Color.White,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "⌄",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lokacija",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = {
                    locationExpanded = !locationExpanded
                }
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            text = "Odaberite lokaciju",
                            color = Color.Gray
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = locationExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6C2FF2),
                        unfocusedBorderColor = Color(0xFF2C203A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF6C2FF2),
                        unfocusedLabelColor = Color.Gray
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
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Odabrana lokacija",
                    tint = Color(0xFF6C2FF2),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(45.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kategorija",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            text = "Odaberite kategoriju",
                            color = Color.Gray
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6C2FF2),
                        unfocusedBorderColor = Color(0xFF2C203A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF6C2FF2),
                        unfocusedLabelColor = Color.Gray
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
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        Color(0xFF0D0718),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        imagePicker.launch("image/*")
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (imageUris.isEmpty())
                        "Dodaj fotografije"
                    else
                        "${imageUris.size} fotografija odabrano",
                    color = if (imageUris.isEmpty())
                        Color.Gray
                    else
                        Color.White,
                    fontSize = 14.sp
                )
            }

            if (imageUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imageUris) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(
                                    RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))


            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    if (
                        title.isBlank() ||
                        location.isBlank() ||
                        date.isBlank() ||
                        category.isBlank()
                    ) {
                        errorMessage =
                            "Molimo ispuni sva obavezna polja."
                        return@Button
                    }
                    val currentUserId =
                        FirebaseAuth
                            .getInstance()
                            .currentUser?.uid ?: ""
                    isLoading = true

                    val imagePaths = imageUris.mapNotNull { uri ->
                        saveImageLocally(
                            context = context,
                            uri = uri
                        )
                    }
                    EventRepository.addEvent(
                        event = Event(
                            title = title,
                            date = date,
                            location = location,
                            category = category,
                            description = description,
                            userId = currentUserId,
                            imagePaths = imagePaths,
                            latitude = selectedLatitude,
                            longitude = selectedLongitude
                        ),
                        onSuccess = {
                            isLoading = false
                            errorMessage = ""
                            showSuccessMessage = true
                            title = ""
                            location = ""
                            date = ""
                            category = ""
                            description = ""
                            imageUris = emptyList()
                        },
                        onError = { error ->
                            errorMessage = error
                            isLoading = false
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

    @Composable
    fun EventTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,

            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(67.dp)
                .padding(bottom = 12.dp),

            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C2FF2),
                unfocusedBorderColor = Color(0xFF2C203A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )
    }