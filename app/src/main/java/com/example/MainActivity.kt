package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.NevtaApp
import com.example.ui.theme.NevtaBookTheme
import com.example.ui.viewmodel.NevtaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NevtaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            NevtaBookTheme(darkTheme = isDarkTheme) {
                NevtaApp(viewModel = viewModel)
            }
        }
    }
}

