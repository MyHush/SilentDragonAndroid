package org.myhush.silentdragon.chat

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_chat.*
import org.myhush.silentdragon.R
import org.myhush.silentdragon.conversation_item_recive
import org.myhush.silentdragon.conversation_item_send

class ConversationActivity : AppCompatActivity() {
    var displayName = ""
    var contact: Addressbook.Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        displayName = intent.extras.getString("displayName")
        findViewById<TextView>(R.id.textViewContactName2).text = displayName


        restoreChat()
    }

    private fun restoreChat() {
        contact?.messageList?.forEach {
            attachMessage(it)
        }
    }

    fun attachMessage(message: Message){
        val fragTx: FragmentTransaction = supportFragmentManager.beginTransaction()

        when (message.messageType){
            MessageType.SEND -> {
                val fragment = conversation_item_send()
                fragment.msg = message
                fragTx.add(R.id.MessageList, fragment)
            }

            MessageType.RECIEVE -> {
                val fragment = conversation_item_recive()
                fragment.msg = message
                fragTx.add(R.id.MessageList, fragment)
            }
        }
        fragTx.commit()
    }
}
