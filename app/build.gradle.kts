plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.thesixonenine.scanqrcode"
    compileSdk = 36
    defaultConfig {
        applicationId = "io.github.thesixonenine.scanqrcode"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            // proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    lint {
        disable += "TypographyFractions"
        disable += "TypographyQuotes"
        abortOnError = false
    }
}

base.archivesName.set("${rootProject.name}-${android.defaultConfig.versionName}")

abstract class RenameApkTask : DefaultTask() {
    @get:InputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun execute() {
        outputDir.get().asFile.listFiles()?.filter { it.extension == "apk" }?.forEach { apk ->
            val newName = apk.name.replace("-release.apk", ".apk")
            if (newName != apk.name) {
                apk.renameTo(File(apk.parentFile, newName))
            }
        }
    }
}

val renameApk = tasks.register<RenameApkTask>("renameReleaseApk") {
    outputDir.set(layout.buildDirectory.dir("outputs/apk/release"))
}

tasks.configureEach {
    if (name.startsWith("assemble") && name.endsWith("Release")) {
        finalizedBy(renameApk)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)
    implementation(libs.okhttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
