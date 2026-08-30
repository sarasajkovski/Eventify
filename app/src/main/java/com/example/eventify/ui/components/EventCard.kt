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
import androidx.compose.runtime.Composable
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
import java.io.File

@Composable
fun EventCard(event: Event, onClick: (Event) -> Unit, isFavorite: Boolean = false, onFavoriteClick: (Event) -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF120A1F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(event)
            }
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    if (event.imagePaths.isNotEmpty()){
                        File(event.imagePaths.first())
                    } else {
                        null
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 18.dp,
                        end = 8.dp,
                        top = 15.dp,
                        bottom = 23.dp
                    ),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = event.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = event.date,
                        color = Color.LightGray,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = event.location,
                        color = Color.LightGray,
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF6C2FF2),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(
                                horizontal = 15.dp,
                                vertical = 6.dp
                            )
                    ) {
                        Text(
                            text = event.category,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }

                IconButton(
                    onClick = { onFavoriteClick(event) },
                    modifier = Modifier.size(40.dp)
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