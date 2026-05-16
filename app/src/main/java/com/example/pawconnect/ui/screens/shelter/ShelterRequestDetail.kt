package com.example.pawconnect.ui.screens.shelter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pawconnect.R
import com.example.pawconnect.repository.AdoptionRequest
import com.example.pawconnect.repository.AdoptionRequestsRepository
import com.example.pawconnect.repository.PetData
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterRequestDetailScreen(navController: NavController, requestId: String) {
    var request by remember { mutableStateOf<AdoptionRequest?>(null) }
    var pet by remember { mutableStateOf<PetData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Cargar la solicitud
    LaunchedEffect(requestId) {
        if (requestId.isNotBlank()) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("adoptionRequests").document(requestId)
            docRef.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val adoptionReq = snapshot.toObject(AdoptionRequest::class.java)
                        if (adoptionReq != null) {
                            request = adoptionReq
                            loadPet(adoptionReq.petId) { petData, error ->
                                if (petData != null) {
                                    pet = petData
                                } else {
                                    errorMessage = error ?: "Error al cargar la mascota."
                                }
                                loading = false
                            }
                        } else {
                            errorMessage = "No se pudo parsear la solicitud."
                            loading = false
                        }
                    } else {
                        errorMessage = "No se encontró la solicitud."
                        loading = false
                    }
                }
                .addOnFailureListener { e ->
                    errorMessage = e.localizedMessage ?: "Error desconocido"
                    loading = false
                }
        } else {
            errorMessage = "No se proporcionó requestId."
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalles de Solicitud",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                request != null && pet != null -> {
                    RequestDetailsContent(
                        request = request!!,
                        pet = pet!!,
                        onAccept = {
                            AdoptionRequestsRepository.updateRequestStatus(request!!.requestId, request!!.petId, "aprobada") { success, error ->
                                if (success) {
                                    navController.popBackStack()
                                } else {
                                    errorMessage = error ?: "Error al aprobar la solicitud."
                                }
                            }
                        },
                        onReject = {
                            AdoptionRequestsRepository.updateRequestStatus(request!!.requestId, request!!.petId, "rechazada") { success, error ->
                                if (success) {
                                    navController.popBackStack()
                                } else {
                                    errorMessage = error ?: "Error al rechazar la solicitud."
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestDetailsContent(
    request: AdoptionRequest,
    pet: PetData,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta de la mascota
        PetCard(pet)

        // Tarjeta de información del adoptante
        AdopterInfoCard(request)

        // Tarjeta de motivo de adopción
        AdoptionReasonCard(request)

        // Tarjeta de entorno familiar
        LivingEnvironmentCard(request)

        // Botones de acción
        ActionButtons(onAccept, onReject)
    }
}

@Composable
private fun PetCard(pet: PetData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen de la mascota
            val painter = rememberAsyncImagePainter(
                model = pet.petPhoto,
                fallback = painterResource(R.drawable.my_placeholder),
                error = painterResource(R.drawable.my_placeholder)
            )
            Image(
                painter = painter,
                contentDescription = pet.petName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Información de la mascota
            Text(
                text = "Mascota para adopción",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = pet.petName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun AdopterInfoCard(request: AdoptionRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Información del adoptante",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(text = "Nombre: ${request.userName} ${request.userSurname}")
            Text(text = "Edad: ${request.userAge}")
            Text(text = "Correo: ${request.userEmail}")
            Text(text = "Teléfono: ${request.userPhone}")
        }
    }
}

@Composable
private fun AdoptionReasonCard(request: AdoptionRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Motivo de adopción",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = request.reason,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LivingEnvironmentCard(request: AdoptionRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Entorno familiar",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(text = "Actualmente tiene otros animales: ${if (request.currentlyHasPets) "Sí" else "No"}")
            Text(text = "Ha tenido animales antes: ${if (request.previouslyHadPets) "Sí" else "No"}")
            Text(text = "Personas en el hogar: ${request.peopleInHouse}")
            Text(text = "Todos están de acuerdo en adoptar: ${if (request.allAgree) "Sí" else "No"}")
            Text(text = "Niños en casa: ${if (request.hasKids) "Sí" else "No"}")
            Text(text = "Vive en casa propia: ${if (request.ownHouse) "Sí" else "No"}")

            if (!request.ownHouse) {
                Text(text = "Arrendador permite mascotas: ${if (request.landlordAllows) "Sí" else "No"}")
            }
        }
    }
}

@Composable
private fun ActionButtons(onAccept: () -> Unit, onReject: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onAccept,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Aprobar", style = MaterialTheme.typography.labelLarge)
        }

        Button(
            onClick = onReject,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Rechazar", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Función auxiliar para cargar la mascota dado su petId.
 */
private fun loadPet(
    petId: String,
    onResult: (PetData?, String?) -> Unit
) {
    if (petId.isBlank()) {
        onResult(null, "petId vacío.")
        return
    }
    val db = FirebaseFirestore.getInstance()
    db.collection("mascotas")
        .document(petId)
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val petData = snapshot.toObject(PetData::class.java)?.copy(id = snapshot.id)
                onResult(petData, null)
            } else {
                onResult(null, "No se encontró la mascota con ID $petId.")
            }
        }
        .addOnFailureListener { e ->
            onResult(null, e.localizedMessage)
        }
}