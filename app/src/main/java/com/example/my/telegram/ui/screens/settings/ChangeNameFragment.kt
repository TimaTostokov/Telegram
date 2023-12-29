package com.example.my.telegram.ui.screens.settings

import com.example.my.telegram.R
import com.example.my.telegram.database.USER
import com.example.my.telegram.database.setNameToDatabase
import com.example.my.telegram.databinding.FragmentChangeNameBinding
import com.example.my.telegram.ui.screens.base.BaseChangeFragment
import com.example.my.telegram.utils.showToast

/* Фрагмент для изменения имени пользователя */

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangeNameBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        initFullnameList()
    }

    private fun initFullnameList() {
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            binding.settingsInputName.setText(fullnameList[0])
            binding.settingsInputSurname.setText(fullnameList[1])
        } else binding.settingsInputName.setText(fullnameList[0])
    }

    override fun change() {
        val name = binding.settingsInputName.text.toString()
        val surname = binding.settingsInputSurname.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullname = "$name $surname"
            setNameToDatabase(fullname)
        }
    }

}