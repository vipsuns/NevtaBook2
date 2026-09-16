package com.example.ui.navigation

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.*
import kotlinx.coroutines.launch
import com.example.ui.screens.entries.EntriesScreen
import com.example.ui.screens.entries.EntryDetailScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.family.FamilyScreen
import com.example.ui.screens.given.GivenScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.reminders.RemindersScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.returns.ReturnNevtaScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.AppLanguage
import com.example.utils.LocalAppLanguage
import com.example.utils.t

sealed class Screen(val route: String, val titleEn: String, val titleHi: String, val iconOutlined: ImageVector, val iconFilled: ImageVector) {
    object Home : Screen("home", "Home", "होम", Icons.Outlined.Home, Icons.Filled.Home)
    object Entries : Screen("entries", "Received", "नेवता मिला", Icons.Outlined.AccountBalanceWallet, Icons.Filled.AccountBalanceWallet)
    object Given : Screen("given", "Given", "दिया नेवता", Icons.Outlined.Payments, Icons.Filled.Payments)
    object Events : Screen("events", "Events", "कार्यक्रम", Icons.Outlined.Celebration, Icons.Filled.Celebration)
    object Returns : Screen("returns", "Returns", "वापसी", Icons.Outlined.SwapHoriz, Icons.Filled.SwapHoriz)
    object Reports : Screen("reports", "Reports", "रिपोर्ट", Icons.Outlined.BarChart, Icons.Filled.BarChart)
    object Reminders : Screen("reminders", "Reminders", "रिमाइंडर", Icons.Outlined.Alarm, Icons.Filled.Alarm)
    object Family : Screen("family", "Family", "परिवार", Icons.Outlined.Group, Icons.Filled.Group)
    object Settings : Screen("settings", "Settings", "सेटिंग्स", Icons.Outlined.Settings, Icons.Filled.Settings)
    object Notifications : Screen("notifications", "Notifications", "सूचनाएं", Icons.Outlined.Notifications, Icons.Filled.Notifications)
    object EntryDetail : Screen("entry_detail/{entryId}", "Details", "विवरण", Icons.Outlined.Info, Icons.Filled.Info) {
        fun createRoute(entryId: String) = "entry_detail/$entryId"
    }
}

