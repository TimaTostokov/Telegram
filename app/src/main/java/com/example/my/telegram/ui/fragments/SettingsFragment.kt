package com.example.my.telegram.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.my.telegram.R
import com.example.my.telegram.activities.RegisterActivity
import com.example.my.telegram.databinding.FragmentSettingsBinding
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AUTH
import com.example.my.telegram.utils.CURRENT_UID
import com.example.my.telegram.utils.FOLDER_PROFILE_IMAGE
import com.example.my.telegram.utils.REF_STORAGE_ROOT
import com.example.my.telegram.utils.USER
import com.example.my.telegram.utils.downloadAndSetImage
import com.example.my.telegram.utils.getUrlFromStorage
import com.example.my.telegram.utils.putImageToStorage
import com.example.my.telegram.utils.putUrlToDatabase
import com.example.my.telegram.utils.replaceActivity
import com.example.my.telegram.utils.replaceFragment
import com.example.my.telegram.utils.showToast

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        binding.settingsBio.text = USER.bio
        binding.settingsFullName.text = USER.fullname
        binding.settingsPhoneNumber.text = USER.phone
        binding.settingsStatus.text = USER.state
        binding.settingsUsername.text = USER.username
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)

        binding.settingsBtnChangeUsername.setOnClickListener {
            replaceFragment(
                ChangeUserNameFragment()
            )
        }

        binding.settingsBtnChangeBio.setOnClickListener {
            replaceFragment(ChangeBioFragment())
        }

        binding.settingsChangePhoto.setOnClickListener {
            changePhotoUser()
        }
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                APP_ACTIVITY.replaceActivity(RegisterActivity())
            }

            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)
            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        binding.settingsUserPhoto.downloadAndSetImage(it)
                        showToast("OKAY")
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }

}