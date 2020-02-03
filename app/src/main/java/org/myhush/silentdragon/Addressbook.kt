package org.myhush.silentdragon

import org.myhush.silentdragon.chat.Message

object Addressbook {
    class Contact {
        var nickname = ""
        var addressIn = ""
        var addressOut = ""
        val messageList = ArrayList<Message>()

        constructor(nickname: String, addressIn: String, addressOut: String) {
            this.nickname = nickname
            this.addressIn = addressIn
            this.addressOut = addressOut
        }
    }

    val contactList = ArrayList<Contact>()

    fun addContact(nickname: String, addressIn: String, addressOut: String) {
        contactList.add(Contact(nickname, addressIn, addressOut))
    }

    fun findContactByInAddress(address: String): Contact? {
        contactList.forEach {
            if(it.addressIn == address)
                return it
        }
        return null
    }

    fun findContactByOutAddress(address: String): Contact? {
        contactList.forEach {
            if (it.addressOut == address)
                return it
        }
        return null
    }

    fun clear() {
        contactList.clear()
    }
}