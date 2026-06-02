package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05010F))
            .padding(20.dp)
    ) {
        Text(
            text = "Profil",
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Email:",
            color = Color.LightGray,
            fontSize = 14.sp
        )
        Text(
            text = user?.email ?: "Nepoznat korisnik",
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C2FF2)
            )
        ) {
            Text("Odjavi se")
        }
    }
}