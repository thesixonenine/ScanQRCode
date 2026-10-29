package io.github.thesixonenine.scanqrcode.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import io.github.thesixonenine.scanqrcode.ui.theme.ScanQRCodeTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CAMERA_PAUSE_TIMEOUT_MS = 10_000L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanQRCodeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var scanned by remember { mutableStateOf(false) }
    val barcodeView = remember { mutableStateOf<DecoratedBarcodeView?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var cameraActive by remember { mutableStateOf(false) }

    fun startCamera() {
        if (!cameraActive && barcodeView.value != null) {
            barcodeView.value?.resume()
            cameraActive = true
        }
    }

    fun stopCamera() {
        if (cameraActive) {
            barcodeView.value?.pause()
            cameraActive = false
        }
    }

    DisposableEffect(context) {
        val lifecycleOwner = context as? LifecycleOwner
        var timeoutJob: Job? = null
        val observer = lifecycleOwner?.lifecycle?.let { lifecycle ->
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        scanned = false
                        timeoutJob?.cancel()
                        timeoutJob = null
                        startCamera()
                    }

                    Lifecycle.Event.ON_STOP -> {
                        timeoutJob?.cancel()
                        timeoutJob = coroutineScope.launch {
                            delay(CAMERA_PAUSE_TIMEOUT_MS)
                            stopCamera()
                        }
                    }

                    else -> Unit
                }
            }.also { lifecycle.addObserver(it) }
        }
        onDispose {
            timeoutJob?.cancel()
            observer?.let { lifecycleOwner.lifecycle.removeObserver(it) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        DecoratedBarcodeView(ctx).apply {
                            barcodeView.value = this
                            decodeContinuous(
                                BarcodeCallback { result: BarcodeResult ->
                                    if (!scanned && result.text != null) {
                                        scanned = true
                                        val intent = Intent(ctx, DisplayActivity::class.java).apply {
                                            putExtra("qr_content", result.text)
                                        }
                                        ctx.startActivity(intent)
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                DisposableEffect(barcodeView.value) {
                    startCamera()
                    onDispose {
                        stopCamera()
                    }
                }
                Text(
                    text = "Point camera at QR code",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            } else {
                Text(
                    text = "Camera permission required to scan QR codes",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun AndroidView(
    factory: (android.content.Context) -> android.view.View,
    modifier: Modifier = Modifier
) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = factory,
        modifier = modifier
    )
}
