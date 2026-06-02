package com.example.eventify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF05010F),
                        Color(0xFF16002E),
                        Color(0xFF05010F)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registracija",
            color = Color.White,
            fontSize = 34.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        EventTextField(email, { email = it }, "Email")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Lozinka") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C2FF2),
                unfocusedBorderColor = Color(0xFF2C203A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Unesi email i lozinku."
                    return@Button
                }
                if (password.length < 6) {
                    errorMessage = "Lozinka mora imati barem 6 znakova."
                    return@Button
                }
                isLoading = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        isLoading = false
                        if (it.isSuccessful) {
                            onRegisterSuccess()
                        } else {
                            errorMessage = "Registracija nije uspjela"
                        }
                        isLoading = false
                    }
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
                Text("Registriraj se")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red)
        }
    }
}