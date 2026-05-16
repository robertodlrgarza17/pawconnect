package com.example.pawconnect.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pawconnect.R
import com.example.pawconnect.Screen
import com.example.pawconnect.ui.screens.components.UserBottomNavBar
import com.example.pawconnect.ui.screens.components.ShelterBottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiaDeAdopcion(navController: NavController) {
    var userType by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { doc ->
                userType = doc.getString("tipoCuenta") ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Guía de Adopción",
                        fontWeight = FontWeight.Bold,
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            if (userType == "Refugio") {
                ShelterBottomNavBar(
                    onHuellasClick = { navController.navigate(Screen.ShelterPets.route) },
                    onHomeClick = { navController.navigate(Screen.ShelterHome.route) },
                    onPerfilClick = { navController.navigate(Screen.ShelterProfile.route) }
                )
            } else {
                UserBottomNavBar(
                    onHuellasClick = { navController.navigate(Screen.Pets.route) },
                    onHomeClick = { navController.navigate(Screen.Home.route) },
                    onPerfilClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Image/Logo
            Image(
                painter = painterResource(id = R.drawable.logo_pawconnect),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "¡Encuentra a tu mejor amigo!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Descubre cómo puedes cambiar una vida y encontrar un compañero para siempre.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Section: Why Adopt?
            AdoptionGuideCard(
                title = "¿Por qué adoptar?",
                description = "Al adoptar no solo cambias la vida de un animal, sino que también contribuyes a reducir el problema de mascotas sin hogar.",
                icon = R.drawable.ic_qm,
                containerColor = Color(0xFFD4C3FC)
            )

            // Section: Adoption Process
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2B6DE).copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Proceso de Adopción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    AdoptionStep(
                        icon = R.drawable.ic_uc,
                        title = "1. Prepárate",
                        description = "Asegúrate de tener el tiempo, espacio y recursos necesarios para cuidar a un nuevo integrante."
                    )

                    AdoptionStep(
                        icon = R.drawable.ic_heart,
                        title = "2. Encuentra tu compañero",
                        description = "Explora los perfiles y elige una mascota que se ajuste a tu estilo de vida."
                    )
                }
            }

            // Image Carousel
            CarouselSection()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AdoptionGuideCard(title: String, description: String, icon: Int, containerColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AdoptionStep(icon: Int, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CarouselSection() {
    val imageList = listOf(
        R.drawable.refugio_dogs_1,
        R.drawable.refugio_dogs_2,
        R.drawable.refugio_dogs_3
    )
    var currentImageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = currentImageIndex) {
        delay(5000)
        currentImageIndex = (currentImageIndex + 1) % imageList.size
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(id = imageList[currentImageIndex]),
            contentDescription = "Carrusel",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )
    }
}
