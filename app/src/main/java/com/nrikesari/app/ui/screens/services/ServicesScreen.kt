package com.nrikesari.app.ui.screens.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.ServiceCard
import com.nrikesari.app.viewmodel.MainViewModel

@Composable
fun ServicesScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val services by viewModel.services.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(36.dp))

        /* -------- TITLE -------- */

        Text(
            text = "Our Services",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Solutions we offer to help brands grow digitally.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        /* ------- SERVICES LIST ------- */

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            items(
                items = services,
                key = { it.id }
            ) { service ->

                ServiceCard(
                    service = service,
                    onClick = {
                        navController.navigate(
                            Screen.ServiceDetail.createRoute(service.id)
                        )
                    }
                )
            }
        }
    }
}