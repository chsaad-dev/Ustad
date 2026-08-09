package com.ustad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ustad.navigation.MainNavGraph
import com.ustad.navigation.MainViewModel
import com.ustad.navigation.SplashState
import com.ustad.presentation.theme.Primary
import com.ustad.presentation.theme.UstadTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UstadTheme {
                val splashState by mainViewModel.splashState.collectAsState()

                when (val state = splashState) {
                    is SplashState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                        }
                    }
                    is SplashState.Success -> {
                        val navController = rememberNavController()
                        MainNavGraph(
                            navController = navController,
                            startDestination = state.startDestination
                        )
                    }
                }
            }
        }
    }
}