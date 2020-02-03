package org.myhush.silentdragon

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.myhush.silentdragon.chat.Message
import java.text.SimpleDateFormat
import java.util.*

class conversation_item_send : Fragment() {
    var msg: Message? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v: View = inflater.inflate(R.layout.fragment_conversation_item_send, container, false)
        v.findViewById<TextView>(R.id.message).text = msg?.memo
        v.findViewById<TextView>(R.id.Time).text = SimpleDateFormat("MM-dd, HH:mm").format(Date(msg!!.dateTime))
        return v
    }
}
