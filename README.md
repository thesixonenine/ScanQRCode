# ScanQRCode

[![Build CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml)
[![Android CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml)


扫描并识别二维码


## Windows 构建流程


#### 设置环境变量

`JAVA_HOME` `ANDROID_SDK_ROOT` `JAVA_TOOL_OPTIONS`

```bash
# 设置 JAVA 版本为 JDK17
JAVA_HOME=\path\to\jdk17
# 设置 Android SDK 的环境变量
ANDROID_SDK_ROOT=\path\to\sdkroot
# 设置 JAVA 工具的环境变量
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
```

#### 安装 Android 命令行工具与 Android SDK

安装命令行工具

```bash
scoop install main/android-clt
```

安装 Android SDK

```bash
sdkmanager --sdk_root="\path\to\sdkroot" "platforms;android-28"
```

#### 环境指定

- 如果未设置Android SDK的环境变量, 则需要新增 `local.properties` 文件, 在文件中指定 `sdk.dir=\path\to\sdkroot`
- 如果未设置JAVA版本为JDK17, 则修改 `gradle.properties` 文件, 在文件中指定 `org.gradle.java.home=\path\to\jdk17`

#### 升级 AGP 和 Gradle

[版本对应](https://developer.android.com/build/releases/gradle-plugin#updating-gradle)

#### 打包并签名 APK

```bash
# powershell
gradle assembleRelease -P RELEASE_STORE_PASSWORD=${ENV:RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${ENV:RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${ENV:RELEASE_KEY_PASSWORD}
# bash
gradle assembleRelease -P RELEASE_STORE_PASSWORD=${RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${RELEASE_KEY_PASSWORD}
```

#### 手动签名 APK

```bash
jarsigner -verbose -keystore \path\to\keystore\release.keystore -signedjar .\app\build\outputs\apk\release\app-release-signed.apk .\app\build\outputs\apk\release\app-release-unsigned.apk keystorealias
```

#### 查看 keystore

```bash
keytool -list -v -keystore .\app\keystore\release.keystore
```

#### 查看 Action 运行日志

```bash
gh api -H "Accept: application/vnd.github+json" -H "X-GitHub-Api-Version: 2022-11-28" /repos/thesixonenine/ScanQRCode/actions/runs --paginate --jq '.workflow_runs[] | select(.conclusion != "") | .id'
```
