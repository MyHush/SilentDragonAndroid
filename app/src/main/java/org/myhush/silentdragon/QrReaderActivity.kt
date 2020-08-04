// Copyright 2019-2020 The Hush developers
package org.myhush.silentdragon

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import kotlinx.android.synthetic.main.activity_qr_reader.*

class QrReaderActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager

    companion object {
        const val REQUEST_ADDRESS = 1
        const val REQUEST_CONNDATA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_reader)

        title = getString(R.string.scan_qr_code)

        val code = intent.getIntExtra("REQUEST_CODE", 0)
        if (code == REQUEST_ADDRESS)
            txtQrCodeHelp.text = ""

        lblErrorMsg.text = ""

        btnQrCodeCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)

        barcodeView.decodeSingle(object: BarcodeCallback{
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    if (result.text != null) {
                        processQrCodeText(result.text)
                    }
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            50 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Do what if user refuses permission? Go back?
                } else {
                    recreate()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_qrcodereader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_manual_input -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.paste_the_code_here_manually))

                // Set up the input
                val input = EditText(this)
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                // Set up the buttons
                builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    run {
                        val txt = input.text.toString()
                        processText(txt)
                    }
                }
                builder.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.cancel() }

                builder.create().show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    private fun processText(barcodeInfo: String) {
        val code = intent.getIntExtra("REQUEST_CODE", 0)

        // See if this the data is of the right format
        if (code == REQUEST_CONNDATA && !barcodeInfo.startsWith("ws")) {
            Log.i(TAG, "Not a connection")
            var err = barcodeInfo
            if (err.length > 48) {
                err = err.substring(0, 22) + "...." + err.substring(err.length - 22, err.length)
            }
            lblErrorMsg.text = getString(R.string.is_not_a_valid_connection_string, err)
            return
        }

        if (code == REQUEST_ADDRESS &&
            !DataModel.isValidAddress(StringBuilder(barcodeInfo).toString()) &&
            !barcodeInfo.startsWith("hush:")) {
            Log.i(TAG, "Not an address")
            var err = barcodeInfo
            if (err.length > 48) {
                err = err.substring(0, 22) + "...." + err.substring(err.length - 22, err.length)
            }
            lblErrorMsg.text = getString(R.string.is_not_a_valid_hush_address, err)
            return
        }

        // The data seems valid, so return it.
        val data = Intent()

        // Payment URIs are often formatted as "hush:<addr>", but this causes parsing problems.
        // So change it to hush://<addr>, so that it parses properly
        if (barcodeInfo.startsWith("hush:") && !barcodeInfo.startsWith("hush://")) {
            data.data = Uri.parse(barcodeInfo.replaceFirst("hush:", "hush://"))
        } else {
            data.data = Uri.parse(barcodeInfo)
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun processQrCodeText(qrcodeInfo: String) {
        if (qrcodeInfo.startsWith("ws")) {
            Log.i(TAG, "It's a ws connection")
            //Toast.makeText(this, "YEAH: " + qrcodeInfo, Toast.LENGTH_SHORT).show();

            val data = Intent()     // The data seems valid, so return it
            data.data = Uri.parse(qrcodeInfo)
            setResult(Activity.RESULT_OK, data)
            finish()
        } else {
            Log.i(TAG, "Not a ws connection")
            //Toast.makeText(this, "Not a ws connection", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private val TAG = "QrReader"
}
