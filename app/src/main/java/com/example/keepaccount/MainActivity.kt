package com.example.keepaccount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.keepaccount.Utils.NetworkUtils
import com.example.keepaccount.ui.screens.*
import com.example.keepaccount.ui.theme.KeepAccountTheme

class MainActivity : ComponentActivity() {
    private lateinit var tutorialPref: TutorialPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tutorialPref = TutorialPref(this)

        setContent {
            KeepAccountTheme {
                MainApp(tutorialPref)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(tutorialPref: TutorialPref) {
    val navController = rememberNavController()
    val isNetworkConnected by NetworkUtils.networkState.collectAsStateWithLifecycle()
    var showTutorial by remember { mutableStateOf(!tutorialPref.isTutorialShown()) }

    // 網路斷開提示
    if (!isNetworkConnected) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(android.R.string.dialog_alert_title)) },
            text = { Text(stringResource(R.string.network_type_no_network)) },
            confirmButton = {
                TextButton(onClick = { }) { Text(stringResource(R.string.i_know)) }
            }
        )
    }

    // 教學視窗
    if (showTutorial) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(android.R.string.dialog_alert_title)) },
            text = { Text(stringResource(R.string.jump_to_nav_event_list_fragment)) },
            confirmButton = {
                TextButton(onClick = {
                    tutorialPref.setTutorialShown()
                    showTutorial = false
                    navController.navigate(Screen.EventList.route)
                }) {
                    Text(stringResource(R.string.yes))
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                val items = listOf(
                    Triple(Screen.ItemList, R.string.list_fragment_title, Icons.Default.DateRange),
                    Triple(Screen.EventList, R.string.event_fragment_title, Icons.Default.List),
                    Triple(Screen.Strip, R.string.budget_fragment_title, Icons.Default.Money),
                    Triple(Screen.BarcodeInvoice, R.string.barcode_and_invoice_fragment_title, Icons.Default.Search),
                    Triple(Screen.Visual, R.string.visual_fragment_title, Icons.Default.PieChart)
                )

                items.forEach { (screen, labelRes, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(stringResource(labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ItemList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ItemList.route) { ItemListScreen(navController) }
            composable(Screen.EventList.route) { EventListScreen(navController) }
            composable(Screen.Strip.route) { StripScreen(navController) }
            composable(Screen.BarcodeInvoice.route) { BarcodeAndInvoiceScreen(navController) }
            composable(Screen.Visual.route) { VisualScreen() }

            composable(
                route = Screen.AddItem.route,
                arguments = listOf(
                    navArgument("year") { type = NavType.StringType },
                    navArgument("month") { type = NavType.StringType },
                    navArgument("day") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getString("year") ?: ""
                val month = backStackEntry.arguments?.getString("month") ?: ""
                val day = backStackEntry.arguments?.getString("day") ?: ""
                AddItemScreen(navController, year, month, day)
            }

            composable(
                route = Screen.AddEvent.route,
                arguments = listOf(navArgument("eventId") { type = NavType.IntType; defaultValue = -1 })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId") ?: -1
                AddEventScreen(navController, eventId)
            }

            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getInt("eventId") ?: -1
                EventDetailScreen(navController, eventId)
            }
        }
    }
}
