# SilentDragon Android
[![GitHub license](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://github.com/MyHush/SilentDragonAndroid/blob/master/LICENSE)
[![GitHub version](https://badge.fury.io/gh/MyHush%2FSilentDragonAndroid.svg)](https://badge.fury.io/gh/MyHush%2FSilentDragonAndroid)
[![Github All Releases](https://img.shields.io/github/downloads/MyHush/SilentDragonAndroid/total.svg)](https://img.shields.io/github/downloads/MyHush/SilentDragonAndroid/total.svg)

<p align="left">
    <a href="https://twitter.com/MyHushTeam">
        <img src="https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Ftwitter.com%2Fmyhushteam"
            alt="MyHushTeam's Twitter"></a>
    <a href="https://twitter.com/intent/follow?screen_name=MyHushTeam">
        <img src="https://img.shields.io/twitter/follow/MyHushTeam?style=social&logo=twitter"
            alt="follow on Twitter"></a>
    <a href="https://fosstodon.org/@myhushteam">
        <img src="https://img.shields.io/badge/Mastodon-MyHushTeam-blue"
            alt="follow on Mastodon"></a>
    <a href="https://www.reddit.com/r/Myhush/">
        <img src="https://img.shields.io/reddit/subreddit-subscribers/Myhush?style=social"
            alt="MyHushTeam's Reddit"></a>
</p>

SilentDragon Android is an Android frontend for the desktop [SilentDragon](https://github.com/MyHush/SilentDragon) or [SilentDragonLite](https://github.com/MyHush/SilentDragonLite) that lets you send and receive shielded payments from your mobile phone. We are currently working on implementing Lite Wallet and HushChat functionality on Android, so follow us on our [Twitter](https://twitter.com/MyHushTeam) or [Mastodon](https://fosstodon.org/@myhushteam) to stay updated. After this functionality is implemented, we are planning on making an F-Droid release.

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
sensitive information about the keystore. **These files should not be committed
to git.** Once you have both of these files you can create a release build for
the Google Play Store. For further information, [click here](release_build_process.md).

## Contributing

Contributions to this project are welcome and encouraged.

## Support

For support or other questions, join us on [Discord](https://myhush.org/discord), or tweet at [@MyHushTeam](https://twitter.com/MyHushTeam), or toot at our [Mastodon](https://fosstodon.org/@myhushteam), or join [Telegram](http://myhush.org/telegram) or [file an issue](https://github.com/MyHush/SilentDragonAndroid/issues).

