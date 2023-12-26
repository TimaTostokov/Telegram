package com.example.my.telegram.ui.fragments

import com.example.my.telegram.R
import com.example.my.telegram.databinding.FragmentChangeUserNameBinding
import com.example.my.telegram.utils.AppValueEventListener
import com.example.my.telegram.utils.CHILD_USERNAME
import com.example.my.telegram.utils.NODE_USERNAMES
import com.example.my.telegram.utils.NODE_USERS
import com.example.my.telegram.utils.REF_DATABASE_ROOT
import com.example.my.telegram.utils.CURRENT_UID
import com.example.my.telegram.utils.USER
import com.example.my.telegram.utils.showToast
import java.util.Locale

class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangeUserNameBinding.inflate(layoutInflater)
    }

    private lateinit var mNewUserName: String


    override fun onResume() {
        super.onResume()
        binding.settingsInputUsername.setText(USER.username)
    }

    override fun change() {
        mNewUserName = binding.settingsInputUsername.text.toString().lowercase(Locale.getDefault())
        if (mNewUserName.isEmpty()) {
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUserName)) {
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUserName).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
            .setValue(mNewUserName)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("ERROR!!!")
                    deleteOldUserName()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUserName() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("ERROR!!!")
                    fragmentManager?.popBackStack()
                    USER.username = mNewUserName
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

}