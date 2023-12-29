package com.example.my.telegram.ui.messagerecyclerview.viewholders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.my.telegram.database.CURRENT_UID
import com.example.my.telegram.ui.messagerecyclerview.views.MessageView
import com.example.my.telegram.utils.asTime

class HolderTextMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private  val blocUserMessage: ConstraintLayout = view.bloc_user_message
    private  val chatUserMessage: TextView = view.chat_user_message
    private  val chatUserMessageTime: TextView = view.chat_user_message_time
    private  val blocReceivedMessage: ConstraintLayout = view.bloc_received_message
    private  val chatReceivedMessage: TextView = view.chat_received_message
    private  val chatReceivedMessageTime: TextView = view.chat_received_message_time


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocUserMessage.visibility = View.VISIBLE
            blocReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blocUserMessage.visibility = View.GONE
            blocReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {}

    override fun onDetach() {}

}