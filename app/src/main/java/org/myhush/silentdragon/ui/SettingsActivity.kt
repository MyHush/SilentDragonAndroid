// Copyright 2019-2020 The Hush developers
package org.myhush.silentdragon.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_settings.*
import org.myhush.silentdragon.ConnectionManager
import org.myhush.silentdragon.DataModel
import org.myhush.silentdragon.R
import org.myhush.silentdragon.SilentDragonApp

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        updateUI()

        btnDisconnect.setOnClickListener {
            DataModel.setConnString(
                null,
                applicationContext
            )
            DataModel.clear()
            ConnectionManager.closeConnection()

            updateUI()
        }

        chkDisallowInternet.setOnClickListener {
            DataModel.setGlobalAllowInternet(!chkDisallowInternet.isChecked)

            if (chkDisallowInternet.isChecked) {
                ConnectionManager.closeConnection()
            }

            updateUI()
        }

        spinnerCurrency!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                var cur = parent.adapter.getItem(pos).toString() // Set selected currency

                DataModel.selectedCurrency = cur // Set cur as selected

                // Save currency
                var pref: SharedPreferences = getSharedPreferences("MainFile",0)

                var editor: SharedPreferences.Editor = pref.edit()
                editor.putString("currency",
                    DataModel.selectedCurrency
                )

                editor.commit()
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {}

        }

    }

    private fun fillSpinner(){
        val items = DataModel.currencyValues.keys.toMutableList()
        var selectedIndex = 0
        items.sort()

        for (i in items){
            if(i == DataModel.selectedCurrency)
                break
            selectedIndex++
        }


        var adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerCurrency.adapter = adapter
        spinnerCurrency.setSelection(selectedIndex)
    }

    fun updateUI() {
        fillSpinner()
        txtSettingsConnString.text = DataModel.getConnString(
            SilentDragonApp.appContext!!
        )
            ?: getString(R.string.not_connected)

        chkDisallowInternet.isChecked = !DataModel.getGlobalAllowInternet()

        lblServerVersion.text = DataModel.mainResponseData?.serverversion ?: getString(
            R.string.not_connected
        )
    }
}
