package com.example.my.telegram.ui.screens.settings

import com.example.my.telegram.R
import com.example.my.telegram.database.USER
import com.example.my.telegram.database.setBioToDatabase
import com.example.my.telegram.databinding.FragmentChangeBioBinding
import com.example.my.telegram.databinding.FragmentContactsBinding
import com.example.my.telegram.ui.screens.base.BaseChangeFragment

/* Фрагмент для изменения информации о пользователе */

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangeBioBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputBio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = binding.settingsInputBio.text.toString()
        setBioToDatabase(newBio)
    }

}