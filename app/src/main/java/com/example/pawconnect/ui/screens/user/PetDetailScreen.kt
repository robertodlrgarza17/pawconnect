package com.example.pawconnect.ui.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
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
import com.example.pawconnect.Screen
import com.example.pawconnect.repository.PetData
import com.example.pawconnect.repository.FavoritesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(navController: NavController, petId: String) {
    var pet by remember { mutableStateOf<PetData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }
    var userType by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(petId) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { doc ->
                userType = doc.getString("tipoCuenta") ?: ""
            }
        }

        db.collection("mascotas")
            .document(petId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    pet = document.toObject(PetData::class.java)?.copy(id = petId)
                } else {
                    errorMessage = "Mascota no encontrada"
                }
                loading = false
            }
            .addOnFailureListener { e ->
                errorMessage = e.localizedMessage ?: "Error desconocido"
                loading = false
            }
    }

    // Verificar si la mascota ya está en favoritos una vez que se haya cargado
    LaunchedEffect(pet) {
        pet?.let {
            FavoritesRepository.isFavorite(it.id) { favorite ->
                isFavorite = favorite
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = pet?.petName ?: "Detalle de la Mascota",
                        color = MaterialTheme.colorScheme.onPrimary
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
                colors = centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp
                    )
                }
                pet != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            val painter = rememberAsyncImagePainter(
                                model = pet?.petPhoto,
                                fallback = painterResource(R.drawable.my_placeholder),
                                error = painterResource(R.drawable.my_placeholder)
                            )
                            // Imagen de la mascota
                            Image(
                                painter = painter,
                                contentDescription = pet?.petName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Tarjeta con detalles de la mascota
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    PetDetailItem("Especie", pet!!.petSpecies)
                                    PetDetailItem("Raza", pet!!.petBreed)
                                    PetDetailItem("Tamaño", pet!!.petSize)
                                    PetDetailItem("Peso", "${pet!!.petWeight} kg")
                                    PetDetailItem("Edad", "${pet!!.petAge} años")
                                    PetDetailItem("Sexo", pet!!.petSex)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    PetStatusItem("Vacunado", pet!!.hasVaccines)
                                    PetStatusItem("Esterilizado", pet!!.isSterilized)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            // Tarjeta con la historia de la mascota
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Historia de ${pet!!.petName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = pet!!.petHistory.ifEmpty { "No hay información disponible" },
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (userType == "Refugio") {
                                Button(
                                    onClick = {
                                        navController.navigate(Screen.ShelterEditPet.route.replace("{petId}", pet!!.id))
                                    },
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Editar información", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            } else {
                                // Botón para agregar o eliminar favorito según el estado
                                if (!isFavorite) {
                                    Button(
                                        onClick = {
                                            FavoritesRepository.addFavorite(petId = pet!!.id) { success, error ->
                                                coroutineScope.launch {
                                                    if (success) {
                                                        snackbarHostState.showSnackbar("Mascota agregada a favoritos")
                                                        isFavorite = true
                                                        navController.navigate(Screen.Favorite.route)  // Navega a Favoritos
                                                    } else {
                                                        snackbarHostState.showSnackbar("Error al agregar: ${error ?: "Error desconocido"}")
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Agregar a favoritos", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            FavoritesRepository.removeFavorite(petId = pet!!.id) { success, error ->
                                                coroutineScope.launch {
                                                    if (success) {
                                                        snackbarHostState.showSnackbar("Mascota eliminada de favoritos")
                                                        isFavorite = false
                                                        navController.navigate(Screen.Favorite.route)  // Navega a Favoritos
                                                    } else {
                                                        snackbarHostState.showSnackbar("Error al eliminar: ${error ?: "Error desconocido"}")
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Eliminar de favoritos", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Botón "Adóptame" (opcional)
                                Button(
                                    onClick = {
                                        pet?.let { safePet ->
                                            navController.navigate("FormularioAdoptame/${safePet.id}")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Adóptame", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun PetStatusItem(label: String, status: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            painter = painterResource(id = if (status) R.drawable.icon_check else R.drawable.icon_close),
            contentDescription = "$label status",
            tint = if (status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
    }
}

