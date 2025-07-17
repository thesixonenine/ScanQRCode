# ScanQRCode

[![Build CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/build.yml)
[![Android CI](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml/badge.svg?branch=master)](https://github.com/thesixonenine/ScanQRCode/actions/workflows/android.yml)


Scanning and recognizing QR code, Barcode.


## Build with PowerShell on Windows

### Environment Variables

[Configure Environment References](https://blog.thesixonenine.site/p/windows/#configure-environment)

- ANDROID_HOME
- JAVA_HOME

### ANDROID_HOME

#### Manual

```powershell
# https://developer.android.com/tools/sdkmanager
# use special ANDROID_HOME
# mkdir $env:USERPROFILE/Android/Sdk/cmdline-tools
# cd $env:USERPROFILE/Android/Sdk

# use default ANDROID_HOME
mkdir $env:LOCALAPPDATA/Android/Sdk/cmdline-tools
cd $env:LOCALAPPDATA/Android/Sdk

# configure ANDROID_HOME
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", (Get-Location).Path, "USER")

# https://developer.android.com/studio#command-line-tools-only
# configure cmdline-tools
cd cmdline-tools
Start-BitsTransfer -Source "https://dl.google.com/android/repository/commandlinetools-win-13114758_latest.zip" -Destination "commandlinetools.zip"
# for china
# Start-BitsTransfer -Source "https://googledownloads.cn/android/repository/commandlinetools-win-13114758_latest.zip" -Destination "commandlinetools.zip"
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

#### Scoop

```bash
scoop install main/android-clt
sdkmanager --sdk_root="/path/to/sdkroot" "platforms;android-28"
```

#### Android Sdk Mirror(for China)

```powershell
mkdir $env:USERPROFILE/.android
cd $env:USERPROFILE/.android
echo "proxy=http`nproxy_host=mirrors.cloud.tencent.com`nproxy_port=443`nno_https=false" > ./repositories.cfg
```

### JAVA_HOME

```powershell
# configure JAVA_HOME
mkdir $env:USERPROFILE/Java
cd $env:USERPROFILE/Java
Start-BitsTransfer -Source "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.15%2B6/OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.zip" -Destination "jdk.zip"
# for china
# https://mirror.tuna.tsinghua.edu.cn/Adoptium/17/jdk/x64/windows/OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.zip
tar -xf jdk.zip
Rename-Item ./jdk-17.0.15+6 ./17
cd 17
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", (Get-Location).Path, "USER")
[System.Environment]::SetEnvironmentVariable("PATH", "$([System.IO.Path]::Combine([System.Environment]::GetEnvironmentVariable('JAVA_HOME', 'User'), 'bin'));$([System.Environment]::GetEnvironmentVariable('PATH', 'User'))", "User")

# check java version
java -version
```

#### use properties file replace environment variable

> - If you have not set the `ANDROID_HOME` environment variable, add `sdk.dir=/path/to/sdkroot` to the `local.properties` file.
> - If you have not set the `JAVA_HOME` environment variable, add `org.gradle.java.home=/path/to/jdk17` to the `gradle.properties` file.


### Build APK

```powershell
# pull and build APK
mkdir $env:USERPROFILE/github
cd $env:USERPROFILE/github
git clone git@github.com:thesixonenine/ScanQRCode.git
cd ScanQRCode
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=$env:RELEASE_KEY_PASSWORD -P RELEASE_KEY_ALIAS=$env:RELEASE_KEY_ALIAS -P RELEASE_KEY_PASSWORD=$env:RELEASE_KEY_PASSWORD
# use special JAVA_HOME
# -D org.gradle.java.home="C:\Program Files\Java\jdk-17"

# bash
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=${RELEASE_KEY_PASSWORD} -P RELEASE_KEY_ALIAS=${RELEASE_KEY_ALIAS} -P RELEASE_KEY_PASSWORD=${RELEASE_KEY_PASSWORD}
# cmd
./gradlew assembleRelease -P RELEASE_STORE_PASSWORD=%RELEASE_KEY_PASSWORD% -P RELEASE_KEY_ALIAS=%RELEASE_KEY_ALIAS% -P RELEASE_KEY_PASSWORD=%RELEASE_KEY_PASSWORD%
```

## Upgrade AGP And Gradle

[Correspond Versions](https://developer.android.com/build/releases/gradle-plugin#updating-gradle)


## Other Command

```bash
# Manual Sign APK
jarsigner -verbose -keystore /path/to/keystore/release.keystore -signedjar ./app/build/outputs/apk/release/app-release-signed.apk ./app/build/outputs/apk/release/app-release-unsigned.apk keystorealias

# View Keystore
keytool -list -v -keystore ./app/keystore/release.keystore

# View Action Logs
gh api -H "Accept: application/vnd.github+json" -H "X-GitHub-Api-Version: 2022-11-28" /repos/thesixonenine/ScanQRCode/actions/runs --paginate --jq '.workflow_runs[] | select(.conclusion != "") | .id'
```
