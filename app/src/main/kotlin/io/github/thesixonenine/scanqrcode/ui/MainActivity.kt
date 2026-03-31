package io.github.thesixonenine.scanqrcode.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import io.github.thesixonenine.scanqrcode.data.model.ReleaseInfo
import io.github.thesixonenine.scanqrcode.data.repository.ApkDownloader
import io.github.thesixonenine.scanqrcode.data.repository.UpdateChecker
import io.github.thesixonenine.scanqrcode.ui.components.DownloadDialog
import io.github.thesixonenine.scanqrcode.ui.components.UpdateDialog
import io.github.thesixonenine.scanqrcode.ui.theme.ScanQRCodeTheme
import io.github.thesixonenine.scanqrcode.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
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
    val activity = context as ComponentActivity
    val scope = rememberCoroutineScope()

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

    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var releaseInfo by remember { mutableStateOf<ReleaseInfo?>(null) }
    var downloadProgress by remember { mutableIntStateOf(0) }
    var currentVersion by remember { mutableStateOf("") }

    val apkDownloader = remember { ApkDownloader(context) }

    DisposableEffect(context) {
        val lifecycleOwner = context as? LifecycleOwner
        val observer = lifecycleOwner?.lifecycle?.let { lifecycle ->
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    scanned = false
                }
            }.also { lifecycle.addObserver(it) }
        }
        onDispose {
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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            releaseInfo?.matchedAsset?.let { asset ->
                startDownload(apkDownloader, asset.downloadUrl, context, activity) { progress ->
                    downloadProgress = progress
                }
                showDownloadDialog = true
                showUpdateDialog = false
            }
        } else {
            releaseInfo?.matchedAsset?.let { asset ->
                startSilentDownload(apkDownloader, asset.downloadUrl, context, activity)
                showUpdateDialog = false
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            currentVersion = packageInfo.versionName ?: "0.0.0"
        } catch (e: Exception) {
            currentVersion = "0.0.0"
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val result = withTimeoutOrNull(3_000) {
            withContext(Dispatchers.IO) {
                UpdateChecker.checkForUpdate()
            }
        }

        if (result != null && result.version != currentVersion) {
            releaseInfo = result
            showUpdateDialog = true
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
                    barcodeView.value?.resume()
                    onDispose {
                        barcodeView.value?.pause()
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

    if (showUpdateDialog && releaseInfo != null) {
        UpdateDialog(
            version = releaseInfo!!.version,
            onDismiss = { showUpdateDialog = false },
            onUpdate = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        releaseInfo?.matchedAsset?.let { asset ->
                            startDownload(apkDownloader, asset.downloadUrl, context, activity) { progress ->
                                downloadProgress = progress
                            }
                            showDownloadDialog = true
                            showUpdateDialog = false
                        }
                    } else {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    releaseInfo?.matchedAsset?.let { asset ->
                        startDownload(apkDownloader, asset.downloadUrl, context, activity) { progress ->
                            downloadProgress = progress
                        }
                        showDownloadDialog = true
                        showUpdateDialog = false
                    }
                }
            }
        )
    }

    if (showDownloadDialog) {
        DownloadDialog(
            fileName = releaseInfo?.matchedAsset?.name ?: "",
            progress = downloadProgress,
            onCancel = {
                apkDownloader.cancel()
                NotificationHelper.cancelProgressNotification(context)
                NotificationHelper.showCancelledNotification(context)
                showDownloadDialog = false
            }
        )
    }
}

private fun startDownload(
    downloader: ApkDownloader,
    url: String,
    context: android.content.Context,
    activity: ComponentActivity,
    onProgress: (Int) -> Unit
) {
    NotificationHelper.showProgressNotification(context, 0)

    kotlinx.coroutines.GlobalScope.launch {
        downloader.download(
            url = url,
            onProgress = { progress ->
                onProgress(progress)
                NotificationHelper.updateProgress(context, progress)
            },
            onComplete = { file ->
                NotificationHelper.cancelProgressNotification(context)
                installApk(context, activity, file)
            },
            onCancel = {
                NotificationHelper.cancelProgressNotification(context)
            }
        )
    }
}

private fun startSilentDownload(
    downloader: ApkDownloader,
    url: String,
    context: android.content.Context,
    activity: ComponentActivity
) {
    kotlinx.coroutines.GlobalScope.launch {
        downloader.download(
            url = url,
            onProgress = { },
            onComplete = { file ->
                installApk(context, activity, file)
            },
            onCancel = { }
        )
    }
}

private fun installApk(context: android.content.Context, activity: ComponentActivity, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        activity.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "安装失败: ${e.message}", Toast.LENGTH_LONG).show()
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
