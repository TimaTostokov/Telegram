package com.example.my.telegram.ui.messagerecyclerview.viewholders

import com.example.my.telegram.ui.messagerecyclerview.views.MessageView


interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}