## config WSL2 and build

```bash
wsl.exe --install Ubuntu --name Ubuntu2 --location D:\WSL\Ubuntu2 --web-download
#可能需要在设置(win+I)中的网络里设置使用代理服务器,并设置releases.ubuntu.com为直连
wsl.exe -d Ubuntu2
0.sudo vim /etc/wsl.conf appendWindowsPath=false
1.configure-mirror
2.configure-proxy vim ~/.bashrc
new shell
3.zsh
  sudo apt install -y build-essential curl git sudo wget file unzip software-properties-common
  sudo apt install -y zsh && chsh -s /bin/zsh && \
    git clone --depth=1 https://mirror.nju.edu.cn/git/ohmyzsh.git ~/.oh-my-zsh && \
    cp ~/.oh-my-zsh/templates/zshrc.zsh-template ~/.zshrc
new shell
  git clone --depth=1 https://github.com/zsh-users/zsh-autosuggestions ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-autosuggestions && \
    git clone --depth=1 https://github.com/zsh-users/zsh-syntax-highlighting.git ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-syntax-highlighting && \
    sed -i 's/^plugins=(git)/plugins=(\ngit\n)/' ~/.zshrc && \
    sed -i 's/^plugins=(/&\nzsh-syntax-highlighting/' ~/.zshrc && \
    sed -i 's/^plugins=(/&\nzsh-autosuggestions/' ~/.zshrc && \
    source ~/.zshrc
4.ssh
  mkdir -p ~/.ssh
MOVE TO .ssh
chmod 700 ~/.ssh && \
chmod 600 ~/.ssh/id_ed25519 && \
chmod 644 ~/.ssh/id_ed25519.pub && \
chmod 644 ~/.ssh/allowed_signers

5.jdk
mkdir -p ~/.local/share/jdk
TAR_NAME="OpenJDK21U-jdk_x64_linux_hotspot_21.0.13_9.tar.gz"
curl -fsLO https://mirrors.tuna.tsinghua.edu.cn/Adoptium/21/jdk/x64/linux/$TAR_NAME
tar -zxf $TAR_NAME
mv ./jdkxxx/* ~/.local/share/jdk/
APPEND TO ZSHRC
export JAVA_HOME="$HOME/.local/share/jdk"
[ -d "$JAVA_HOME/bin" ] && [[ ":$PATH:" != *":$JAVA_HOME/bin:"* ]] && export PATH="$PATH:$JAVA_HOME/bin"

5.ANDROID_HOME
mkdir -p ~/android/sdk/cmdline-tools/latest
cd /tmp
wget https://dl.google.com/android/repository/commandlinetools-linux-15859902_latest.zip
unzip -q commandlinetools-linux-15859902_latest.zip -d /tmp/android-cmdline-tools
mv /tmp/android-cmdline-tools/cmdline-tools/* ~/android/sdk/cmdline-tools/latest/
APPEND TO ZSHRC
export ANDROID_HOME="$HOME/android/sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

yes | sdkmanager --sdk_root="$ANDROID_HOME" --licenses

6.Clone And Build
mkdir -p ~/.android

Prepare ~/.android/release.keystore.txt
cat ~/.android/release.keystore.txt | base64 --decode > ~/.android/release.keystore

Prepare Password To ~/.zshrc
export RELEASE_STORE_PASSWORD=
export RELEASE_KEY_ALIAS=
export RELEASE_KEY_PASSWORD=

git clone git@github.com:thesixonenine/ScanQRCode.git
cd ScanQRCode
./gradlew clean assembleRelease
```
