# ScanQRCode

[![Build CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml)
[![Android CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml)


Scanning and recognizing QR code, Barcode.


## Windows Build

### PowerShell Configure Environment

#### Configure ANDROID_HOME

```powershell
# https://developer.android.google.cn/tools/sdkmanager?hl=zh-cn
# use special ANDROID_HOME
# mkdir $env:USERPROFILE/Android/Sdk/cmdline-tools
# cd $env:USERPROFILE/Android/Sdk

# use default ANDROID_HOME
mkdir $env:LOCALAPPDATA/Android/Sdk/cmdline-tools
cd $env:LOCALAPPDATA/Android/Sdk

# configure ANDROID_HOME
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", (Get-Location).Path, "USER")

# configure cmdline-tools
cd cmdline-tools
Start-BitsTransfer -Source "https://googledownloads.cn/android/repository/commandlinetools-win-13114758_latest.zip" -Destination "commandlinetools.zip"
tar -xf commandlinetools.zip
Rename-Item ./cmdline-tools ./latest

# add sdkmanager to PATH
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('ANDROID_HOME', 'User'), 'cmdline-tools\latest\bin'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('ANDROID_HOME', 'User'), 'platform-tools'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")
# open new PowerShell window and accept licenses
1..10 | ForEach-Object { echo "y" } | sdkmanager --licenses

# check sdkmanager version
sdkmanager --version
```

**Configure Android Sdk Mirror(for China)**

```powershell
mkdir $env:USERPROFILE/.android
cd $env:USERPROFILE/.android
echo "proxy=http`nproxy_host=mirrors.cloud.tencent.com`nproxy_port=443`nno_https=false" > ./repositories.cfg
```

#### Configure JAVA_HOME

```powershell
# configure JAVA_HOME
mkdir $env:USERPROFILE/Java
cd $env:USERPROFILE/Java
Start-BitsTransfer -Source "https://mirror.tuna.tsinghua.edu.cn/Adoptium/17/jdk/x64/windows/OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.zip" -Destination "jdk.zip"
tar -xf jdk.zip
Rename-Item ./jdk-17.0.15+6 ./17
cd 17
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", (Get-Location).Path, "USER")
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('JAVA_HOME', 'User'), 'bin'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")

# check java version
java -version
```

#### Build APK

```powershell
# pull and build APK
mkdir $env:USERPROFILE/github
cd $env:USERPROFILE/github
git clone git@github.com:thesixonenine/ScanQRCode.git
cd ScanQRCode
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${ENV:RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${ENV:RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${ENV:RELEASE_KEY_PASSWORD}
# use special JAVA_HOME
# -D org.gradle.java.home="C:\Program Files\Java\jdk-17"
```

### Configure

#### Configure Environment

`JAVA_HOME` `ANDROID_HOME` `JAVA_TOOL_OPTIONS`

[Configure Environment References](https://blog.thesixonenine.site/p/windows/#configure-environment)

```bash
# configure JAVA_HOME
JAVA_HOME="\path\to\jdk17"
# configure ANDROID_HOME
ANDROID_HOME="\path\to\sdkroot"
# Optional
JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
```

#### Install Android cmdline-tools And Android SDK

Install cmdline-tools

```bash
scoop install main/android-clt
```

Install Android SDK

```bash
sdkmanager --sdk_root="\path\to\sdkroot" "platforms;android-28"
```

#### Configure Environment

- If you have not set the `ANDROID_HOME` environment variable, add `sdk.dir=\path\to\sdkroot` to the `local.properties` file.
- If you have not set the `JAVA_HOME` environment variable, add `org.gradle.java.home=\path\to\jdk17` to the `gradle.properties` file.

#### Upgrade AGP And Gradle

[Correspond Versions](https://developer.android.com/build/releases/gradle-plugin#updating-gradle)

#### Build And Sign APK

```bash
# powershell
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${ENV:RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${ENV:RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${ENV:RELEASE_KEY_PASSWORD}
# bash
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${RELEASE_KEY_PASSWORD}
```

#### Sign APK

```bash
jarsigner -verbose -keystore \path\to\keystore\release.keystore -signedjar .\app\build\outputs\apk\release\app-release-signed.apk .\app\build\outputs\apk\release\app-release-unsigned.apk keystorealias
```

#### View Keystore

```bash
keytool -list -v -keystore .\app\keystore\release.keystore
```

#### View Action Logs

```bash
gh api -H "Accept: application/vnd.github+json" -H "X-GitHub-Api-Version: 2022-11-28" /repos/thesixonenine/ScanQRCode/actions/runs --paginate --jq '.workflow_runs[] | select(.conclusion != "") | .id'
```
