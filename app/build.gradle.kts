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
        minSdk =26
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
        disable += "TypographyFractions" + "TypographyQuotes"
        abortOnError = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support.constraint:constraint-layout:2.0.4")
    implementation("com.journeyapps:zxing-android-embedded:3.6.0")
    implementation("com.google.zxing:core:3.5.3")
    testImplementation("junit:junit:4.13.2")
}
