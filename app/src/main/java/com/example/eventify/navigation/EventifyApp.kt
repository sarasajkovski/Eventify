package com.example.eventify.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.eventify.data.Event
import com.example.eventify.ui.screens.LoginScreen
import com.example.eventify.ui.screens.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.filled.Logout
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
                    navController.navigate("start")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main")
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
                            imageVector = Icons.Default.Logout,
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
                        bottomNavController.navigate("home") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                        bottomNavController.navigate("search") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    selected = currentRoute == "favorites",
                    onClick = {
                        bottomNavController.navigate("favorites") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    selected = currentRoute == "add",
                    onClick = {
                        bottomNavController.navigate("add") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    selected = currentRoute == "profile",
                    onClick = {
                        bottomNavController.navigate("profile") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                MapScreen()
            }
            composable("favorites") {
                FavoritesScreen()
            }
            composable("add") {
                AddEventScreen()
            }
            composable("details") {
                EventDetailsScreen(
                    event = selectedEvent,
                    onDelete = {
                        bottomNavController.navigate("events")
                    },
                    onEdit = {
                        bottomNavController.navigate("edit")
                    }
                )
            }
            composable("edit") {
                EditEventScreen(
                    event = selectedEvent,
                    onSave = {
                        bottomNavController.navigate("events")
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
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        bottomNavController.navigate("start")
                    }
                )
            }
        }
    }
}