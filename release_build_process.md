# Release Build Process

The first time you create a release build you'll need to create a keystore file and prepare a properties file. The
release keystore is used for app signing and a properties file is used to store
sensitive information about the keystore. **These files should not be committed
to git.** Once you have both of these files you can create a release build for
the Google Play Store.

## Create a release keystore

### Creating a release keystore via CLI

The `keytool` command can be used, for example:

    keytool -genkey -alias silentdragon -keyalg RSA -keystore new.jks -dname "CN=Duke Leto, O=Hush" -storepass testing -keypass 123 -validity XXX

### Creating a release keystore via GUI

* With Android Studio IDE open, on the system bar click Build -> Generate Signed Bundle/APK
* Select the APK option instead of the Bundle option
* On the next screen select app as the module and click "Create new"
* Set the Key Store Name to `silent_dragon_keystore.jks` and the path to that of the project, create a password for the keystore path, a Key alias, and a key password. The store password and key password should be the same. Fill out some basic organization information and click Ok.
* On the next screen make sure the build variant "release" is selected and click Finish.

## Preparing a properties file

Copy `secrets.properties` file from `examples` folder and paste it to the projects main directory.
Fill store_file_location, key_alias, key_password and store_password when you created the release keystore.

## Building a release APK for Google Play

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

## Building a release APK for F-Droid

This will be pursued once lite wallet functionality exists in the SilentDragonAndroid wallet.

## Contributing

Contributions to this project are welcome and encouraged.

## License

This project is under the GNU Public License v3. For the full license, see [LICENSE](LICENSE).

