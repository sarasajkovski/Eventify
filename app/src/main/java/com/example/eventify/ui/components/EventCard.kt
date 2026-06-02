package com.example.eventify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.eventify.data.Event
import com.example.eventify.data.EventRepository

@Composable
fun EventCard(event: Event, onClick: (Event) -> Unit, isFavorite: Boolean = false, onFavoriteClick: (Event) -> Unit = {}) {

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF120A1F)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(event)
            }
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    event.imageUrl.ifEmpty {
                        "https://picsum.photos/400/200"
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 22.dp,
                            topEnd = 22.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = event.title,
                            color = Color.White,
                            fontSize = 20.sp,
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
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF6C2FF2),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                )
                        ) {
                            Text(
                                text = event.category,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            onFavoriteClick(event)
                        }
                    ) {
                        Icon(
                            imageVector =
                                if (isFavorite)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint =
                                if (isFavorite)
                                    Color.Red
                                else
                                    Color.White
                        )
                    }
                }
            }
        }
    }
}