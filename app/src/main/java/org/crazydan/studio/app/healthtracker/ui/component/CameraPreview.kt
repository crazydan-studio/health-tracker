package org.crazydan.studio.app.healthtracker.ui.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.crazydan.studio.app.healthtracker.R
import java.util.concurrent.Executor
import java.util.concurrent.Executors

data class CameraOption(
    /**
     * 启用前置或后置摄像头，可选值：
     * [CameraSelector.LENS_FACING_BACK]、[CameraSelector.LENS_FACING_FRONT]
     */
    val selector: Int = CameraSelector.LENS_FACING_BACK,
    val analyzer: ImageAnalysis.Analyzer,
)

private sealed class CameraState {
    data object Initializing : CameraState()
    data object Starting : CameraState()
    data object Started : CameraState()

    data object Granted : CameraState()
    data object Denied : CameraState()
    data object NotDetected : CameraState()

    data class Error(val msg: String) : CameraState()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview(
    option: CameraOption,
) {
    val context = LocalContext.current

    var cameraState by remember {
        mutableStateOf<CameraState>(CameraState.Initializing)
    }

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    LaunchedEffect(Unit) {
        cameraState = checkCameraStatus(context, cameraPermissionState)
    }
    LaunchedEffect(cameraPermissionState.status) {
        cameraState = checkCameraStatus(context, cameraPermissionState)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (cameraState) {
            is CameraState.Initializing -> {
                TextMessageView(R.string.msg_initializing_camera)
            }

            is CameraState.NotDetected -> {
                TextMessageView(R.string.msg_no_detected_camera)
            }

            is CameraState.Denied -> {
                CameraDeniedView {
                    cameraPermissionState.launchPermissionRequest()
                }
            }

            is CameraState.Error -> {
                CameraErrorView(
                    msg = (cameraState as CameraState.Error).msg,
                    onRetry = {
                        cameraState = CameraState.Starting
                    },
                )
            }

            is CameraState.Granted -> {
                cameraProvider = null
                cameraState = CameraState.Starting
            }

            is CameraState.Starting, is CameraState.Started -> {
                cameraProvider?.let {
                    CameraRunningView(
                        provider = it,
                        selector = option.selector,
                        analyzer = option.analyzer,
                        onSuccess = {
                            cameraState = CameraState.Started
                        },
                        onError = { msg ->
                            cameraState = CameraState.Error(msg)
                        },
                    )
                }

                // Note: 确保初始化提示信息在最上层
                if (cameraState == CameraState.Starting) {
                    TextMessageView(R.string.msg_initializing_camera)

                    LaunchedEffect(Unit) {
                        val future = ProcessCameraProvider.getInstance(context)
                        future.addListener({
                            try {
                                cameraProvider = future.get()
                            } catch (e: Exception) {
                                cameraState = CameraState.Error(e.message + "")
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                }
            }
        }
    }
}

private val cameraExecutor: Executor = Executors.newSingleThreadExecutor()

@Composable
private fun CameraRunningView(
    provider: ProcessCameraProvider,
    selector: Int,
    analyzer: ImageAnalysis.Analyzer,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraSelector =
        CameraSelector.Builder()
            .requireLensFacing(selector)
            .build()

    if (!provider.hasCamera(cameraSelector)) {
        onError(
            stringResource(
                when (selector) {
                    CameraSelector.LENS_FACING_BACK -> {
                        R.string.msg_no_back_camera
                    }

                    CameraSelector.LENS_FACING_FRONT -> {
                        R.string.msg_no_front_camera
                    }

                    else -> {
                        R.string.msg_no_detected_camera
                    }
                }
            )
        )
        return
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { view ->
            try {
                val preview = Preview.Builder().build()
                preview.surfaceProvider = view.surfaceProvider

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis,
                )

                onSuccess()
            } catch (e: Exception) {
                onError(e.message + "")
            }
        }
    )
}

@Composable
private fun CameraDeniedView(
    onRequestPermission: () -> Unit
) {
    HelperView {
        Text(
            text = stringResource(R.string.title_camera_denied),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.msg_allow_to_use_camera),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text(stringResource(R.string.btn_grant_permission))
        }
    }
}

@Composable
private fun CameraErrorView(
    msg: String,
    onRetry: () -> Unit
) {
    HelperView {
        Text(
            text = stringResource(R.string.title_camera_start_failed),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = msg,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.btn_retry))
        }
    }
}

@Composable
private fun TextMessageView(
    strId: Int,
) {
    HelperView {
        Text(stringResource(strId))
    }
}

@Composable
private fun HelperView(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
private fun checkCameraStatus(
    context: Context,
    permission: PermissionState,
): CameraState {
    return when {
        permission.status.isGranted -> {
            if (hasCameraHardware(context)) {
                CameraState.Granted
            } else {
                CameraState.NotDetected
            }
        }

        permission.status.shouldShowRationale -> {
            CameraState.Denied
        }

        else -> {
            CameraState.Denied
        }
    }
}

/** 设备没有摄像头硬件 */
private fun hasCameraHardware(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
}