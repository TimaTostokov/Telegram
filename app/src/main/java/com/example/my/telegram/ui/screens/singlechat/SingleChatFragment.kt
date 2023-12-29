package com.example.my.telegram.ui.screens.singlechat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.my.telegram.R
import com.example.my.telegram.database.CURRENT_UID
import com.example.my.telegram.database.NODE_MESSAGES
import com.example.my.telegram.database.NODE_USERS
import com.example.my.telegram.database.REF_DATABASE_ROOT
import com.example.my.telegram.database.TYPE_TEXT
import com.example.my.telegram.database.getCommonModel
import com.example.my.telegram.database.getUserModel
import com.example.my.telegram.database.sendMessage
import com.example.my.telegram.databinding.FragmentSingleChatBinding
import com.example.my.telegram.models.CommonModel
import com.example.my.telegram.models.User
import com.example.my.telegram.ui.screens.base.BaseFragment
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AppValueEventListener
import com.example.my.telegram.utils.showToast
import com.google.firebase.database.DatabaseReference

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentSingleChatBinding.inflate(layoutInflater)
    }

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: User
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppValueEventListener
    private var mListMessages = emptyList<CommonModel>()

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = binding.chatRecycleView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mListMessages = dataSnapshot.children.map { it.getCommonModel() }
            mAdapter.setList(mListMessages)
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
        }
        mRefMessages.addValueEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.findViewById(R.id.toolbar_info)
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        binding.chatBtnSendMessage.setOnClickListener {
            val message = binding.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("ВВедите сообщение")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                binding.chatInputMessage.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.findViewById<View>(R.id.toolbar_chat_fullname).text = contact.fullname
        } else mToolbarInfo.findViewById<View>(R.id.toolbar_chat_fullname).text =
            mReceivingUser.fullname

        mToolbarInfo.findViewById<View>(R.id.toolbar_chat_image)
            .downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.findViewById<View>(R.id.toolbar_chat_status).text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

}