package com.example.my.telegram.ui.fragments

import com.example.my.telegram.R
import com.example.my.telegram.databinding.FragmentChangeNameBinding
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.CHILD_FULLNAME
import com.example.my.telegram.utils.NODE_USERS
import com.example.my.telegram.utils.REF_DATABASE_ROOT
import com.example.my.telegram.utils.CURRENT_UID
import com.example.my.telegram.utils.USER
import com.example.my.telegram.utils.showToast

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangeNameBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        initFullNameList()
    }

    private fun initFullNameList() {
        val fullNameList = USER.fullname.split(" ")
        if (fullNameList.size > 1) {
            binding.settingsInputName.setText(fullNameList[0])
            binding.settingsInputSurname.setText(fullNameList[1])
        } else {
            binding.settingsInputName.setText(fullNameList[0])
        }
    }

    override fun change() {
        val name = binding.settingsInputName.text.toString()
        val surname = binding.settingsInputSurname.text.toString()
        if (name.isEmpty()) {
            showToast("Имя не может не быть пустым")
        } else {
            val fullName = "$name $surname"
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME).setValue(fullName)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Данные обновлены")
                        USER.fullname = fullName
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                        fragmentManager?.popBackStack()
                    }
                }
        }
    }

}