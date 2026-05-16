package com.example.pawconnect.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawconnect.R
import com.example.pawconnect.Screen
import com.example.pawconnect.repository.AdoptionRequest
import com.example.pawconnect.repository.AdoptionRequestsRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pawconnect.repository.PetData
import com.example.pawconnect.ui.screens.components.UserBottomNavBar
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolitudesUser(navController: NavController) {
    // Obtenemos el correo del usuario autenticado
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: ""

    var requestsList by remember { mutableStateOf<List<AdoptionRequest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Carga las solicitudes asociadas al usuario
    LaunchedEffect(Unit) {
        if (userEmail.isNotEmpty()) {
            AdoptionRequestsRepository.fetchUserRequests(userEmail) { success, list, error ->
                if (success) {
                    requestsList = list
                } else {
                    errorMessage = error ?: "Error desconocido"
                }
                loading = false
            }
        } else {
            errorMessage = "No se encontró correo del usuario."
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis solicitudes",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            UserBottomNavBar(
                onHuellasClick = { navController.navigate(Screen.Pets.route) },
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onPerfilClick = { navController.navigate(Screen.Profile.route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                requestsList.isEmpty() -> {
                    // Placeholder cuando no hay solicitudes
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_pets),
                            contentDescription = "Sin solicitudes",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aún no tienes solicitudes de adopción.",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(requestsList) { request ->
                            UserRequestItem(request)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRequestItem(request: AdoptionRequest) {
    // Estados para almacenar la mascota y su estado de carga
    var pet by remember { mutableStateOf<PetData?>(null) }
    var petLoading by remember { mutableStateOf(true) }
    var petError by remember { mutableStateOf<String?>(null) }

    // Al montar el composable, buscamos la mascota por su ID
    LaunchedEffect(request.petId) {
        if (request.petId.isNotBlank()) {
            FirebaseFirestore.getInstance()
                .collection("mascotas")
                .document(request.petId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        // Transforma el documento en tu modelo PetData
                        pet = doc.toObject(PetData::class.java)?.copy(id = doc.id)
                    } else {
                        petError = "No se encontró la mascota."
                    }
                    petLoading = false
                }
                .addOnFailureListener { e ->
                    petError = e.localizedMessage ?: "Error desconocido al cargar la mascota."
                    petLoading = false
                }
        } else {
            petError = "No se proporcionó un ID de mascota."
            petLoading = false
        }
    }

    // Definimos el color del botón según el estado de la solicitud
    val buttonColor = when (request.status) {
        "pendiente" -> MaterialTheme.colorScheme.tertiary  // Amarillo
        "aprobada" -> MaterialTheme.colorScheme.primary    // Verde
        "rechazada" -> MaterialTheme.colorScheme.error     // Rojo
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    // Construimos la Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        // Verificamos si seguimos cargando, hay error, o ya tenemos la mascota
        when {
            petLoading -> {
                // Mostramos un indicador de carga
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            petError != null -> {
                // Mostramos un texto de error
                Text(
                    text = petError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // Ya tenemos la mascota (pet != null) o al menos no hay error
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pet?.petPhoto?.isNotEmpty() == true) {
                        PetImage(pet!!)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        // En lugar de "Mascota ID", mostramos su nombre (o foto).
                        Text(
                            text = if (pet != null) "Mascota: ${pet!!.petName}"
                            else "Mascota desconocida",
                            style = MaterialTheme.typography.titleMedium
                        )
                        // Motivo de adopción
                        Text(
                            text = "Motivo: ${request.reason}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        // Estado de la solicitud
                        Text(
                            text = "Estado: ${request.status}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botón con color según el estado
                    Button(
                        onClick = { /* Podrías navegar a detalles de la solicitud, etc. */ },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        Text(
                            text = request.status.replaceFirstChar { it.uppercase() },
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetImage(pet: PetData) {
    val painter = rememberAsyncImagePainter(
        model = pet.petPhoto,
        fallback = painterResource(R.drawable.my_placeholder),
        error = painterResource(R.drawable.my_placeholder)
    )
    Image(
        painter = painter,
        contentDescription = pet.petName,
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

