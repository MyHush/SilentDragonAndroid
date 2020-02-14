// Copyright 2019-2020 The Hush developers
package org.myhush.silentdragon

import android.app.Application
import android.content.Context

class SilentDragonApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {

        var appContext: Context? = null
            private set
    }
}
