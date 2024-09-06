pluginManagement {
    repositories {
        maven { url = uri("https://mirrors.tencent.com/nexus/repository/gradle-plugins") }
        gradlePluginPortal()
    }
}
rootProject.name = "ScanQRCode"
include("app")
