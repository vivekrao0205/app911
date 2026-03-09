package com.nrikesari.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.model.PreferencesManager
import com.nrikesari.app.model.User
import com.nrikesari.app.firebase.FirebaseService
import com.nrikesari.app.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val isDarkMode by preferencesManager.darkModeFlow.collectAsState(initial = true)

    val coroutineScope = rememberCoroutineScope()
    val firebaseService = remember { FirebaseService() }
    
    var currentUserProfile by remember { mutableStateOf<User?>(null) }
    
    LaunchedEffect(firebaseService.currentUser) {
        val uid = firebaseService.currentUser?.uid
        if (uid != null) {
            val result = firebaseService.getUserProfile(uid)
            currentUserProfile = result.getOrNull()
        } else {
            currentUserProfile = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Settings & Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Manage your account and app preferences",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (currentUserProfile != null) {
            SectionTitle("Dashboard")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.AccountCircle,
                    title = currentUserProfile!!.name,
                    subtitle = currentUserProfile!!.email,
                    trailing = {
                        Text(
                            "Logout", 
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                firebaseService.logout()
                                currentUserProfile = null
                            }
                        )
                    }
                )
                
                HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outlineVariant)
                
                SettingsItem(
                    icon = Icons.Default.Workspaces,
                    title = "My Projects",
                    subtitle = "View your project status",
                    trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                    onClick = {
                        navController.navigate("my_projects") 
                    }
                )
                
                HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outlineVariant)
                
                SettingsItem(
                    icon = Icons.Default.Event,
                    title = "Book a Call",
                    subtitle = "Schedule a consultation",
                    trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                    onClick = {
                        navController.navigate(Screen.BookCall.route)
                    }
                )
            }
        } else {
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Login,
                    title = "Login / Sign Up",
                    subtitle = "Sign in to manage your projects",
                    trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                    onClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle("Appearance")

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Enable dark theme",
                trailing = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                preferencesManager.setDarkMode(enabled)
                            }
                        }
                    )
                }
            )

            HorizontalDivider(
                thickness = 0.6.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            SettingsItem(
                icon = Icons.Default.ColorLens,
                title = "Theme Color",
                subtitle = "Customize accent color",
                trailing = {
                    Icon(Icons.Default.ArrowForwardIos, null)
                }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        SectionTitle("Notifications")

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Receive updates and alerts",
                trailing = {
                    Switch(
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            )

            HorizontalDivider(
                thickness = 0.6.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            SettingsItem(
                icon = Icons.Default.NotificationsActive,
                title = "Promotional Alerts",
                subtitle = "Offers & announcements",
                trailing = {
                    Switch(
                        checked = false,
                        onCheckedChange = {}
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        SectionTitle("Application")

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.Info,
                title = "About App",
                subtitle = "Version 1.0.0",
                trailing = {
                    Icon(Icons.Default.ArrowForwardIos, null)
                }
            )

            HorizontalDivider(
                thickness = 0.6.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            SettingsItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "View privacy policy",
                trailing = {
                    Icon(Icons.Default.ArrowForwardIos, null)
                }
            )

            HorizontalDivider(
                thickness = 0.6.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            SettingsItem(
                icon = Icons.Default.SupportAgent,
                title = "Support",
                subtitle = "Contact support team",
                trailing = {
                    Icon(Icons.Default.ArrowForwardIos, null)
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Nrikesari App",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Text(
            text = "© 2026 Nrikesari Media",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SectionTitle(text: String) {

    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            content = content
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        trailing()
    }
}