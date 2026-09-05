package com.hellboy.tehrannavai.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(onPermissionsGranted: () -> Unit) {
    val context = LocalContext.current
    
    // درخواست دسترسی‌های لوکیشن طبق نیازمندی‌ها
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    if (locationPermissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "دسترسی به مکان",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "برای مسیریابی دقیق و پیشنهاد مسیر، اپلیکیشن نیاز به دسترسی موقعیت مکانی (GPS) شما دارد.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (locationPermissionsState.shouldShowRationale) {
                // کاربر قبلاً رد کرده است، توضیح بیشتر
                Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                    Text("لطفاً اجازه دهید")
                }
            } else {
                // اولین بار است یا کاربر Never Ask Again زده
                Button(onClick = {
                    if (locationPermissionsState.revokedPermissions.size == locationPermissionsState.permissions.size) {
                        // اولین درخواست
                        locationPermissionsState.launchMultiplePermissionRequest()
                    } else {
                        // هدایت به تنظیمات در صورت بلاک شدن
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }
                }) {
                    Text("اعطای دسترسی مکان")
                }
            }
        }
    }
}
