package com.example.viewmodelcomposablescopeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.viewmodelcomposablescopeplayground.ui.MainSportsScreen
import com.example.viewmodelcomposablescopeplayground.ui.theme.ViewModelComposableScopePlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViewModelComposableScopePlaygroundTheme {
                MainSportsScreen()
            }
        }
    }
}
