// Copyright 2019-2020 The Hush developers
package org.myhush.silentdragon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.lblVersionName

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        updateUI()
    }

    fun updateUI() {
        lblVersionName.text = BuildConfig.VERSION_NAME
    }
}
