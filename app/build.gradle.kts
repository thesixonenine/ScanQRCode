import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "xyz.thesixonenine.scanqrcode"
    compileSdk = 34
    defaultConfig {
        applicationId = "xyz.thesixonenine.scanqrcode"
        minSdk = 26
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            storeFile = file("./keystore/release.keystore")
            storePassword = property("RELEASE_STORE_PASSWORD").toString()
            keyAlias = property("RELEASE_KEY_ALIAS").toString()
            keyPassword = property("RELEASE_KEY_PASSWORD").toString()
        }
    }
    buildTypes {
        getByName("release") {
            // 加密
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            // 混淆规则配置
            // proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            applicationVariants.all {
                outputs.all {
                    if (this is ApkVariantOutputImpl) {
                        // ScanQRCode-debug.apk
                        // ScanQRCode-release.apk
                        this.outputFileName = "${project.parent?.name}-${this.name}.apk"
                    }
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        disable += "TypographyFractions"
        disable += "TypographyQuotes"
        abortOnError = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    testImplementation("junit:junit:4.13.2")
}
