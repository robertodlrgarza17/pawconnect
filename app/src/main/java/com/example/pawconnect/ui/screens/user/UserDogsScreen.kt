package com.example.pawconnect.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawconnect.R
import com.example.pawconnect.Screen
import com.example.pawconnect.repository.PetData
import com.example.pawconnect.repository.fetchPetsBySpecies
import com.example.pawconnect.ui.screens.components.FiltrosContent
import com.example.pawconnect.ui.screens.components.PetCard
import com.example.pawconnect.ui.screens.components.UserBottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDogsScreen(navController: NavController) {
    var petsList by remember { mutableStateOf<List<PetData>>(emptyList()) }
    var filteredPets by remember { mutableStateOf<List<PetData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var showFiltros by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        fetchPetsBySpecies("perro") { success, pets, error ->
            if (success) {
                petsList = pets
                filteredPets = petsList
            } else {
                errorMessage = error ?: "Error al cargar perros"
            }
            loading = false
        }
    }

    if (showFiltros) {
        ModalBottomSheet(
            onDismissRequest = { showFiltros = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            FiltrosContent(
                onFiltrosAplicados = { tamaño, genero ->
                    filteredPets = if (tamaño.isEmpty() && genero.isEmpty()) {
                        petsList
                    } else {
                        petsList.filter { pet ->
                            (tamaño.isEmpty() || pet.petSize.equals(tamaño, ignoreCase = true)) &&
                                    (genero.isEmpty() || pet.petSex.equals(genero, ignoreCase = true))
                        }
                    }
                    showFiltros = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Perros en Adopción",
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
                actions = {
                    IconButton(onClick = { showFiltros = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Filtrar",
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                }
                filteredPets.isEmpty() -> {
                    Text(
                        text = "No se encontraron perros disponibles.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredPets) { pet ->
                            PetCard(
                                pet = pet,
                                onClickInfo = {
                                    navController.navigate(
                                        Screen.PetDetails.route.replace("{petId}", pet.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
