package org.myhush.silentdragon.chat

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_chat.*
import org.myhush.silentdragon.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initListener()
        refresh()

        swiperefreshChat.setOnRefreshListener {
            refresh()
            swiperefreshChat.isRefreshing = false
        }
    }

    private fun initListener(){
        nav_view.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.action_send -> {
                    val intent = Intent(this, SendActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_bal -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_chat -> true
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
    }

    private fun refresh(){
        findViewById<LinearLayout>(R.id.ChatTable).removeAllViews()

        Addressbook.contactList.forEach {
            addChat(it)
        }
    }

    private fun addChat(contact: Addressbook.Contact){
        val fragment = ChatItemFragment()
        val fragTx: FragmentTransaction = supportFragmentManager.beginTransaction()

        fragment.nickname = contact.nickname
        fragment.contactAddress = contact.addressIn

        fragTx.add(R.id.ChatTable, fragment)
        fragTx.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_addChat -> {
                val intent = Intent(this, AddContactActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
