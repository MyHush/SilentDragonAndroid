// Copyright 2019-2020 The Hush developers
package org.myhush.silentdragon

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_send.*
import kotlinx.android.synthetic.main.content_send.*
import java.text.DecimalFormat


class SendActivity : AppCompatActivity() {

    private val REQUEST_CONFIRM = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.send_transaction)

        // Clear the valid address prompt
        txtValidAddress.text = ""
        txtSendCurrencySymbol.text = ""

        if (intent.getStringExtra("address") != null)
            sendAddress.setText(intent.getStringExtra("address"), TextView.BufferType.EDITABLE)

        if (intent.getDoubleExtra("amount", -1.0) > 0)
            setAmountHUSH(intent.getDoubleExtra("amount", 0.0))

        if (intent.getBooleanExtra("includeReplyTo", false))
            chkIncludeReplyTo.isChecked = true

        imageButton.setOnClickListener { view ->
            val intent = Intent(this, QrReaderActivity::class.java)
            intent.putExtra("REQUEST_CODE",
                QrReaderActivity.REQUEST_ADDRESS
            )
            startActivityForResult(intent,
                QrReaderActivity.REQUEST_ADDRESS
            )
        }

        if (DataModel.currencyValues["USD"] == null)
            ConnectionManager.initCurrencies()

        if (DataModel.selectedCurrency == "BTC")
            amountUSD.text = "${DataModel.currencySymbols[DataModel.selectedCurrency]} " + DecimalFormat("0.00000000").format(0)
        else
        {
            amountUSD.text = "${DataModel.currencySymbols[DataModel.selectedCurrency]} " + DecimalFormat("0.00").format(0)
        }

        textViewFee.text = DecimalFormat("0.0000").format(0.0001) + " HUSH"

        sendAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (DataModel.isValidAddress(s.toString())) {
                    txtValidAddress.text = "\u2713 " + getString(R.string.valid_address)
                    txtValidAddress.setTextColor(ContextCompat.getColor(applicationContext,
                        R.color.white_selected
                    ))
                } else {
                    txtValidAddress.text = getString(R.string.not_a_valid_hush_address)
                    txtValidAddress.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                }

                if (s?.startsWith("R") == true) {
                    txtSendMemo.isEnabled       = false
                    chkIncludeReplyTo.isEnabled = false
                    txtSendMemo.text            = SpannableStringBuilder("")
                    txtSendMemoTitle.text       = getString(R.string.no_memo_for_taddresses)
                } else {
                    txtSendMemo.isEnabled = true
                    chkIncludeReplyTo.isEnabled = true
                    txtSendMemoTitle.text = getString(R.string.memo_optional)
                }
            }
        })

        amountHUSH.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hush = s.toString().toDoubleOrNull()
                val price = DataModel.currencyValues[DataModel.selectedCurrency]
                val symbol = DataModel.currencySymbols[DataModel.selectedCurrency]

                if (hush == null)
                    txtSendCurrencySymbol.text = "" // Let the placeholder show
                else {
                    txtSendCurrencySymbol.text = "HUSH"
                }

                if (hush == null || price == null)
                    if (symbol == "BTC")
                        amountUSD.text = "$symbol " + DecimalFormat("0.00000000").format(0)
                    else {
                        amountUSD.text = "$symbol " + DecimalFormat("0.00").format(0)
                    }
                else
                    if (symbol == "BTC")
                        amountUSD.text = "$symbol " + DecimalFormat("#,##0.00000000").format(hush * price)
                    else {
                        amountUSD.text = "$symbol " + DecimalFormat("#,##0.00").format(hush * price)
                    }
            }
        })

        txtSendMemo.imeOptions = EditorInfo.IME_ACTION_DONE
        txtSendMemo.setRawInputType(InputType.TYPE_CLASS_TEXT)

        txtSendMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtMemoSize.text = "${s?.length ?: 0} / 512"
                if (s?.length ?: 0 > 512) {
                    txtMemoSize.setTextColor(ContextCompat.getColor(applicationContext,
                        R.color.colorAccent
                    ))
                } else {
                    txtMemoSize.setTextColor(ContextCompat.getColor(applicationContext,
                        R.color.white_selected
                    ))
                }
            }
        })

        btnSend.setOnClickListener { view ->
            doValidationsThenConfirm()
        }
    }

    private fun doValidationsThenConfirm()  {
        // First, check if the address is correct.
        val toAddr = sendAddress.text.toString()
        if (!DataModel.isValidAddress(toAddr)) {
            showErrorDialog(getString(R.string.invalid_destination_Hush_address))
            return
        }

        // Then if the amount is valid
        val amt = amountHUSH.text.toString()

        // amount=0 xtns are valid
        if (amt.toDoubleOrNull() == null || amt.toDouble() < 0.0 ) {
            showErrorDialog(getString(R.string.invalid_amount))
            return
        }

        println("Maxzspendable ${DataModel.mainResponseData?.maxzspendable}")

        // Check if this is more than the maxzspendable
        if (DataModel.mainResponseData?.maxzspendable != null) {
            if (amt.toDouble() > DataModel.mainResponseData?.maxzspendable!! &&
                amt.toDouble() <= DataModel.mainResponseData?.maxspendable ?: Double.MAX_VALUE) {

                val alertDialog = AlertDialog.Builder(this@SendActivity)
                alertDialog.setTitle(getString(R.string.send_from_taddr))
                //alertDialog.setMessage("$amt ${DataModel.mainResponseData?.tokenName}" + "..."
                alertDialog.setMessage(getString(R.string.more_than_shielded_address, amt, DataModel.mainResponseData?.tokenName))
                alertDialog.apply {
                    setPositiveButton(getString(R.string.send_anyway)) { dialog, id -> doConfirm() }
                    setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
                }

                alertDialog.create().show()
                return
            }
        }

        // Warning if spending more than total
        if (amt.toDouble() > DataModel.mainResponseData?.maxspendable ?: Double.MAX_VALUE) {
            //showErrorDialog("Can't spend more than ${DataModel.mainResponseData?.tokenName} " + "${DataModel.mainResponseData?.maxspendable} in a single Tx")
            showErrorDialog(getString(R.string.max_spend_in_a_single_tx, DataModel.mainResponseData?.maxspendable, DataModel.mainResponseData?.tokenName ))
            return
        }

        val memo = txtSendMemo.text.toString() + getReplyToAddressIfChecked(toAddr)
        if (memo.length > 512) {
            showErrorDialog(getString(R.string.memo_field_over_512))
            return
        }

        if (toAddr.startsWith("R") && !memo.isBlank()) {
            showErrorDialog(getString(R.string.cant_send_a_memo_to_a_taddr))
            return
        }

        doConfirm()
    }

    private fun doConfirm() {
        val toAddr = sendAddress.text.toString()
        val amt = amountHUSH.text.toString()
        val memo = txtSendMemo.text.toString() + getReplyToAddressIfChecked(toAddr)

        val intent = Intent(this, TxDetailsActivity::class.java)
        val tx = DataModel.TransactionItem(
            "confirm", 0, amt, memo,
            toAddr, "", 0
        )
        intent.putExtra("EXTRA_TXDETAILS", Klaxon().toJsonString(tx))
        startActivityForResult(intent, REQUEST_CONFIRM)
    }

    private fun getReplyToAddressIfChecked(toAddr: String) : String {
        if (chkIncludeReplyTo.isChecked && toAddr.startsWith("zs1")) {
            return "\n" + getString(R.string.reply_to) + ":\n${DataModel.mainResponseData?.saplingAddress}"
        } else {
            return ""
        }
    }

    fun showErrorDialog(msg: String) {
        val alertDialog = AlertDialog.Builder(this@SendActivity).create()
        alertDialog.setTitle(getString(R.string.error_sending_transaction))
        alertDialog.setMessage(msg)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok)) {
                dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun Double.format(digits: Int): String? = java.lang.String.format("%.${digits}f", this)

    private fun setAmountHUSH(amt: Double) {
        amountHUSH.setText((DecimalFormat("#.########").format(amt) + "${DataModel.mainResponseData?.tokenName}"))
        setAmountUSD(amt)
    }

    private fun setAmountUSD(amt: Double?) {
        if (amt == null) {
            return
        }
        // Since there is a text-change listner on the USD field, we set the USD first, then override the
        // HUSH field manually.
        amountHUSH.setText((DecimalFormat("#.########").format(amt) + "${DataModel.mainResponseData?.tokenName}"))
        Toast.makeText(this.applicationContext, amt.toString(), Toast.LENGTH_SHORT).show()
        amountUSD.text = "${DataModel.currencySymbols[DataModel.selectedCurrency]} " + DecimalFormat("#,##0.00").format(amt)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            QrReaderActivity.REQUEST_ADDRESS -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.scheme == "hush") {
                        sendAddress.setText(data.data?.host ?: "", TextView.BufferType.EDITABLE)

                        var amt = data.data?.getQueryParameter("amt") ?:
                        data.data?.getQueryParameter("amount")

                        // Remove all commas.
                        amt = amt?.replace(",", ".")

                        if (amt != null) {
                            setAmountUSD(amt.toDoubleOrNull())
                        }

                        val memo = data.data?.getQueryParameter("memo")
                        if (memo != null) {
                            txtSendMemo.setText(memo)
                        }
                    } else {
                        sendAddress.setText(data?.dataString ?: "", TextView.BufferType.EDITABLE)
                    }

                    amountHUSH.requestFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
            }
            REQUEST_CONFIRM -> {
                if (resultCode == Activity.RESULT_OK) {
                    // Send async, so that we don't mess up the activity flow
                    Handler().post {
                        val tx = Klaxon().parse<DataModel.TransactionItem>(data?.dataString!!)
                        DataModel.sendTx(tx!!)

                        finish()
                    }
                }
            }
        }
    }

}
