package com.example.eventify.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventify.ui.screens.*
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.eventify.data.Event
import com.example.eventify.ui.screens.LoginScreen
import com.example.eventify.ui.screens.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search

@Composable
fun EventifyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        composable("start") {
            StartScreen(
                onLoginClick = {
                    navController.navigate("login")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("main") {
            MainScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("start"){
                        popUpTo("main") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main"){
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
var selectedEvent = Event(
    title = "",
    date = "",
    location = "",
    category = ""
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen( onLogout: () -> Unit) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    fun navigateToTab(route: String) {
        bottomNavController.navigate(route) {
            popUpTo("home") {
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Eventify")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF120A1F),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onLogout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF120A1F)
            ) {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = {
                        navigateToTab("home")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Početna"
                        )
                    },
                    label = { Text("Početna") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF6C2FF2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "search",
                    onClick = {
                        navigateToTab("search")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Pretraga"
                        )
                    },
                    label = { Text("Pretraga") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF6C2FF2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "add",
                    onClick = {
                        navigateToTab("add")
                    },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj")
                    },
                    label = {
                        Text("Dodaj")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF6C2FF2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "favorites",
                    onClick = {
                        navigateToTab("favorites")
                    },
                    icon = {
                        Icon(Icons.Default.Favorite, contentDescription = "Omiljeno")
                    },
                    label = {
                        Text("Omiljeno")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF6C2FF2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = {
                        navigateToTab("profile")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profil"
                        )
                    },
                    label = { Text("Profil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF6C2FF2),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                EventsScreen(
                    onEventClick = { event ->
                        selectedEvent = event
                        bottomNavController.navigate("details")
                    }
                )
            }
            composable("map") {
                MapScreen(
                    event = selectedEvent,
                    onBack = {
                        bottomNavController.popBackStack()
                    }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    onEventClick = { event ->
                        selectedEvent = event
                        bottomNavController.navigate("details")
                    }
                )
            }
            composable("add") {
                AddEventScreen()
            }
            composable("details") {
                EventDetailsScreen(
                    event = selectedEvent,
                    onBack = {
                        bottomNavController.popBackStack()
                    },
                    onDelete = {
                        bottomNavController.popBackStack()
                    },
                    onEdit = {
                        bottomNavController.navigate("edit")
                    },
                    onShare = {
                        bottomNavController.navigate("share")
                    },
                    onMapClick = {
                        bottomNavController.navigate("map")
                    }
                )
            }
            composable("edit") {
                EditEventScreen(
                    event = selectedEvent,
                    onSave = {
                        bottomNavController.popBackStack()
                    }
                )
            }
            composable("share") {
                ShareEventScreen(
                    event = selectedEvent,
                    onBack = {
                        bottomNavController.popBackStack()
                    }
                )
            }
            composable("search") {
                SearchScreen(
                    onEventClick = { event ->
                        selectedEvent = event
                        bottomNavController.navigate("details")
                    }
                )
            }
            composable("myEvents") {
                MyEventsScreen(
                    onEventClick = { event ->
                        selectedEvent = event
                        bottomNavController.navigate("details")
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = onLogout,
                    onMyEventsClick = {
                        bottomNavController.navigate("myEvents")
                    }
                )
            }

        }
    }
}