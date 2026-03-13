package com.nrikesari.app.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.nrikesari.app.navigation.Screen
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.AuthState

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isLoading = authState is AuthState.Loading

    /* -------- GOOGLE SIGN IN CLIENT -------- */

    val googleClient = remember {

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                "283024976713-svishfb3k4gmv1nhcf90j02b76u96vo8.apps.googleusercontent.com"
            )
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, options)
    }

    /* -------- GOOGLE SIGN IN RESULT -------- */

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val data = result.data ?: return@rememberLauncherForActivityResult

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {

            val account = task.getResult(ApiException::class.java)
            val token = account.idToken

            if (token != null) {
                authViewModel.loginWithGoogle(token)
            } else {
                errorMessage = "Google token missing"
            }

        } catch (e: Exception) {
            errorMessage = "Google login failed"
        }
    }

    /* -------- AUTH STATE LISTENER -------- */

    LaunchedEffect(authState) {

        when (authState) {

            is AuthState.Authenticated -> {

                navController.navigate(Screen.Settings.route) {

                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }

                    launchSingleTop = true
                }
            }

            is AuthState.Error -> {

                errorMessage = (authState as AuthState.Error).message
                authViewModel.clearError()
            }

            else -> {}
        }
    }

    /* -------- UI -------- */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Nrikesari",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Enter email and password"
                    return@Button
                }

                authViewModel.login(
                    email.trim(),
                    password.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )

            } else {

                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                launcher.launch(googleClient.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {

            Text("Continue with Google")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {

            Text("Don't have an account? ")

            Text(
                text = "Sign up",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {

                    navController.navigate(Screen.Signup.route)
                }
            )
        }
    }
}