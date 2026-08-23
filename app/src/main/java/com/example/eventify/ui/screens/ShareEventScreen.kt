package com.example.eventify.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventify.data.Event
import java.io.File

@Composable
fun ShareEventScreen(event: Event, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Natrag",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onBack()
                    }
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Podijeli događaj",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(
            modifier = Modifier.height(25.dp)
        )
        Text(
            text = "Detalji događaja na dohvat ruke",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Podijeli ovaj događaj s prijateljima.",
            color = Color.LightGray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF120A1F)
            )
        ) {
            Column {
                AsyncImage(
                    model = if (event.imagePaths.isNotEmpty()) {
                        File(event.imagePaths.first())
                    } else {
                        null
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = event.title,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.date,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.location,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF6C2FF2),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Text(
                            text = event.category,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val shareText = buildString {
                    append(event.title)
                    append("\n")
                    append(event.date)
                    append("\n")
                    append(event.location)
                    if (event.description.isNotBlank()) {
                        append("\n\n")
                        append(event.description)
                    }
                }
                val intent = Intent(
                    Intent.ACTION_SEND
                ).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        shareText
                    )
                }
                val chooser =
                    Intent.createChooser(
                        intent,
                        "Podijeli događaj"
                    )
                context.startActivity(chooser)
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
                text = "Podijeli događaj",
                fontSize = 16.sp
            )
        }
    }
}