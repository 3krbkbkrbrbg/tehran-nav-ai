package com.hellboy.tehrannavai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import com.hellboy.tehrannavai.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme {
                    var currentRoute by remember { mutableStateOf("permission") }
                    var hasPermissions by remember { mutableStateOf(false) }

                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    if (!hasPermissions) {
                        PermissionScreen(onPermissionsGranted = { hasPermissions = true; currentRoute = "map" })
                    } else {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                ModalDrawerSheet {
                                    Text("منوی مسیریاب", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                                    Divider()
                                    NavigationDrawerItem(
                                        label = { Text("نقشه و مسیریابی") },
                                        selected = currentRoute == "map",
                                        onClick = { currentRoute = "map"; scope.launch { drawerState.close() } }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text("نقشه‌های آفلاین") },
                                        selected = currentRoute == "offline",
                                        onClick = { currentRoute = "offline"; scope.launch { drawerState.close() } }
                                    )
                                    NavigationDrawerItem(
                                        label = { Text("تنظیمات کلید API") },
                                        selected = currentRoute == "settings",
                                        onClick = { currentRoute = "settings"; scope.launch { drawerState.close() } }
                                    )
                                }
                            }
                        ) {
                            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                                when (currentRoute) {
                                    "map" -> MapScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                    "offline" -> OfflineMapScreen(onBack = { currentRoute = "map" })
                                    "settings" -> SettingsScreen(onBack = { currentRoute = "map" })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
