package com.example.pawconnect.ui.screens.shelter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pawconnect.R
import com.example.pawconnect.Screen
import com.example.pawconnect.ui.screens.components.ShelterBottomNavBar


@Composable
fun ShelterFormularioPetsScreen(navController: NavController) {

    var petName by remember { mutableStateOf("") }
    var petSpecies by remember { mutableStateOf("") }
    var petBreed by remember { mutableStateOf("") }
    var petSize by remember { mutableStateOf("") }
    var petWeight by remember { mutableStateOf("") }
    var petAge by remember { mutableStateOf("") }
    var petSex by remember { mutableStateOf("") }
    var petPhoto by remember { mutableStateOf("") }
    var petHistory by remember { mutableStateOf("") }

    var isSterilized by remember { mutableStateOf(false) }
    var hasVaccines by remember { mutableStateOf(false) }
    var personality by remember { mutableStateOf("") }
    var medicalCondition by remember { mutableStateOf("") }
    var getAlongOtherAnimals by remember { mutableStateOf("No estoy segur@") }
    var getAlongKids by remember { mutableStateOf("No estoy segur@") }

    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            ShelterBottomNavBar(
                onHuellasClick = { navController.navigate(Screen.ShelterPets.route) },
                onHomeClick = { navController.navigate(Screen.ShelterHome.route) },
                onPerfilClick = { navController.navigate(Screen.ShelterProfile.route) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header con Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_pawconnectuniendocorazonescambiandovidas),
                    contentDescription = "Logo PawConnect",
                    modifier = Modifier
                        .height(80.dp)
                        .clickable {
                            navController.navigate(Screen.ShelterHome.route) {
                                popUpTo(Screen.ShelterHome.route) { inclusive = true }
                            }
                        }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Registro de Mascota",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Sección 1: Datos Básicos
                FormSection(title = "Datos de la mascota") {
                    CustomTextField(
                        value = petName,
                        onValueChange = { if (it.all { c -> c.isLetter() || c.isWhitespace() }) petName = it },
                        label = "Nombre de la mascota"
                    )

                    Text("Especie", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        listOf("Perro", "Gato").forEach { option ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { petSpecies = option }) {
                                RadioButton(selected = (petSpecies == option), onClick = { petSpecies = option })
                                Text(option)
                            }
                        }
                    }

                    CustomTextField(
                        value = petBreed,
                        onValueChange = { if (it.all { c -> c.isLetter() || c.isWhitespace() }) petBreed = it },
                        label = "Raza"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CustomTextField(
                            value = petSize,
                            onValueChange = { petSize = it },
                            label = "Tamaño",
                            modifier = Modifier.weight(1f)
                        )
                        CustomTextField(
                            value = petWeight,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) petWeight = it },
                            label = "Peso (kg)",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    CustomTextField(
                        value = petAge,
                        onValueChange = { petAge = it },
                        label = "Edad aproximada"
                    )
                    
                    Text("Sexo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        listOf("Macho", "Hembra").forEach { option ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { petSex = option }) {
                                RadioButton(selected = (petSex == option), onClick = { petSex = option })
                                Text(option)
                            }
                        }
                    }
                    CustomTextField(
                        value = petPhoto,
                        onValueChange = { petPhoto = it },
                        label = "Fotografía (URL)"
                    )
                }

                // Sección 2: Historia
                FormSection(title = "Historia de la mascota") {
                    Text("Cuéntanos un poco sobre su pasado y cómo llegó al refugio.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = petHistory,
                        onValueChange = { petHistory = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Sección 3: Entorno y Salud
                FormSection(title = "Datos del entorno y salud") {
                    SwitchRow(label = "¿Está esterilizado/a?", checked = isSterilized, onCheckedChange = { isSterilized = it })
                    SwitchRow(label = "¿Tiene vacunas al día?", checked = hasVaccines, onCheckedChange = { hasVaccines = it })
                    
                    CustomTextField(value = personality, onValueChange = { personality = it }, label = "¿Cómo es su personalidad?")
                    CustomTextField(value = medicalCondition, onValueChange = { medicalCondition = it }, label = "¿Condiciones médicas?")

                    Text("¿Se lleva bien con otros animales?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    SelectionRow(selectedOption = getAlongOtherAnimals, options = listOf("Sí", "No", "No estoy segur@")) { getAlongOtherAnimals = it }

                    Text("¿Se lleva bien con niños?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    SelectionRow(selectedOption = getAlongKids, options = listOf("Sí", "No", "No estoy segur@")) { getAlongKids = it }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Regresar")
                    }
                    Button(
                        onClick = {
                            val weightVal = petWeight.toDoubleOrNull()
                            when {
                                petName.isBlank() -> errorMessage = "El nombre es obligatorio."
                                petSpecies.isBlank() || (!petSpecies.equals("perro", ignoreCase = true) && !petSpecies.equals("gato", ignoreCase = true)) ->
                                    errorMessage = "La especie debe ser 'perro' o 'gato'."
                                petBreed.isBlank() -> errorMessage = "La raza es obligatoria."
                                petSize.isBlank() -> errorMessage = "El tamaño es obligatorio."
                                weightVal == null -> errorMessage = "El peso debe ser un número válido."
                                petAge.isBlank() -> errorMessage = "La edad es obligatoria."
                                petSex.isBlank() -> errorMessage = "Seleccione el sexo."
                                petPhoto.isBlank() -> errorMessage = "La URL de la foto es obligatoria."
                                petHistory.isBlank() -> errorMessage = "La historia es obligatoria."
                                personality.isBlank() -> errorMessage = "La personalidad es obligatoria."
                                else -> {
                                    errorMessage = ""
                                    com.example.pawconnect.repository.registerPet(
                                        petName, petSpecies, petBreed, petSize, petWeight, petAge, petSex, petPhoto, petHistory,
                                        isSterilized, hasVaccines, personality, medicalCondition, getAlongOtherAnimals, getAlongKids
                                    ) { success, error ->
                                        if (success) {
                                            navController.navigate(Screen.PetSuccess.route)
                                        } else {
                                            errorMessage = error ?: "Error al registrar"
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5D80))
                    ) {
                        Text("Registrar")
                    }
                }
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
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SelectionRow(selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onOptionSelected(option) }) {
                RadioButton(selected = (selectedOption == option), onClick = { onOptionSelected(option) })
                Text(option, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShelterFormularioPetsScreenPreview() {
    val navController = rememberNavController()
    ShelterFormularioPetsScreen(navController = navController)
}
