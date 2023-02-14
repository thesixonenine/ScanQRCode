# ScanQRCode
扫描并识别二维码


## Windows构建流程


#### 设置环境变量

```bash
# 设置JAVA版本为JDK11
JAVA_HOME=\path\to\jdk11
# 设置Android SDK的环境变量
ANDROID_SDK_ROOT=\path\to\sdkroot
# 设置JAVA工具的环境变量
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
```

#### 安装android命令行工具与AndroidSDK

```bash
# 安装命令行工具
scoop install main/android-clt
# 安装sdk
# 如果未设置sdk的环境变量, 则需要加上参数 `--sdk_root="\path\to\sdkroot"`
sdkmanager "platforms;android-28"
```

#### 环境指定

. 如果未设置Android SDK的环境变量, 则需要新增 `local.properties` 文件, 在文件中指定 `sdk.dir=\path\to\sdkroot`
. 如果未设置JAVA版本为JDK11, 则修改 `gradle.properties` 文件, 在文件中指定 `org.gradle.java.home`


#### 打包APK

```bash
./gradlew assembleRelease
jarsigner.exe -verbose -keystore \path\to\keystore\release.keystore -signedjar .\app\build\outputs\apk\release\app-release-signed.apk .\app\build\outputs\apk\release\app-release-unsigned.apk keystorealias
```
