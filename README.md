# ScanQRCode

[![Build CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml)
[![Android CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml)


扫描并识别二维码


## Windows 构建流程

### PowerShell 命令行配置构建环境

#### 配置 ANDROID_HOME 并同意协议

```powershell
# https://developer.android.google.cn/tools/sdkmanager?hl=zh-cn
# 指定 ANDROID_HOME 目录并进入
# mkdir $env:USERPROFILE/Android/Sdk/cmdline-tools
# cd $env:USERPROFILE/Android/Sdk
# 默认 ANDROID_HOME 目录并进入
mkdir $env:LOCALAPPDATA/Android/Sdk/cmdline-tools
cd $env:LOCALAPPDATA/Android/Sdk

# 配置环境变量 ANDROID_HOME
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", (Get-Location).Path, "USER")

# 配置 cmdline-tools
cd cmdline-tools
Start-BitsTransfer -Source "https://googledownloads.cn/android/repository/commandlinetools-win-13114758_latest.zip" -Destination "commandlinetools.zip"
tar -xf commandlinetools.zip
Rename-Item ./cmdline-tools ./latest

# 配置 sdkmanager 工具到 PATH
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('ANDROID_HOME', 'User'), 'cmdline-tools\latest\bin'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('ANDROID_HOME', 'User'), 'platform-tools'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")
# 新开 PowerShell 窗口并同意协议
1..10 | ForEach-Object { echo "y" } | sdkmanager --licenses

# 检查版本
sdkmanager --version
```

**配置 Android Sdk 镜像地址(国内可选)**

```powershell
mkdir $env:USERPROFILE/.android
cd $env:USERPROFILE/.android
echo "proxy=http`nproxy_host=mirrors.cloud.tencent.com`nproxy_port=443`nno_https=false" > ./repositories.cfg
```

#### 配置 JAVA_HOME

```powershell
# 配置 JAVA_HOME
mkdir $env:USERPROFILE/Java
cd $env:USERPROFILE/Java
Start-BitsTransfer -Source "https://mirror.tuna.tsinghua.edu.cn/Adoptium/17/jdk/x64/windows/OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.zip" -Destination "jdk.zip"
tar -xf jdk.zip
Rename-Item ./jdk-17.0.15+6 ./17
cd 17
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", (Get-Location).Path, "USER")
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('JAVA_HOME', 'User'), 'bin'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")

# 检查版本
java -version
```

#### 拉取代码并构建 APK

```powershell
# 拉取代码并构建 APK
mkdir $env:USERPROFILE/github
cd $env:USERPROFILE/github
git clone git@github.com:thesixonenine/ScanQRCode.git
cd ScanQRCode
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${ENV:RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${ENV:RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${ENV:RELEASE_KEY_PASSWORD}
# 或者指定 JAVA_HOME
# -D org.gradle.java.home="C:\Program Files\Java\jdk-17"
```

### 详细配置

#### 设置环境变量

`JAVA_HOME` `ANDROID_HOME` `JAVA_TOOL_OPTIONS`

```bash
# 设置 JAVA 版本为 JDK17
JAVA_HOME="\path\to\jdk17"
# 设置 ANDROID_HOME 的环境变量
ANDROID_HOME="\path\to\sdkroot"
# 设置 JAVA 工具的环境变量(可选)
JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# 附:
# CMD 查看环境变量
echo %USERPROFILE%

# CMD 设置会话级环境变量
set JAVA_HOME="\path\to\jdk"
# CMD 设置用户级环境变量
setx JAVA_HOME "\path\to\jdk"
# CMD 设置系统级环境变量
setx JAVA_HOME "\path\to\jdk" /M

# PowerShell 查看环境变量
$env:JAVA_HOME
# 或者
[System.Environment]::GetEnvironmentVariable("JAVA_HOME")
[System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Process")


# PowerShell 设置会话级环境变量
$env:JAVA_HOME="\path\to\jdk"
# 或者
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "\path\to\jdk", "Process")
# PowerShell 设置用户级环境变量
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "\path\to\jdk", "User")
# PowerShell 设置会话级环境变量
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "\path\to\jdk", "Machine")
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
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${ENV:RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${ENV:RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${ENV:RELEASE_KEY_PASSWORD}
# bash
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${RELEASE_KEY_PASSWORD}
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
