package com.nrikesari.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.nrikesari.app.model.AppDatabase
import com.nrikesari.app.model.PreferencesManager
import com.nrikesari.app.navigation.NrikesariNavGraph
import com.nrikesari.app.ui.theme.NrikesariTheme
import com.nrikesari.app.viewmodel.AppRepository
import com.nrikesari.app.viewmodel.MainViewModel
import com.nrikesari.app.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            val isDarkMode by preferencesManager.darkModeFlow.collectAsState(initial = true)

            NrikesariTheme(darkTheme = isDarkMode) {

                val navController = rememberNavController()

                val database = AppDatabase.getDatabase(context)
                val repository = AppRepository(database.appDao())
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NrikesariNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}