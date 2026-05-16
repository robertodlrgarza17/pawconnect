package com.example.pawconnect


import androidx.activity.compose.setContent
import com.example.pawconnect.theme.AppTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa Firebase
        FirebaseApp.initializeApp(this)
        
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(navController)
                }
            }
        }
    }
}

