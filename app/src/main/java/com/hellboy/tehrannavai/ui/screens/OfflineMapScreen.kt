package com.hellboy.tehrannavai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineMapScreen(onBack: () -> Unit) {
    val provinces = listOf("تهران (۱۵۰ مگابایت)", "البرز (۴۵ مگابایت)", "اصفهان (۱۲۰ مگابایت)")
    val coroutineScope = rememberCoroutineScope()
    
    // وضعیت دانلود (شبیه‌سازی)
    var downloadingProvince by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نقشه‌های آفلاین (OSM)") },
                navigationIcon = {
                    Button(onClick = onBack, modifier = Modifier.padding(8.dp)) { Text("بازگشت") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    "دانلود نقشه برای استفاده کاملاً آفلاین بدون اینترنت:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(provinces) { province ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(province, style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = {
                                    downloadingProvince = province
                                    progress = 0f
                                    coroutineScope.launch {
                                        while (progress < 1f) {
                                            delay(500)
                                            progress += 0.1f
                                        }
                                        downloadingProvince = null
                                    }
                                },
                                enabled = downloadingProvince == null
                            ) {
                                Text("دانلود (MBTiles)")
                            }
                        }
                        
                        if (downloadingProvince == province) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("در حال دانلود... ${(progress * 100).toInt()}%")
                        }
                    }
                }
            }
        }
    }
}
