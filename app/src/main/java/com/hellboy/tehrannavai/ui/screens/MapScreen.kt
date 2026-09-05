package com.hellboy.tehrannavai.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.hellboy.tehrannavai.core.ai.GeminiService
import com.hellboy.tehrannavai.core.navigation.RetrofitClient
import com.hellboy.tehrannavai.core.security.ApiKeyManager
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(onMenuClick: () -> Unit) {
    val context = LocalContext.current
    val apiKeyManager = remember { ApiKeyManager(context) }
    
    val tehran = LatLng(35.6892, 51.3890)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tehran, 12f)
    }
    
    var destinationText by remember { mutableStateOf("") }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var routeInfo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isAiMode by remember { mutableStateOf(false) } // تغییر حالت بین عادی و هوشمند
    
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            if (routePoints.isNotEmpty()) {
                Polyline(points = routePoints, color = Color.Blue, width = 12f)
            }
        }
        
        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            // دکمه منوی همبرگری بالای صفحه
            SmallTopAppBar(
                title = { Text("مسیریاب هوشمند") },
                navigationIcon = {
                    Button(onClick = onMenuClick, modifier = Modifier.padding(8.dp)) {
                        Text("≡")
                    }
                }
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("حالت AI (Gemini)")
                        Switch(
                            checked = isAiMode,
                            onCheckedChange = { isAiMode = it }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = destinationText,
                            onValueChange = { destinationText = it },
                            label = { Text(if (isAiMode) "چی نیاز داری؟ (مثلا کافه)" else "کجا می‌خوای بری؟") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (destinationText.isNotEmpty()) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        try {
                                            var finalDestination = destinationText
                                            
                                            if (isAiMode) {
                                                val geminiKey = apiKeyManager.getGeminiKey()
                                                if (geminiKey.isNullOrEmpty()) {
                                                    Toast.makeText(context, "کلید Gemini تنظیم نشده است!", Toast.LENGTH_SHORT).show()
                                                    isLoading = false
                                                    return@launch
                                                }
                                                val geminiService = GeminiService(geminiKey)
                                                // پردازش با AI
                                                finalDestination = geminiService.extractDestinationFromPrompt(destinationText)
                                                Toast.makeText(context, "هوش مصنوعی مقصد را یافت: \$finalDestination", Toast.LENGTH_LONG).show()
                                            }

                                            val mapsKey = apiKeyManager.getMapsKey() ?: "YOUR_MAPS_KEY_FALLBACK"
                                            val originStr = "${cameraPositionState.position.target.latitude},${cameraPositionState.position.target.longitude}"
                                            
                                            val response = RetrofitClient.directionsApi.getDirections(
                                                origin = originStr,
                                                destination = finalDestination,
                                                apiKey = mapsKey
                                            )
                                            
                                            if (response.routes.isNotEmpty()) {
                                                val leg = response.routes[0].legs[0]
                                                routeInfo = "مقصد: \$finalDestination\nفاصله: ${leg.distance.text} | زمان: ${leg.duration.text}"
                                                routePoints = decodePoly(response.routes[0].overview_polyline.points)
                                            } else {
                                                routeInfo = "مسیری یافت نشد."
                                            }
                                        } catch (e: Exception) {
                                            routeInfo = "خطا: ${e.localizedMessage}"
                                        }
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Text(if (isLoading) "..." else "بریم")
                        }
                    }
                }
            }
        }
        
        if (routeInfo.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(text = routeInfo, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
