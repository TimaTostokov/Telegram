package com.example.my.telegram.ui.screens.settings

import com.example.my.telegram.R
import com.example.my.telegram.database.CURRENT_UID
import com.example.my.telegram.database.NODE_USERNAMES
import com.example.my.telegram.database.REF_DATABASE_ROOT
import com.example.my.telegram.database.USER
import com.example.my.telegram.database.updateCurrentUsername
import com.example.my.telegram.databinding.FragmentChangeUserNameBinding
import com.example.my.telegram.ui.screens.base.BaseChangeFragment
import com.example.my.telegram.utils.AppValueEventListener
import com.example.my.telegram.utils.showToast
import java.util.*

/* Фрагмент для изменения username пользователя */

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangeUserNameBinding.inflate(layoutInflater)
    }

    lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        binding.settingsInputUsername.setText(USER.username)
    }

    override fun change() {
        mNewUsername = binding.settingsInputUsername.text.toString().toLowerCase(Locale.getDefault())
        if (mNewUsername.isEmpty()) {
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT.child(
                NODE_USERNAMES
            ).addListenerForSingleValueEvent(AppValueEventListener {
                if (it.hasChild(mNewUsername)) {
                    showToast("Такой пользователь уже существует")
                } else {
                    changeUsername()
                }
            })

        }
    }

    private fun changeUsername() {
        /* Изменение username в базе данных */
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(
            CURRENT_UID
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(mNewUsername)
                }
            }
    }

}