package com.nrikesari.app.ui.screens.services

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.ServiceCard
import com.nrikesari.app.viewmodel.MainViewModel

@Composable
fun ServicesScreen(navController: NavController, viewModel: MainViewModel) {
    val services by viewModel.services.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Our Services",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        LazyColumn {
            items(services) { service ->
                ServiceCard(
                    service = service,
                    onClick = { navController.navigate(Screen.ServiceDetail.createRoute(service.id)) }
                )
            }
        }
    }
}
