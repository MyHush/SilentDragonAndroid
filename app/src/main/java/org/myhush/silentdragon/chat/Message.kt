package org.myhush.silentdragon.chat

import org.myhush.silentdragon.DataModel

class Message {
    var memo = ""
    var fromAddress = ""
    var toAddress = ""
    var messageType = MessageType.SEND
    var dateTime: Long = 0
    var txID = ""
    var txHeight: Int = -1

    constructor(fromAddress: String, tx: DataModel.TransactionItem, messageType: MessageType){
        this.memo = tx.memo.toString()
        this.txID = tx.txid.toString()
        this.dateTime = tx.datetime
        this.toAddress = tx.addr
        this.fromAddress = fromAddress
        this.messageType = messageType
    }
}