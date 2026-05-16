package com.example.pawconnect.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawconnect.R
import com.example.pawconnect.Screen
import com.example.pawconnect.repository.AdoptionRequest
import com.example.pawconnect.repository.AdoptionRequestsRepository
import com.example.pawconnect.ui.screens.components.UserBottomNavBar
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioAdoptame(navController: NavController, petId: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    var userName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var userSurname by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf(currentUser?.email ?: "") }
    var userPhone by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var currentlyHasPets by remember { mutableStateOf(false) }
    var previouslyHadPets by remember { mutableStateOf(false) }
    var peopleInHouse by remember { mutableStateOf("") }
    var allAgree by remember { mutableStateOf(false) }
    var hasKids by remember { mutableStateOf(false) }
    var ownHouse by remember { mutableStateOf(false) }
    var landlordAllows by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Solicitud de Adopción", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            UserBottomNavBar(
                onHuellasClick = { navController.navigate(Screen.Pets.route) },
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onPerfilClick = { navController.navigate(Screen.Profile.route) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Completa esta encuesta para iniciar el proceso de adopción.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Sección 1: Datos Personales
            FormSection(title = "Datos Personales") {
                CustomTextField(value = userName, onValueChange = { userName = it }, label = "Nombre")
                CustomTextField(value = userSurname, onValueChange = { userSurname = it }, label = "Apellidos")
                CustomTextField(value = userAge, onValueChange = { if (it.toIntOrNull() != null || it.isEmpty()) userAge = it }, label = "Edad")
                CustomTextField(value = userEmail, onValueChange = { userEmail = it }, label = "Correo")
                CustomTextField(value = userPhone, onValueChange = { userPhone = it }, label = "Teléfono")
            }

            // Sección 2: Motivo
            FormSection(title = "¿Por qué deseas adoptar?") {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Escribe el motivo aquí...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Sección 3: Entorno
            FormSection(title = "Datos del Entorno") {
                SwitchRow(label = "¿Actualmente tienes otros animales?", checked = currentlyHasPets, onCheckedChange = { currentlyHasPets = it })
                SwitchRow(label = "¿Anteriormente has tenido animales?", checked = previouslyHadPets, onCheckedChange = { previouslyHadPets = it })
                CustomTextField(value = peopleInHouse, onValueChange = { if (it.toIntOrNull() != null || it.isEmpty()) peopleInHouse = it }, label = "¿Cuántas personas viven en tu casa?")
                SwitchRow(label = "¿Todos en el hogar están de acuerdo?", checked = allAgree, onCheckedChange = { allAgree = it })
                SwitchRow(label = "¿Hay niños en casa?", checked = hasKids, onCheckedChange = { hasKids = it })
                SwitchRow(label = "¿Vives en casa propia?", checked = ownHouse, onCheckedChange = { ownHouse = it })
                
                if (!ownHouse) {
                    SwitchRow(label = "¿Tus arrendadores permiten mascotas?", checked = landlordAllows, onCheckedChange = { landlordAllows = it })
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = {
                    when {
                        userName.isBlank() || userSurname.isBlank() || userAge.isBlank() || 
                        userEmail.isBlank() || userPhone.isBlank() || reason.isBlank() -> 
                            errorMessage = "Por favor, completa todos los campos obligatorios."
                        else -> {
                            errorMessage = ""
                            val newRequest = AdoptionRequest(
                                userName = userName,
                                userSurname = userSurname,
                                userAge = userAge,
                                userEmail = userEmail,
                                userPhone = userPhone,
                                reason = reason,
                                currentlyHasPets = currentlyHasPets,
                                previouslyHadPets = previouslyHadPets,
                                peopleInHouse = peopleInHouse,
                                allAgree = allAgree,
                                hasKids = hasKids,
                                ownHouse = ownHouse,
                                landlordAllows = landlordAllows,
                                petId = petId
                            )
                            AdoptionRequestsRepository.addAdoptionRequest(newRequest) { success, error ->
                                if (success) {
                                    navController.navigate(Screen.AdoptionSuccess.route)
                                } else {
                                    errorMessage = error ?: "Error al guardar la solicitud"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5D80))
            ) {
                Text("Finalizar Solicitud", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            content()
        }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
