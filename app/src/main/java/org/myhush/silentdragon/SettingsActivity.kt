package org.myhush.silentdragon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        updateUI()

        btnDisconnect.setOnClickListener {
            DataModel.setConnString(null, applicationContext)
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
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {}

        }

    }

    private fun fillSpinner(){
        var items = listOf("USD", "EUR", "BTC")
        var adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerCurrency.adapter = adapter
    }

    fun updateUI() {
        fillSpinner()
        txtSettingsConnString.text = DataModel.getConnString(SilentDragonApp.appContext!!)
            ?: "Not Connected"

        chkDisallowInternet.isChecked = !DataModel.getGlobalAllowInternet()

        lblVersionName.text = BuildConfig.VERSION_NAME
        lblServerVersion.text = DataModel.mainResponseData?.serverversion ?: "Not Connected"
    }
}
