package com.example.my.telegram.ui.screens.group

import androidx.recyclerview.widget.RecyclerView
import com.example.my.telegram.R
import com.example.my.telegram.database.CURRENT_UID
import com.example.my.telegram.database.NODE_MESSAGES
import com.example.my.telegram.database.NODE_PHONES_CONTACTS
import com.example.my.telegram.database.NODE_USERS
import com.example.my.telegram.database.REF_DATABASE_ROOT
import com.example.my.telegram.database.getCommonModel
import com.example.my.telegram.databinding.FragmentAddContactsBinding
import com.example.my.telegram.databinding.FragmentContactsBinding
import com.example.my.telegram.models.CommonModel
import com.example.my.telegram.ui.screens.base.BaseFragment
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AppValueEventListener
import com.example.my.telegram.utils.hideKeyboard
import com.example.my.telegram.utils.replaceFragment
import com.example.my.telegram.utils.showToast

/* Главный фрагмент, содержит все чаты, группы и каналы с которыми взаимодействует пользователь*/

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentAddContactsBinding.inflate(layoutInflater)
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefContactsList = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        listContacts.clear()
        super.onResume()
        APP_ACTIVITY.title = "Добавить участника"
        hideKeyboard()
        initRecyclerView()
        binding.addContactsBtnNext.setOnClickListener {
            if (listContacts.isEmpty()) showToast("Добавьте участника")
            else replaceFragment(CreateGroupFragment(listContacts))
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.addContactsRecycleView
        mAdapter = AddContactsAdapter()

        // 1 запрос
        mRefContactsList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->
                // 2 запрос
                mRefUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getCommonModel()

                        // 3 запрос
                        mRefMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getCommonModel() }

                                if (tempList.isEmpty()) {
                                    newModel.lastMessage = "Чат очищен"
                                } else {
                                    newModel.lastMessage = tempList[0].text
                                }


                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }

}