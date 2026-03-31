package io.github.thesixonenine.scanqrcode.data.model

import android.os.Build

data class ReleaseInfo(
    val tagName: String,
    val version: String,
    val assets: List<Asset>
) {
    val matchedAsset: Asset?
        get() {
            val currentAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: return assets.find { it.name.contains("universal") }
            return when {
                currentAbi.contains("arm64-v8a") -> assets.find { it.name.contains("arm64-v8a") }
                currentAbi.contains("armeabi-v7a") -> assets.find { it.name.contains("armeabi-v7a") }
                currentAbi.contains("x86_64") -> assets.find { it.name.contains("x86_64") }
                currentAbi.contains("x86") -> assets.find { it.name.contains("-x86.apk") && !it.name.contains("x86_64") }
                else -> assets.find { it.name.contains("universal") }
            }
        }
}

data class Asset(
    val name: String,
    val downloadUrl: String
)
