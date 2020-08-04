// Copyright 2019-2020 The Hush developers
package org.myhush.silentdragon.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.lblVersionName
import org.myhush.silentdragon.BuildConfig
import org.myhush.silentdragon.R

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
