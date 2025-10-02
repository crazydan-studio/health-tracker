package org.crazydan.studio.app.healthtracker.ui.screen

import android.content.Context
import android.net.Uri
import android.os.Handler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import org.crazydan.studio.app.healthtracker.ui.component.CameraOption
import org.crazydan.studio.app.healthtracker.ui.component.CameraPreview

/**
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-09-29
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SyncHealthDataScreen(
    onResult: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var scanResult by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(true) }
    var showResultDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            processImageFromUri(context, it) { result ->
                scanResult = result
                showResultDialog = true
                onResult(result)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("二维码扫描器") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                icon = { Icon(Icons.Default.PhotoLibrary, "相册") },
                text = { Text("从相册选择") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CameraPreview(
                    CameraOption(
                        analyzer = { imageProxy ->
                            if (isScanning) {
                                processImageProxy(imageProxy, context) { result ->
                                    // 在主线程中回调结果
                                    Handler(context.mainLooper).post {
                                        scanResult = result
                                        showResultDialog = true
                                        isScanning = false
                                        onResult(result)
                                    }
                                }
                            } else {
                                imageProxy.close()
                            }
                        }
                    ))

                if (isScanning) {
                    ScanningFrame()
                }
            }

            // 扫描结果
            scanResult?.let {
                ScanResultSection(result = it)
            }
        }
    }

    // 结果显示对话框
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = {
                showResultDialog = false
                isScanning = true // 重新开始扫描
            },
            title = { Text("扫描结果") },
            text = { Text(scanResult ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResultDialog = false
                        isScanning = true
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
fun ScanningFrame() {
    Box(
        modifier = Modifier
            .size(250.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, ShapeDefaults.ExtraSmall)
    ) {
        // 扫描动画线
        var offset by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                offset = 0f
                repeat(250) {
                    offset += 1f
                    delay(10)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = offset.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun ScanResultSection(result: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "扫描结果:",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = result,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// 图像处理函数
@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    context: Context,
    onResult: (String) -> Unit
) {
    val inputImage = InputImage.fromMediaImage(
        imageProxy.image!!,
        imageProxy.imageInfo.rotationDegrees
    )

    val scanner: BarcodeScanner = BarcodeScanning.getClient()

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                val barcode = barcodes.first()
                val result = when (barcode.valueType) {
                    Barcode.TYPE_URL -> barcode.url?.url ?: barcode.rawValue ?: ""
                    Barcode.TYPE_TEXT -> barcode.rawValue ?: ""
                    Barcode.TYPE_WIFI -> "WiFi: ${barcode.wifi?.ssid}"
                    Barcode.TYPE_CONTACT_INFO -> "联系人: ${barcode.contactInfo?.title}"
                    Barcode.TYPE_EMAIL -> "邮箱: ${barcode.email?.address}"
                    Barcode.TYPE_PHONE -> "电话: ${barcode.phone?.number}"
                    else -> barcode.rawValue ?: "未知格式"
                }
                onResult(result)
            }
        }
        .addOnFailureListener {
            // 可选的错误处理
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

// 处理从相册选择的图片
private fun processImageFromUri(
    context: Context,
    uri: Uri,
    onResult: (String) -> Unit
) {
    try {
        val inputImage = InputImage.fromFilePath(context, uri)
        val scanner: BarcodeScanner = BarcodeScanning.getClient()

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    val result = when (barcode.valueType) {
                        Barcode.TYPE_URL -> barcode.url?.url ?: barcode.rawValue ?: ""
                        Barcode.TYPE_TEXT -> barcode.rawValue ?: ""
                        Barcode.TYPE_WIFI -> "WiFi: ${barcode.wifi?.ssid}"
                        else -> barcode.rawValue ?: "未知格式"
                    }
                    onResult(result)
                } else {
                    onResult("未识别到二维码")
                }
            }
            .addOnFailureListener {
                onResult("识别失败: ${it.message}")
            }
    } catch (e: Exception) {
        onResult("处理图片失败: ${e.message}")
    }
}