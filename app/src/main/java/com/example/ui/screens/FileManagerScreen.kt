package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ui.components.LiquidCard
import com.example.ui.components.LiquidSubScreen
import java.io.File
import kotlinx.coroutines.launch

fun getStorageRoots(context: Context): List<File> {
    val dirs = ContextCompat.getExternalFilesDirs(context, null)
    return dirs.filterNotNull().map {
        val path = it.absolutePath.substringBefore("/Android/")
        File(path)
    }.distinctBy { it.absolutePath }
}

fun getDriveLabel(context: Context, file: File): String {
    if (file.absolutePath == Environment.getExternalStorageDirectory().absolutePath) {
        return "Internal Storage"
    }
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val volume = storageManager.getStorageVolume(file)
            volume?.getDescription(context) ?: if (file.name.contains("-")) "SD Card / USB" else file.name
        } else {
            if (file.name.contains("-")) "SD Card / USB" else file.name
        }
    } catch (e: Exception) {
        file.name
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(navController: NavController) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasPermission = isGranted }

    val manageStorageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasPermission = Environment.isExternalStorageManager()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${context.packageName}")
                    manageStorageLauncher.launch(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    manageStorageLauncher.launch(intent)
                }
            } else {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    var currentPath by remember { mutableStateOf<File?>(null) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var roots by remember { mutableStateOf<List<File>>(emptyList()) }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    fun scanDrives() {
        roots = getStorageRoots(context)
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) scanDrives()
    }

    LaunchedEffect(currentPath, hasPermission) {
        if (hasPermission && currentPath != null) {
            val list = currentPath?.listFiles()
            files = list?.toList()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
        }
    }

    fun formatSize(size: Long): String {
        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024
        return when {
            size >= gb -> String.format("%.2f GB", size / gb)
            size >= mb -> String.format("%.2f MB", size / mb)
            size >= kb -> String.format("%.2f KB", size / kb)
            else -> String.format("%d B", size)
        }
    }

    if (selectedFile != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedFile = null },
            containerColor = Color(0xFF1E1B4B)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedFile!!.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Size: ${formatSize(selectedFile!!.length())}",
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        selectedFile?.delete()
                        // refresh
                        val list = currentPath?.listFiles()
                        files = list?.toList()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
                        selectedFile = null
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00))
                ) {
                    Text("Delete File", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { selectedFile = null },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Cancel", color = Color.White)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    LiquidSubScreen(
        title = if (currentPath == null) "Storage Drives" else currentPath!!.name,
        onBack = { 
            if (currentPath == null) {
                navController.popBackStack() 
            } else {
                val parent = currentPath!!.parentFile
                if (roots.any { it.absolutePath == currentPath!!.absolutePath } || parent == null) {
                    currentPath = null
                } else {
                    currentPath = parent
                }
            }
        }
    ) { paddingValues ->
        if (!hasPermission) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Storage permission is required.", color = Color.White)
            }
        } else if (currentPath == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Button(
                        onClick = { scanDrives() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Connected Storage", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
                items(roots) { root ->
                    val label = getDriveLabel(context, root)
                    val icon = if (label == "Internal Storage") Icons.Default.PhoneAndroid else if (label.contains("USB", true)) Icons.Default.Usb else Icons.Default.SdStorage
                    
                    LiquidCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { currentPath = root }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = root.absolutePath,
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        } else if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Folder is empty", color = Color.White.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(files) { file ->
                    val isDir = file.isDirectory
                    LiquidCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { 
                            if (isDir) {
                                currentPath = file
                            } else {
                                selectedFile = file
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDir) Icons.Default.Folder else if (file.name.endsWith(".jpg", true) || file.name.endsWith(".png", true)) Icons.Default.Image else Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = if (isDir) Color(0xFF00E5FF) else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                val sizeText = if (isDir) {
                                    val count = file.listFiles()?.size ?: 0
                                    "$count Items"
                                } else {
                                    formatSize(file.length())
                                }
                                Text(
                                    text = sizeText,
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
