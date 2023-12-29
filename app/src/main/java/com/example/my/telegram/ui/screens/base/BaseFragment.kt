package com.example.my.telegram.ui.screens.base

import androidx.fragment.app.Fragment
import com.example.my.telegram.MainActivity
import com.example.my.telegram.utils.APP_ACTIVITY

open class BaseFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }

}