package com.example.pawconnect.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawconnect.Screen
import com.example.pawconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {
    var tipoCuenta by remember { mutableStateOf("Selecciona tipo de cuenta") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo con blur
        Image(
            painter = painterResource(id = R.drawable.background_dogs),
            contentDescription = "Fondo de perros",
            modifier = Modifier
                .fillMaxSize()
                .blur(12.dp),
            contentScale = ContentScale.Crop
        )

        // Overlay semi-transparente
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_pawconnect),
                contentDescription = "Logo PawConnect",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Bienvenido",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TipoCuentaDropdown(
                        tipoCuentaSeleccionado = tipoCuenta,
                        onTipoCuentaChange = { tipoCuenta = it }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.matches(Regex("^[A-Za-z0-9@._-]*$"))) email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            errorMessage = ""
                            when {
                                tipoCuenta == "Selecciona tipo de cuenta" || email.isBlank() || password.isBlank() ->
                                    errorMessage = "Por favor, completa todos los campos."
                                !isValidEmail(email) ->
                                    errorMessage = "El correo electrónico no es válido."
                                else -> {
                                    isLoading = true
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val firebaseUser = auth.currentUser
                                                firebaseUser?.let { user ->
                                                    firestore.collection("users")
                                                        .document(user.uid)
                                                        .get()
                                                        .addOnSuccessListener { document ->
                                                            isLoading = false
                                                            if (document.exists()) {
                                                                val rolEnDB = document.getString("tipoCuenta")
                                                                if (rolEnDB == tipoCuenta) {
                                                                    val route = if (tipoCuenta == "Refugio") Screen.ShelterHome.route else Screen.UserHome.route
                                                                    navController.navigate(route) {
                                                                        popUpTo(Screen.Login.route) { inclusive = true }
                                                                    }
                                                                } else {
                                                                    errorMessage = "El tipo de cuenta no coincide."
                                                                    auth.signOut()
                                                                }
                                                            } else {
                                                                errorMessage = "Usuario no encontrado."
                                                                auth.signOut()
                                                            }
                                                        }
                                                        .addOnFailureListener {
                                                            isLoading = false
                                                            errorMessage = "Error al obtener datos."
                                                        }
                                                }
                                            } else {
                                                isLoading = false
                                                errorMessage = "Credenciales incorrectas."
                                            }
                                        }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(
                        onClick = { if (!isLoading) navController.navigate(Screen.Register.route) }
                    ) {
                        Text("¿No tienes cuenta? Regístrate aquí", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
