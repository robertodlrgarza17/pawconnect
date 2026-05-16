package com.example.pawconnect.ui.screens.shelter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawconnect.R
import com.example.pawconnect.Screen
import com.example.pawconnect.repository.AdoptionRequest
import com.example.pawconnect.repository.AdoptionRequestsRepository
import com.example.pawconnect.ui.screens.components.ShelterBottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterRevisionPetsScreen(navController: NavController) {
    var requestsList by remember { mutableStateOf<List<AdoptionRequest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        AdoptionRequestsRepository.fetchPendingRequests { success, list, error ->
            if (success) {
                requestsList = list
            } else {
                errorMessage = error ?: "Error desconocido"
            }
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Revisión de Solicitudes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            ShelterBottomNavBar(
                onHuellasClick = { navController.navigate(Screen.ShelterPets.route) },
                onHomeClick = { navController.navigate(Screen.ShelterHome.route) },
                onPerfilClick = { navController.navigate(Screen.ShelterProfile.route) }
            )
        },
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
                    Text(text = errorMessage, modifier = Modifier.align(Alignment.Center))
                }
                requestsList.isEmpty() -> {
                    Text(
                        text = "No hay solicitudes pendientes.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(requestsList) { request ->
                            RequestItemCard(request) {
                                navController.navigate("ShelterRequestDetail/${request.requestId}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(request: AdoptionRequest, onClickReview: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_user),
                contentDescription = "Usuario",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = request.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "La solicitud de adopción se encuentra pendiente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onClickReview,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Revisar")
            }
        }
    }
}
