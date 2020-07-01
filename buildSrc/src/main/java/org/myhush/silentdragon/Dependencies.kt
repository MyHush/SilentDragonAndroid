package org.myhush.silentdragon

object Deps {
    const val kotlinVersion = "1.3.72"

    const val compileSdkVersion = 29
    const val buildToolsVersion = "29.0.3"
    const val minSdkVersion = 17
    const val targetSdkVersion = 29

    object Kotlin : Version(kotlinVersion) {
        val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
        object Coroutines : Version("1.3.2") {
            val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            val CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        }
    }
    object AndroidX {
        //const val ANNOTATION = "androidx.annotation:annotation:1.0.0"
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.0.0"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val CORE_KTX = "androidx.core:core-ktx:1.3.0"
        const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:1.1.0-beta01"
        const val LEGACY = "androidx.legacy:legacy-support-v4:1.0.0"
        const val MULTIDEX = "androidx.multidex:multidex:2.0.1"
        const val PAGING = "androidx.paging:paging-runtime-ktx:2.1.2"
        const val VECTOR_DRAWABLE = "androidx.vectordrawable:vectordrawable:1.0.0"

        // for camera function
        object CameraX : Version("1.0.0-beta04") {
            val CAMERA2 = "androidx.camera:camera-camera2:1.0.0-beta04"
            val CORE = "androidx.camera:camera-core:1.0.0-beta04"
        }

        object Navigation : Version("2.1.0") {
            val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$version"
            val UI_KTX = "androidx.navigation:navigation-ui-ktx:$version"
        }
        object Room : Version("2.2.5") {
            val ROOM_COMPILER = "androidx.room:room-compiler:$version"
            val ROOM_KTX = "androidx.room:room-ktx:$version"
        }
    }
    // from Demo App SDK
    object Hush {
        val ANDROID_WALLET_PLUGINS = "cash.z.ecc.android:zcash-android-wallet-plugins:1.0.0"
        val KOTLIN_BIP39 = "cash.z.ecc.android:kotlin-bip39:1.0.0-beta09"
        object Sdk : Version("1.1.0-beta02") {
            val MAINNET = "cash.z.ecc.android:sdk-mainnet:$version"
            //val TESTNET = "cash.z.ecc.android:sdk-testnet:$version"
        }
    }
    object Grpc : Version("1.25.0") {
        val ANDROID = "io.grpc:grpc-android:$version"
        val OKHTTP = "io.grpc:grpc-okhttp:$version"
        val PROTOBUG ="io.grpc:grpc-protobuf-lite:$version"
        val STUB = "io.grpc:grpc-stub:$version"
    }
}

open class Version(@JvmField val version: String)
