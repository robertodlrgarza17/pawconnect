package com.example.pawconnect

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.pawconnect.ui.screens.LoginScreen
import com.example.pawconnect.ui.screens.SignUpScreen
import com.example.pawconnect.ui.screens.SuccessScreen
import com.example.pawconnect.ui.screens.user.AdoptionSuccessScreen
import com.example.pawconnect.ui.screens.shelter.ShelterEditPetScreen
import com.example.pawconnect.ui.screens.shelter.PetSuccessScreen
import com.example.pawconnect.ui.screens.shelter.ShelterHomeScreen
import com.example.pawconnect.ui.screens.shelter.ShelterProfileScreen
import com.example.pawconnect.ui.screens.shelter.ShelterRegistrationPetsScreen
import com.example.pawconnect.ui.screens.shelter.ShelterPetsScreen
import com.example.pawconnect.ui.screens.shelter.ShelterFormularioPetsScreen
import com.example.pawconnect.ui.screens.shelter.ShelterSuccess
import com.example.pawconnect.ui.screens.shelter.ShelterPruebas
import com.example.pawconnect.ui.screens.shelter.PlantillaRefugio
import com.example.pawconnect.ui.screens.user.EditProfile
import com.example.pawconnect.ui.screens.user.FavoriteScreen
import com.example.pawconnect.ui.screens.user.ProfileScreen
import com.example.pawconnect.ui.screens.user.UserHomeScreen
import com.example.pawconnect.ui.screens.user.UserDogsScreen
import com.example.pawconnect.ui.screens.user.UserCatsScreen
import com.example.pawconnect.ui.screens.user.PetDetailScreen
import com.example.pawconnect.ui.screens.user.PlantillaUsers
import com.example.pawconnect.ui.screens.user.FormularioAdoptame
import com.example.pawconnect.ui.screens.user.GuiaDeAdopcion
import com.example.pawconnect.ui.screens.user.SolitudesUser
import com.example.pawconnect.ui.screens.shelter.ShelterRequestDetailScreen
import com.example.pawconnect.ui.screens.shelter.ShelterRevisionPetsScreen
import com.example.pawconnect.ui.screens.shelter.ShelterCatsScreen
import com.example.pawconnect.ui.screens.shelter.ShelterDogsScreen
import com.example.pawconnect.ui.screens.user.SuccessUser
import com.example.pawconnect.ui.screens.user.NotificationPreferences

sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object Register : Screen("register")
    object Success : Screen("success")

    // Usuario
    object Home : Screen("home")
    object Pets : Screen("pets")
    object Profile : Screen("profile")
    object UserHome : Screen("user_home")
    object UserDogs : Screen("user_dogs")
    object UserCats : Screen("user_cats")
    object UserRegistrationPets : Screen("user_registration_pets")
    object PetDetails : Screen("pet_details/{petId}")
    object Favorite : Screen("favorite")
    object FormularioAdoptame : Screen("formulario_adoptame")
    object GuiaDeAdopcion: Screen("guia_de_adopcion")
    object EditProfileScreen: Screen("editar_perfil")
    object SuccessUser: Screen("success_user")
    object AdoptionSuccess : Screen("adoption_success")
    object NotificationPreferences: Screen("notificaciones")

    // Refugio
    object ShelterHome : Screen("shelter_home")
    object ShelterProfile : Screen("shelter_profile")
    object ShelterRegistrationPets : Screen("shelter_registration_pets")
    object ShelterPets : Screen("shelter_pets")
    object ShelterFormularioPets : Screen("shelter_formulario_pets")
    object ShelterSuccess : Screen("shelter_success")
    object ShelterPruebas : Screen("shelter_pruebas")
    object ShelterRequestDetail : Screen("shelter_request_detail/{requestId}")
    object ShelterRevisionPetsScreen : Screen("shelter_revision_pets")
    object PetSuccess : Screen("pet_success")
    object ShelterEditPet : Screen("shelter_edit_pet/{petId}")


    // Otros
    object PlantillaRefugio : Screen("plantilla_refugio")
    object PlantillaUsers : Screen("plantilla_users")
    object ShelterCatsScreen : Screen("shelter_cats_screen")
    object ShelterDogsScreen : Screen("shelter_dogs_screen")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { SignUpScreen(navController) }
        composable(Screen.Success.route) { SuccessScreen(navController) }

        // Usuario
        composable(Screen.Home.route) { UserHomeScreen(navController) }
        composable(Screen.Pets.route) { SolitudesUser(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.UserHome.route) { UserHomeScreen(navController) }
        composable(Screen.UserDogs.route) { UserDogsScreen(navController) }
        composable(Screen.UserCats.route) { UserCatsScreen(navController) }
        composable(Screen.UserRegistrationPets.route) { ShelterFormularioPetsScreen(navController) }
        composable(
            route = Screen.PetDetails.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            PetDetailScreen(navController, petId)
        }
        composable(Screen.Favorite.route) { FavoriteScreen(navController) }
        composable(Screen.GuiaDeAdopcion.route) { GuiaDeAdopcion(navController) }
        composable(Screen.EditProfileScreen.route) { EditProfile(navController) }
        composable(
            route = "FormularioAdoptame/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            FormularioAdoptame(navController, petId)
        }
        composable(Screen.SuccessUser.route) { SuccessUser(navController) }
        composable(Screen.AdoptionSuccess.route) { AdoptionSuccessScreen(navController) }
        composable(Screen.NotificationPreferences.route) {NotificationPreferences(navController) }

        // Refugio
        composable(Screen.ShelterHome.route) { ShelterHomeScreen(navController) }
        composable(Screen.ShelterProfile.route) { ShelterProfileScreen(navController) }
        composable(Screen.ShelterRegistrationPets.route) { ShelterRegistrationPetsScreen(navController) }
        composable(Screen.ShelterPets.route) { ShelterPetsScreen(navController) }
        composable(Screen.ShelterFormularioPets.route) { ShelterFormularioPetsScreen(navController) }
        composable(Screen.ShelterSuccess.route) { ShelterSuccess(navController) }
        composable(Screen.ShelterPruebas.route) { ShelterPruebas(navController) }
        composable(Screen.PlantillaRefugio.route) { PlantillaRefugio(navController) }
        composable(Screen.PlantillaUsers.route) { PlantillaUsers(navController) }
        composable(
            route = "ShelterRequestDetail/{requestId}",
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            ShelterRequestDetailScreen(navController, requestId)
        }
        composable(Screen.ShelterRevisionPetsScreen.route) { ShelterRevisionPetsScreen(navController) }
        composable(Screen.ShelterCatsScreen.route) { ShelterCatsScreen(navController) }
        composable(Screen.ShelterDogsScreen.route) { ShelterDogsScreen(navController) }
        composable(Screen.PetSuccess.route) { PetSuccessScreen(navController) }
        composable(
            route = Screen.ShelterEditPet.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            ShelterEditPetScreen(navController, petId)
        }

    }
}



