package com.nrikesari.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrikesari.app.model.PreferencesManager
import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    val isDarkMode by preferencesManager.darkModeFlow.collectAsState(initial = true)
    val selectedTheme by preferencesManager.themeColorFlow.collectAsState(initial = "Default")

    val coroutineScope = rememberCoroutineScope()

    val currentUserProfile by authViewModel.currentUserProfile.collectAsState()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var promoEnabled by remember { mutableStateOf(false) }

    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Settings & Profile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Manage your account and app preferences",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(28.dp))

        currentUserProfile?.let { user ->

            SectionTitle("Dashboard")

            SettingsCard {

                SettingsItem(
                    icon = Icons.Default.AccountCircle,
                    title = user.name,
                    subtitle = user.email,
                    trailing = {
                        Text(
                            "Logout",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                authViewModel.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Default.Workspaces,
                    title = "My Projects",
                    subtitle = "View your project status",
                    trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                    onClick = { navController.navigate(Screen.MyProjects.route) }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Default.Event,
                    title = "Book a Call",
                    subtitle = "Schedule a consultation",
                    trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                    onClick = { navController.navigate(Screen.BookCall.route) }
                )
            }

        } ?: run {

            SettingsCard {

                SettingsItem(
                    icon = Icons.Default.Login,
                    title = "Login / Sign Up",
                    subtitle = "Sign in to manage your projects",
                    trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                    onClick = { navController.navigate(Screen.Login.route) }
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        SectionTitle("Appearance")

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Enable dark theme",
                trailing = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = {
                            coroutineScope.launch {
                                preferencesManager.setDarkMode(it)
                            }
                        }
                    )
                }
            )

            HorizontalDivider()

            SettingsItem(
                icon = Icons.Default.ColorLens,
                title = "Theme Color",
                subtitle = "Current: $selectedTheme",
                trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                onClick = { showThemeDialog = true }
            )
        }

        Spacer(Modifier.height(22.dp))

        SectionTitle("Notifications")

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Receive updates and alerts",
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            )

            HorizontalDivider()

            SettingsItem(
                icon = Icons.Default.NotificationsActive,
                title = "Promotional Alerts",
                subtitle = "Offers & announcements",
                trailing = {
                    Switch(
                        checked = promoEnabled,
                        onCheckedChange = { promoEnabled = it }
                    )
                }
            )
        }

        Spacer(Modifier.height(22.dp))

        SectionTitle("Application")

        SettingsCard {

            SettingsItem(
                icon = Icons.Default.Info,
                title = "About App",
                subtitle = "Version 1.0.0",
                trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                onClick = { showAboutDialog = true }
            )

            HorizontalDivider()

            SettingsItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "View privacy policy",
                trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                onClick = { showPrivacyDialog = true }
            )

            HorizontalDivider()

            SettingsItem(
                icon = Icons.Default.SupportAgent,
                title = "Support",
                subtitle = "Email support team",
                trailing = { Icon(Icons.Default.ArrowForwardIos, null) },
                onClick = {

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:contact@nrikesari.in")
                        putExtra(Intent.EXTRA_SUBJECT, "Nrikesari App Support")
                    }

                    context.startActivity(intent)
                }
            )
        }

        Spacer(Modifier.height(30.dp))
    }

    if (showThemeDialog) {

        val themeColors = listOf(
            "Default" to Color(0xFF9E3A3A),
            "Slate" to Color(0xFF475569),
            "Indigo" to Color(0xFF4F46E5),
            "Emerald" to Color(0xFF059669),
            "Amber" to Color(0xFFD97706),
            "Rose" to Color(0xFFE11D48)
        )

        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme Color") },
            text = {

                Column {

                    themeColors.forEach { (name, color) ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    coroutineScope.launch {
                                        preferencesManager.setThemeColor(name)
                                    }

                                    showThemeDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color, CircleShape)
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showAboutDialog) {

        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Nrikesari") },
            text = {
                Text(
                    "Nrikesari builds modern digital solutions including apps and websites to help businesses grow."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showPrivacyDialog) {

        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Text(
                    "Your privacy is important to us. Nrikesari collects only necessary information to provide services. Your data is never sold or shared without consent."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK")
                }
            }
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = title,
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