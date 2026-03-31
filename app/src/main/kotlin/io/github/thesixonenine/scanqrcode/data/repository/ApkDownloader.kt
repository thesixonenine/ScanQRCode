package io.github.thesixonenine.scanqrcode.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

private const val TAG = "ApkDownloader"

class ApkDownloader(private val context: Context) {
    @Volatile private var isCancelled = false
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun download(
        url: String,
        onProgress: (Int) -> Unit,
        onComplete: (File) -> Unit,
        onCancel: () -> Unit
    ) = withContext(Dispatchers.IO) {
        isCancelled = false
        
        val downloadDir = File(context.cacheDir, "downloads")
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val fileName = url.substringAfterLast("/")
        val outputFile = File(downloadDir, fileName)

        try {
            val request = Request.Builder()
                .url(url)
                .build()

            val call = client.newCall(request)

            call.execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "Download failed: ${response.code}")
                    withContext(Dispatchers.Main) {
                        onCancel()
                    }
                    return@withContext
                }

                val body = response.body ?: run {
                    withContext(Dispatchers.Main) {
                        onCancel()
                    }
                    return@withContext
                }

                val contentLength = body.contentLength()
                var bytesDownloaded = 0L

                body.byteStream().use { input ->
                    outputFile.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            if (isCancelled) {
                                outputFile.delete()
                                withContext(Dispatchers.Main) {
                                    onCancel()
                                }
                                return@withContext
                            }

                            output.write(buffer, 0, bytesRead)
                            bytesDownloaded += bytesRead

                            if (contentLength > 0) {
                                val progress = ((bytesDownloaded * 100) / contentLength).toInt()
                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    onComplete(outputFile)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download error", e)
            outputFile.delete()
            withContext(Dispatchers.Main) {
                onCancel()
            }
        }
    }

    fun cancel() {
        isCancelled = true
    }
}
