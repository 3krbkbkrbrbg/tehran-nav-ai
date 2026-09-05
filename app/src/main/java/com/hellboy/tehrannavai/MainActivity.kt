package com.hellboy.tehrannavai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.hellboy.tehrannavai.ui.screens.MapScreen
import com.hellboy.tehrannavai.ui.screens.PermissionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var hasPermissions by remember { mutableStateOf(false) }
                        
                        if (hasPermissions) {
                            MapScreen()
                        } else {
                            PermissionScreen(
                                onPermissionsGranted = {
                                    hasPermissions = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
