package com.nrikesari.app.ui.screens.portfolio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.ui.components.PortfolioCard
import com.nrikesari.app.viewmodel.MainViewModel

@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val portfolio by viewModel.portfolio.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredPortfolio = portfolio.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item { Spacer(Modifier.height(24.dp)) }

        /* -------- TITLE -------- */

        item {

            Text(
                text = "Our Portfolio",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        /* -------- SEARCH FIELD -------- */

        item {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search projects") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        /* -------- PROJECT LIST -------- */

        items(
            filteredPortfolio,
            key = { it.id }
        ) { project ->

            PortfolioCard(
                project = project,
                onClick = {
                    navController.navigate(
                        Screen.ProjectDetail.createRoute(project.id)
                    )
                }
            )
        }

        item { Spacer(Modifier.height(60.dp)) }
    }


}
