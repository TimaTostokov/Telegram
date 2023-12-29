package com.example.my.telegram.ui.screens.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.my.telegram.R
import com.example.my.telegram.database.AUTH
import com.example.my.telegram.database.CURRENT_UID
import com.example.my.telegram.database.FOLDER_PROFILE_IMAGE
import com.example.my.telegram.database.REF_STORAGE_ROOT
import com.example.my.telegram.database.USER
import com.example.my.telegram.database.getUrlFromStorage
import com.example.my.telegram.database.putFileToStorage
import com.example.my.telegram.database.putUrlToDatabase
import com.example.my.telegram.databinding.FragmentSettingsBinding
import com.example.my.telegram.ui.screens.base.BaseFragment
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AppStates
import com.example.my.telegram.utils.downloadAndSetImage
import com.example.my.telegram.utils.replaceFragment
import com.example.my.telegram.utils.restartActivity
import com.example.my.telegram.utils.showToast

/* Фрагмент настроек */

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
        binding.settingsBtnChangeUsername.setOnClickListener {
            replaceFragment(
                ChangeUsernameFragment()
            )
        }
        binding.settingsBtnChangeBio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        binding.settingsChangePhoto.setOnClickListener { changePhotoUser() }
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {
        /* Изменения фото пользователя */
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        /* Создания выпадающего меню*/
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Слушатель выбора пунктов выпадающего меню */
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }

            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* Активность которая запускается для получения картинки для фото пользователя */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {

            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(
                FOLDER_PROFILE_IMAGE
            )
                .child(CURRENT_UID)
            putFileToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        binding.settingsUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }

}