package io.github.thesixonenine.scanqrcode.data.repository

import android.util.Log
import io.github.thesixonenine.scanqrcode.data.model.Asset
import io.github.thesixonenine.scanqrcode.data.model.ReleaseInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

private const val TAG = "UpdateChecker"
private const val GITHUB_API_URL = "https://api.github.com/repos/thesixonenine/ScanQRCode/releases/latest"

object UpdateChecker {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    fun checkForUpdate(): ReleaseInfo? {
        return try {
            val request = Request.Builder()
                .url(GITHUB_API_URL)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "Unexpected response: ${response.code}")
                    return null
                }

                val body = response.body?.string() ?: return null
                parseReleaseInfo(body)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check for update", e)
            null
        }
    }

    private fun parseReleaseInfo(json: String): ReleaseInfo? {
        return try {
            val tagName = extractJsonValue(json, "\"tag_name\"") ?: return null
            val version = tagName.removePrefix("v")
            val assets = extractAssets(json)

            ReleaseInfo(
                tagName = tagName,
                version = version,
                assets = assets
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse release info", e)
            null
        }
    }

    private fun extractAssets(json: String): List<Asset> {
        val assets = mutableListOf<Asset>()
        val assetsRegex = """\"name\":\s*"([^"]+\.apk)"[^}]*\"browser_download_url\":\s*"([^"]+)"""".toRegex()
        
        assetsRegex.findAll(json).forEach { match ->
            val (name, url) = match.destructured
            assets.add(Asset(name = name, downloadUrl = url))
        }
        
        return assets
    }

    private fun extractJsonValue(json: String, key: String): String? {
        val regex = """$key:\s*"([^"]*)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1)
    }
}