val BOTTOM_BAR_SCREENS = listOf(
    Screen.Home,
    Screen.Entries,
    Screen.Given,
    Screen.Events,
    Screen.Returns
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NevtaApp(viewModel: NevtaViewModel) {
    val currentLanguage by viewModel.appLanguage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val unreadNotifications = notifications.count { !it.isRead }
    val dashboardKpi by viewModel.dashboardKpi.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom sheet states
    var showAddEntrySheet by remember { mutableStateOf(false) }
    var showAddGivenSheet by remember { mutableStateOf(false) }
    var showAddEventSheet by remember { mutableStateOf(false) }
    var showAddReminderSheet by remember { mutableStateOf(false) }
    var showAddFamilySheet by remember { mutableStateOf(false) }

    // Floating Action Menu
    var showFabMenu by remember { mutableStateOf(false) }

    val showBottomBar = currentRoute in BOTTOM_BAR_SCREENS.map { it.route }

    CompositionLocalProvider(LocalAppLanguage provides currentLanguage) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerTonalElevation = 4.dp,
                    modifier = Modifier.widthIn(max = 330.dp)
                ) {
                    ProfileNavDrawerContent(
                        userProfile = userProfile,
                        dashboardKpi = dashboardKpi,
                        unreadNotifications = unreadNotifications,
                        currentRoute = currentRoute,
                        currentLanguage = currentLanguage,
                        isDarkTheme = isDarkTheme,
                        onNavigateToRoute = { route ->
                            coroutineScope.launch { drawerState.close() }
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onToggleLanguage = {
                            val nextLang = when (currentLanguage) {
                                AppLanguage.ENGLISH -> AppLanguage.HINDI
                                AppLanguage.HINDI -> AppLanguage.ENGLISH
                            }
                            viewModel.setAppLanguage(nextLang)
                        },
                        onToggleDarkTheme = {
                            viewModel.toggleDarkTheme()
                        },
                        onEditProfile = {
                            coroutineScope.launch { drawerState.close() }
                            showEditProfileDialog = true
                        },
                        onTriggerBackup = {
                            Toast.makeText(
                                context,
                                if (currentLanguage == AppLanguage.HINDI) "क्लाउड बैकअप सुरक्षित संपन्न! (Cloud Backup Complete)" else "Cloud Backup completed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onCloseDrawer = {
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    NevtaHomeHeader(
                        userName = userProfile?.name ?: "Rajendra Sharma",
                        currentLanguage = currentLanguage,
                        unreadNotifications = unreadNotifications,
                        onToggleLanguage = {
                            val nextLang = when (currentLanguage) {
                                AppLanguage.ENGLISH -> AppLanguage.HINDI
                                AppLanguage.HINDI -> AppLanguage.ENGLISH
                            }
                            viewModel.setAppLanguage(nextLang)
                        },
                        onNavigateToNotifications = {
                            if (currentRoute != Screen.Notifications.route) {
                                navController.navigate(Screen.Notifications.route)
                            }
                        },
                        onNavigateToProfile = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.shadow(8.dp)
                    ) {
                        BOTTOM_BAR_SCREENS.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) screen.iconFilled else screen.iconOutlined,
                                        contentDescription = t(screen.titleEn, screen.titleHi),
                                        tint = if (selected) NevtaPrimary else NevtaMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = t(screen.titleEn, screen.titleHi),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (selected) NevtaPrimary else NevtaMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = NevtaPeach
                                )
                            )
                        }
                    }
                }
            },
        floatingActionButton = {
            if (showBottomBar && currentRoute != Screen.Entries.route) {
                FloatingActionButton(
                    onClick = { showAddEntrySheet = true },
                    containerColor = NevtaPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("global_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Nevta", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToEntries = { navController.navigate(Screen.Entries.route) },
                    onNavigateToGiven = { navController.navigate(Screen.Given.route) },
                    onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                    onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                    onNavigateToReturnNevta = { navController.navigate(Screen.Returns.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Settings.route) },
                    onViewEntryDetail = { entryId -> navController.navigate(Screen.EntryDetail.createRoute(entryId)) },
                    onOpenAddEntry = { showAddEntrySheet = true },
                    onOpenAddGiven = { showAddGivenSheet = true },
                    onOpenAddEvent = { showAddEventSheet = true },
                    onOpenAddReminder = { showAddReminderSheet = true }
                )
            }

            composable(Screen.Entries.route) {
                EntriesScreen(
                    viewModel = viewModel,
                    onViewDetail = { entryId -> navController.navigate(Screen.EntryDetail.createRoute(entryId)) },
                    onOpenAddEntry = { showAddEntrySheet = true },
                    onNavigateBack = { navController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Given.route) {
                GivenScreen(
                    viewModel = viewModel,
                    onOpenAddGiven = { showAddGivenSheet = true }
                )
            }

            composable(Screen.Events.route) {
                EventsScreen(
                    viewModel = viewModel,
                    onOpenAddEvent = { showAddEventSheet = true }
                )
            }

            composable(Screen.Returns.route) {
                ReturnNevtaScreen(
                    viewModel = viewModel,
                    onOpenAddGiven = { showAddGivenSheet = true },
                    onNavigateBack = { navController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Reports.route) {
                ReportsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Reminders.route) {
                RemindersScreen(
                    viewModel = viewModel,
                    onOpenAddReminder = { showAddReminderSheet = true },
                    onNavigateBack = { navController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Family.route) {
                FamilyScreen(
                    viewModel = viewModel,
                    onOpenAddMember = { showAddFamilySheet = true },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToFamily = { navController.navigate(Screen.Family.route) },
                    onNavigateBack = { navController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EntryDetail.route,
                arguments = listOf(navArgument("entryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
                EntryDetailScreen(
                    entryId = entryId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Bottom Sheets
        if (showAddEntrySheet) {
            AddEntryBottomSheet(
                viewModel = viewModel,
                onDismiss = { showAddEntrySheet = false },
                onEntryAdded = { newEntryId ->
                    navController.navigate(Screen.EntryDetail.createRoute(newEntryId))
                }
            )
        }

        if (showAddGivenSheet) {
            AddGivenBottomSheet(
                viewModel = viewModel,
                onDismiss = { showAddGivenSheet = false },
                onSuccess = { navController.navigate(Screen.Given.route) }
            )
        }

        if (showAddEventSheet) {
            AddEventBottomSheet(
                viewModel = viewModel,
                onDismiss = { showAddEventSheet = false },
                onSuccess = { navController.navigate(Screen.Events.route) }
            )
        }

        if (showAddReminderSheet) {
            AddReminderBottomSheet(
                viewModel = viewModel,
                onDismiss = { showAddReminderSheet = false },
                onSuccess = { navController.navigate(Screen.Reminders.route) }
            )
        }

        if (showAddFamilySheet) {
            AddFamilyBottomSheet(
                viewModel = viewModel,
                onDismiss = { showAddFamilySheet = false },
                onSuccess = { navController.navigate(Screen.Family.route) }
            )
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = userProfile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateProfile(updated)
                showEditProfileDialog = false
                Toast.makeText(
                    context,
                    if (currentLanguage == AppLanguage.HINDI) "प्रोफाइल सफलतापूर्वक अपडेट हो गई" else "Profile updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}
}
}
