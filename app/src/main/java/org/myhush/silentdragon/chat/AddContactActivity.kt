package org.myhush.silentdragon.chat

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_contact.*
import org.myhush.silentdragon.Addressbook
import org.myhush.silentdragon.R

class AddContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        buttonAddContact.setOnClickListener {
            if (checkValueValidation()) {
                var nickName = editText_NickName.text.toString()
                var fullname = editText_FullName.text.toString()
                var zAddr = editTextZAddress.text.toString()

                Addressbook.addContact(fullname, nickName, zAddr)

                this.finish()
            }
        }
    }

    private fun sendErrorDialog(msg: String) {
        val alertDialog = AlertDialog.Builder(this@AddContactActivity).create()
        alertDialog.setTitle("Error by add a new Contact!")
        alertDialog.setMessage(msg)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") {
                dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun checkValueValidation(): Boolean{
        if (editText_FullName.text.length <= 1) {
            sendErrorDialog("Pleas insert a name!")
            return false
        }
        if (editText_NickName.text.length <= 1) {
            sendErrorDialog("Pleas insert a nickname!")
            return false
        }
        if (editTextZAddress.text.length <= 1) {
            if(!editTextZAddress.text.startsWith('z', true)) {
                sendErrorDialog("Pleas insert a z-address!")
                return false
            }
        }

        return true
    }
}
