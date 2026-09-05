package com.hellboy.tehrannavai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hellboy.tehrannavai.core.security.ApiKeyManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val apiKeyManager = remember { ApiKeyManager(context) }
    
    var geminiKey by remember { mutableStateOf(apiKeyManager.getGeminiKey() ?: "") }
    var mapsKey by remember { mutableStateOf(apiKeyManager.getMapsKey() ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات") },
                navigationIcon = {
                    Button(onClick = onBack, modifier = Modifier.padding(8.dp)) { Text("بازگشت") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("کلیدهای API خود را وارد کنید", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = geminiKey,
                onValueChange = { geminiKey = it },
                label = { Text("Gemini API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = mapsKey,
                onValueChange = { mapsKey = it },
                label = { Text("Google Maps API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    apiKeyManager.saveGeminiKey(geminiKey)
                    apiKeyManager.saveMapsKey(mapsKey)
                    Toast.makeText(context, "تنظیمات ذخیره شد", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ذخیره کلیدها")
            }
        }
    }
}
