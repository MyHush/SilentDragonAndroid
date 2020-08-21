# SilentDragon Android
[![GitHub license](https://img.shields.io/badge/license-GPLv3-green)](https://github.com/MyHush/SilentDragonAndroid/blob/master/LICENSE)

SilentDragon Android is an Android frontend for the desktop SilentDragon that lets you send and receive shielded payments from your mobile phone.

<img height=50% width=50% src="https://raw.githubusercontent.com/MyHush/SilentDragonAndroid/master/SDA.jpg">

## Installation

The minimum supported Android version is 4.4.x KitKat. There are two ways to install:

1. Via the [Google Play Store](https://play.google.com/store/apps/details?id=org.myhush.silentdragon).
1. Direct APK install available at the [Releases page](https://github.com/MyHush/SilentDragonAndroid/releases). You will need to allow `Install from untrusted sources` on your Android phone in order to install from this source.

## Running SilentDragon

In order to let your Android phone connect to your desktop, you need to run the desktop [SilentDragon](https://github.com/MyHush/SilentDragon), and sync fully. This is not a full node
on your Android (your poor battery!). It's a remote control for your full node.

Thankfully this should only take a short time with a fast internet connection!
As the Hush network grows, it will take longer. As of Sept 2019, the blockchain
is about 900MB on disk.

After your node is synced, go to `Apps -> Connect Mobile App` to view the
connection QR Code, which you can scan from the Android App.

### Bugs???

You can file issues in the [issues tab](https://github.com/MyHush/SilentDragonAndroid/issues).

We appreciate them! Please follow the Github issue template, when reasonable.

## Compiling from source

You can also compile and run from source.

On OS X:

    brew doctor
    brew install ant
    brew install maven
    brew install gradle
    brew cask install android-sdk
    brew cask install android-ndk

    touch ~/.android/repositories.cfg
    sdkmanager --update
    sdkmanager "platform-tools" "platforms;android-28"
    gradle build

On Debian-based systems:

    apt-get install -y android-sdk gradle
    touch ~/.android/repositories.cfg
    sdkmanager --update
    sdkmanager "platform-tools" "platforms;android-28"
    gradle build

Make sure you have Gradle 5.4.x or higher, 5.4.1 is known to work:

    ./gradlew wrapper --gradle-version=5.4.1

Or you can use Android Studio on Linux, OS X, or Windows:

    Make sure to install ndkVersion 21.1.6352462.
    Clone the repository and open the project in Android Studio.
    Android Studio will automatically run the initial build process.
    Click the Run button to launch the app after the build process is complete.

## Release Build Process

The first time you create a release build you'll need to create a keystore file and prepare a properties file. The
release keystore is used for app signing and a properties file is used to store
sensitive information about the keystore. These files should not be committed
to git. Once you have both of these files you can create a release build for
the Google Play Store.

### Creating a release keystore via CLI

The `keytool` command can be used, for example:

    keytool -genkey -alias silentdragon -keyalg RSA -keystore new.jks -dname "CN=Duke Leto, O=Hush" -storepass testing -keypass 123 -validity XXX

### Creating a release keystore via GUI

* With Android Studio IDE open, on the system bar click Build -> Generate Signed Bundle/APK
* Select the APK option instead of the Bundle option
* On the next screen select app as the module and click "Create new"
* Set the Key Store Name to `silent_dragon_keystore.jks` and the path to that of the project, create a password for the keystore path, a Key alias, and a key password. The store password and key password should be the same. Fill out some basic organization information and click Ok.
* On the next screen make sure the build variant "release" is selected and click Finish.

### Preparing a properties file

Copy `secrets.properties` file from `examples` folder and paste it to the projects main directory.
Fill store_file_location, key_alias, key_password and store_password when you created the release keystore.

### Building a release APK for Google Play

Before creating each build you should increment the version code & version name
in the build.gradle file. These must be incremented for each release otherwise
the Play Store will reject the build.

To create a release build navigate to the project directory in terminal and run

```
    ./new_binary.sh 1.2.3
```

where 1.2.3 is the version number, which must match the codebase to be accepted to Google Play.

This will produce an apk file in the following directory.

    SilentDragonAndroid/app/build/output/apk/release/app-release.apk

and also copy it to the current directory with the filename SilentDragonAndroid-1.2.3.apk

This build can be directly uploaded to Google Play.

### Building a release APK for F-Droid

This will be pursued once lite wallet functionality exists in the SilentDragonAndroid wallet.

## Contributing

Contributions to this project are welcome and encouraged.

## License

This project is under the GNU Public License v3. For the full license, see [LICENSE](LICENSE).

