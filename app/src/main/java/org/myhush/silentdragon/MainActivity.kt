// Copyright 2019-2020 The Hush developers
// Released under the GPLv3
package org.myhush.silentdragon

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.beust.klaxon.Klaxon
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.client.android.Intents
import com.google.zxing.client.android.Intents.Scan.QR_CODE_MODE
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_qr_reader.*
import kotlinx.android.synthetic.main.content_main.*
import org.myhush.silentdragon.DataModel.ConnectionStatus
import org.myhush.silentdragon.DataModel.connStatus
import org.myhush.silentdragon.ui.AboutActivity
import org.myhush.silentdragon.ui.SettingsActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(),
    TransactionItemFragment.OnFragmentInteractionListener,
    UnconfirmedTxItemFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // IntentIntegrator is part of zxing-android-embedded to read QR codes
    private lateinit var intentIntegrator: IntentIntegrator

    override fun onCreate(savedInstanceState: Bundle?) {
        //StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()) // TESTING

        super.onCreate(savedInstanceState)

        title = getString(R.string.app_name)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // When creating, clear all the data first
        setMainStatus("")
        DataModel.init()

        intentIntegrator = IntentIntegrator(this)

        btnConnect.setOnClickListener {
            //startActivity(Intent(this@MainActivity, QrReaderActivity::class.java))
            run {
                intentIntegrator.setDesiredBarcodeFormats(QR_CODE_MODE)
                intentIntegrator.setCameraId(0)     // set to back camera
                intentIntegrator.setBeepEnabled(true)
                intentIntegrator.setOrientationLocked(false)    // trying to force portrait here, but it's not working
                intentIntegrator.setPrompt("Go to Apps -> Connect mobile app on your desktop wallet and scan the QR Code to connect")
                intentIntegrator.initiateScan()
            }
        }

        btnReconnect.setOnClickListener {
            ConnectionManager.refreshAllData()
        }

        btnHelp.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setMessage(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(resources.getString(R.string.help_text), HtmlCompat.FROM_HTML_MODE_LEGACY) else Html.fromHtml(resources.getString(R.string.help_text)))
                .setNegativeButton(resources.getString(R.string.ok), DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle(resources.getString(R.string.help))
            alert.show()
        }

        swiperefresh.setOnRefreshListener {
            ConnectionManager.refreshAllData()
        }

        txtMainBalanceUSD.setOnClickListener {

            if(DataModel.selectedCurrency == "BTC")
                Toast.makeText(applicationContext, "1 HUSH = ${DataModel.currencySymbols[DataModel.selectedCurrency]}${DecimalFormat(" #,##0.00000000")
                    .format(DataModel.currencyValues[DataModel.selectedCurrency])}", Toast.LENGTH_LONG).show()
            else(
                Toast.makeText(applicationContext, "1 HUSH = ${DataModel.currencySymbols[DataModel.selectedCurrency]}${DecimalFormat("#,##0.00")
                .format(DataModel.currencyValues[DataModel.selectedCurrency])}", Toast.LENGTH_LONG).show()
                    )
        }

        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.action_send -> {
                    val intent = Intent(this, SendActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_bal -> true
                R.id.action_recieve -> {
                    val intent = Intent(this, ReceiveActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }

        loadSharedPref()
        updateUI(false)
    }

    private fun loadSharedPref() {
        var ref: SharedPreferences = getSharedPreferences("MainFile", 0)

        DataModel.selectedCurrency = ref.getString("currency", "BTC").toString()
    }

    private fun setMainStatus(status: String) {
        lblBalance.text = ""
        txtMainBalanceUSD.text = ""
        txtMainBalance.text = status
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(updateTxns: Boolean) {

        runOnUiThread {
            Log.i(TAG, "Updating UI $updateTxns")

            bottomNav.itemIconTintList = null
            bottomNav.menu.findItem(R.id.action_bal)?.isChecked = true
            when (connStatus) {
                ConnectionStatus.DISCONNECTED -> {
                    setMainStatus(resources.getString(R.string.no_connection))

                    scrollViewTxns.visibility = ScrollView.GONE
                    layoutConnect.visibility = ConstraintLayout.VISIBLE
                    swiperefresh.isRefreshing = false

                    // Hide the reconnect button if there is no connection string
                    if (DataModel.getConnString(SilentDragonApp.appContext!!).isNullOrBlank() ||
                        DataModel.getSecret() == null) {
                        btnHelp.visibility = Button.VISIBLE
                        btnReconnect.visibility = Button.GONE
                        lblConnectionOr.visibility = TextView.GONE
                    } else {
                        btnHelp.visibility = Button.GONE
                        btnReconnect.visibility = Button.VISIBLE
                        lblConnectionOr.visibility = TextView.VISIBLE
                    }

                    // Disable the send and recieve buttons
                    bottomNav.menu.findItem(R.id.action_recieve).isEnabled = false
                    bottomNav.menu.findItem(R.id.action_send).isEnabled = false

                    if (updateTxns) {
                        Handler().post {
                            run {
                                addPastTransactions(DataModel.transactions)
                            }
                        }
                    }
                }
                ConnectionStatus.CONNECTING -> {
                    setMainStatus(resources.getString(R.string.connecting))
                    scrollViewTxns.visibility = ScrollView.GONE
                    layoutConnect.visibility = ConstraintLayout.GONE
                    swiperefresh.isRefreshing = true

                    // Disable the send and recieve buttons
                    bottomNav.menu.findItem(R.id.action_recieve).isEnabled = false
                    bottomNav.menu.findItem(R.id.action_send).isEnabled = false
                }
                ConnectionStatus.CONNECTED -> {
                    scrollViewTxns.visibility = ScrollView.VISIBLE
                    layoutConnect.visibility = ConstraintLayout.GONE
                    ConnectionManager.initCurrencies()

                    if (DataModel.mainResponseData == null) {
                        setMainStatus(resources.getString(R.string.loading))
                    } else {
                        val cur = DataModel.selectedCurrency
                        val price = DataModel.currencyValues[cur]?: 0.0
                        val bal = DataModel.mainResponseData?.balance ?: 0.0
                        val balText = DecimalFormat("#0.00000000").format(bal)

                        lblBalance.text = resources.getString(R.string.balance)
                        txtMainBalance.text = balText + " ${DataModel.mainResponseData?.tokenName} "
                        if(cur == "BTC")
                            txtMainBalanceUSD.text =  "${DataModel.currencySymbols[cur]} " + DecimalFormat("0.00000000").format(bal * price)
                        else
                            txtMainBalanceUSD.text =  "${DataModel.currencySymbols[cur]} " + DecimalFormat("#,##0.00").format(bal * price)

                        // Enable the send and recieve buttons
                        bottomNav.menu.findItem(R.id.action_recieve).isEnabled = true
                        bottomNav.menu.findItem(R.id.action_send).isEnabled = true
                    }

                    if (updateTxns) {
                        Handler().post {
                            run {
                                addPastTransactions(DataModel.transactions)
                            }
                        }
                    } else {
                        swiperefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun addPastTransactions(txns: List<DataModel.TransactionItem>?) {
        runOnUiThread {
            val fragTx = supportFragmentManager.beginTransaction()

            for (fr in supportFragmentManager.fragments) {
                fragTx.remove(fr)
            }

            // If there are no transactions, make sure to commit the Tx, so existing items are removed, and just return
            if (txns.isNullOrEmpty()) {
                fragTx.commitAllowingStateLoss()

                swiperefresh.isRefreshing = false
                return@runOnUiThread
            }

            // Split all the transactions into confirmations = 0 and confirmations > 0
            // Unconfirmed first
            val unconfirmed = txns.filter { t -> t.confirmations == 0L }
            if (unconfirmed.isNotEmpty()) {
                for (tx in unconfirmed) {
                    fragTx.add(
                        txList.id ,
                        UnconfirmedTxItemFragment.newInstance(
                            Klaxon().toJsonString(tx),
                            ""
                        ),
                        "tag1"
                    )
                }
            }

            // Add all confirmed transactions
            val confirmed = txns.filter { t -> t.confirmations > 0L }
            if (confirmed.isNotEmpty()) {
                var oddeven = "odd"
                for (tx in confirmed) {
                    fragTx.add(
                        txList.id,
                        TransactionItemFragment.newInstance(
                            Klaxon().toJsonString(tx),
                            oddeven
                        ),
                        "tag1"
                    )
                    oddeven = if (oddeven == "odd") "even" else "odd"
                }
            }
            fragTx.commitAllowingStateLoss()

            swiperefresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_refresh -> {
                swiperefresh.isRefreshing = true
                ConnectionManager.refreshAllData()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // We've received a signal
            when(intent.getStringExtra("action")) {
                "refresh" -> {
                    swiperefresh.isRefreshing = !intent.getBooleanExtra("finished", true)
                }
                "newdata" -> {
                    val updateTxns = intent.getBooleanExtra("updateTxns", false)
                    updateUI(updateTxns)
                }
                "error" -> {
                    val msg = intent.getStringExtra("msg")

                    if (!msg.isNullOrEmpty()) {
                        Snackbar.make(layoutConnect, msg, Snackbar.LENGTH_LONG).show()
                    }

                    // Also check if we need to disconnect
                    if (intent.getBooleanExtra("doDisconnect", false)) {
                        disconnected()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mReceiver, IntentFilter(ConnectionManager.DATA_SIGNAL))

        // On resuming, refresh all data
        ConnectionManager.refreshAllData()
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    // the toasts work here so commenting out
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                //Toast.makeText(applicationContext, result.contents,Toast.LENGTH_LONG).show()
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(applicationContext,"scan failed",Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){
            if(result.contents != null){
                Log.d("MainActivity", "Scanned" + result.contents)
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show();
                processMobileConnectorText(result.contents)
            } else {
                Log.d("MainActivity", "Cancelled scan")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun disconnected() {
        Log.i(TAG, "Disconnected")

        connStatus = ConnectionStatus.DISCONNECTED
        println("Connstatus = Disconnected")

        DataModel.clear()
        swiperefresh.isRefreshing = false
        updateUI(true)
    }

    private fun processMobileConnectorText(qrcodeInfo: String) {
        if (qrcodeInfo.startsWith("ws")) {
            Log.i(TAG, "It's a ws connection")
            //Toast.makeText(this, "YEAH " + qrcodeInfo.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "Not a ws connection")
            //Toast.makeText(this, "Not a ws connection", Toast.LENGTH_SHORT).show();
        }
    }

    private val TAG = "MainActivity"
}
