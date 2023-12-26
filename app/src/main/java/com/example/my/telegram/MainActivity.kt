package com.example.my.telegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.my.telegram.activities.RegisterActivity
import com.example.my.telegram.databinding.ActivityMainBinding
import com.example.my.telegram.ui.fragments.ChatsFragment
import com.example.my.telegram.ui.objects.AppDrawer
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AUTH
import com.example.my.telegram.utils.AppStates
import com.example.my.telegram.utils.initFirebase
import com.example.my.telegram.utils.initUser
import com.example.my.telegram.utils.replaceActivity
import com.example.my.telegram.utils.replaceFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            initFields()
            initFunc()
        }
    }

    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(mToolbar)
            mAppDrawer.create()
            replaceFragment(ChatsFragment(), false)
        } else {
            replaceActivity(RegisterActivity())
        }
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

}