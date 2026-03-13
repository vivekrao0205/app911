package com.nrikesari.app.ui.screens.auth

import android.app.Activity
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

import com.nrikesari.app.navigation.Screen
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SignupScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val activity = context as Activity

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    // Google Sign In
    val googleSignInClient = remember {

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID")
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, options)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {

            val account = task.getResult(ApiException::class.java)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnSuccessListener {

                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }

        } catch (e: Exception) {

            errorMessage = "Google Sign Up Failed"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (name.isBlank() || email.isBlank() || password.isBlank()) {

                            errorMessage = "Please fill all fields"
                            return@Button
                        }

                        if (password != confirmPassword) {

                            errorMessage = "Passwords do not match"
                            return@Button
                        }

                        isLoading = true

                        auth.createUserWithEmailAndPassword(
                            email.trim(),
                            password.trim()
                        ).addOnSuccessListener {

                            isLoading = false

                            navController.navigate(Screen.Settings.route) {
                                popUpTo(Screen.Signup.route) { inclusive = true }
                            }

                        }.addOnFailureListener {

                            isLoading = false
                            errorMessage = it.message
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {

                    if (isLoading)
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    else
                        Text("Create Account")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {

                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {

                    Text("Continue with Google")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row {

                    Text("Already have an account? ")

                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {

                            navController.navigate(Screen.Login.route)
                        }
                    )
                }
            }
        }
    }
}

