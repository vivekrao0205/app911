package com.nrikesari.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.nrikesari.app.model.AppDatabase
import com.nrikesari.app.model.PreferencesManager
import com.nrikesari.app.navigation.NrikesariNavGraph
import com.nrikesari.app.ui.theme.NrikesariTheme
import com.nrikesari.app.viewmodel.AppRepository
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.MainViewModel
import com.nrikesari.app.viewmodel.MainViewModelFactory
import com.nrikesari.app.viewmodel.UserViewModel
import com.nrikesari.app.firebase.FirebaseService

import android.content.Intent

class MainActivity : ComponentActivity() {

    private val clickActionRoute = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.getStringExtra("clickAction")?.let {
            clickActionRoute.value = it
            intent.removeExtra("clickAction")
        }

        enableEdgeToEdge()

        setContent {

            val context = LocalContext.current

            val preferencesManager = remember {
                PreferencesManager(context)
            }

            val isDarkMode by preferencesManager
                .darkModeFlow
                .collectAsState(initial = true)

            val themeColor by preferencesManager
                .themeColorFlow
                .collectAsState(initial = "Default")

            NrikesariTheme(
                darkTheme = isDarkMode,
                themeColor = themeColor
            ) {

                val navController = rememberNavController()

                val database = AppDatabase.getDatabase(context)
                val repository = AppRepository(database.appDao())

                val mainViewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                val authViewModel: AuthViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel()

                val authState by authViewModel.authState.collectAsState()
                LaunchedEffect(authState) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("users").document(user.uid)
                        
                        userRef.get().addOnSuccessListener { documentSnapshot ->
                            val isNew = !documentSnapshot.exists()
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val token = task.result
                                    val data = hashMapOf<String, Any>(
                                        "uid" to user.uid,
                                        "email" to (user.email ?: ""),
                                        "name" to (user.displayName ?: user.email?.substringBefore("@") ?: "User"),
                                        "fcmToken" to token
                                    )
                                    if (isNew) {
                                        data["joinedAt"] = System.currentTimeMillis()
                                        data["accountStatus"] = "active"
                                    }
                                    
                                    userRef.set(data, com.google.firebase.firestore.SetOptions.merge())
                                        .addOnSuccessListener {
                                            if (isNew) {
                                                // Trigger registration notification for admins
                                                val notifId = java.util.UUID.randomUUID().toString()
                                                val notification = com.nrikesari.app.model.Notification(
                                                    id = notifId,
                                                    userId = user.uid,
                                                    title = "New User Registered",
                                                    message = "${data["name"]} (${user.email}) has registered.",
                                                    type = "registration",
                                                    clickAction = "admin_users",
                                                    isAdminAlert = true
                                                )
                                                db.collection("notifications")
                                                    .document(notifId)
                                                    .set(notification)
                                            }
                                        }
                                }
                            }
                        }
                    }
                }

                // Global real-time local notifications listener
                val firebaseService = remember { FirebaseService() }
                val notifiedIds = remember { mutableSetOf<String>() }
                val listenerRegistration = remember { mutableStateOf<com.google.firebase.firestore.ListenerRegistration?>(null) }

                LaunchedEffect(authState) {
                    listenerRegistration.value?.remove()
                    listenerRegistration.value = null
                    
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val isAdmin = currentUser.email == "vivekrao9505@gmail.com" || currentUser.email == "anileshwar7@gmail.com"
                        val callback = { list: List<com.nrikesari.app.model.Notification> ->
                            val now = System.currentTimeMillis()
                            list.forEach { notif ->
                                // Check if notification is less than 2 minutes old and has not been notified yet in this session
                                val isRecent = (now - notif.timestamp) < 120000
                                if (isRecent && !notifiedIds.contains(notif.id)) {
                                    notifiedIds.add(notif.id)
                                    // Trigger local notification outside the app!
                                    com.nrikesari.app.service.MyFirebaseMessagingService.showNotification(
                                        context,
                                        notif.title,
                                        notif.message,
                                        notif.clickAction
                                    )
                                } else {
                                    // Make sure we mark old notifications as notified so we don't trigger them on startup
                                    notifiedIds.add(notif.id)
                                }
                            }
                        }
                        
                        if (isAdmin) {
                            listenerRegistration.value = firebaseService.listenToAdminNotifications(callback)
                        } else {
                            listenerRegistration.value = firebaseService.listenToUserNotifications(currentUser.uid, callback)
                        }
                    }
                }
                
                DisposableEffect(Unit) {
                    onDispose {
                        listenerRegistration.value?.remove()
                    }
                }

                val routeToNavigate by clickActionRoute
                LaunchedEffect(routeToNavigate, navController) {
                    routeToNavigate?.let { route ->
                        navController.navigate(route)
                        clickActionRoute.value = null
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    NrikesariNavGraph(
                        navController = navController,
                        viewModel = mainViewModel,
                        authViewModel = authViewModel,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra("clickAction")?.let {
            clickActionRoute.value = it
            intent.removeExtra("clickAction")
        }
    }
}
