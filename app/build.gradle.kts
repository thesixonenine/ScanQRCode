import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "xyz.thesixonenine.scanqrcode"
    compileSdk = 36
    defaultConfig {
        applicationId = "xyz.thesixonenine.scanqrcode"
        minSdk = 22
        versionCode = 1
        versionName = "0.0.3"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = true
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("${System.getProperty("user.home")}/.android/release.keystore")
            storePassword = System.getenv("RELEASE_STORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
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
                val variant = this
                outputs.all {
                    if (this is ApkVariantOutputImpl) {
                        outputFileName = outputFileName.replaceFirst(project.name, project.parent?.name + "-" + variant.versionName, false)
                    }
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    lint {
        disable += "TypographyFractions"
        disable += "TypographyQuotes"
        abortOnError = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    testImplementation("junit:junit:4.13.2")
}
