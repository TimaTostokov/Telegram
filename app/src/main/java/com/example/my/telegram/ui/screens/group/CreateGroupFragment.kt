package com.example.my.telegram.ui.screens.group

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.my.telegram.ui.screens.mainlist.MainListFragment
import com.example.my.telegram.R
import com.example.my.telegram.database.createGroupToDatabase
import com.example.my.telegram.databinding.FragmentCreateGroupBinding
import com.example.my.telegram.models.CommonModel
import com.example.my.telegram.ui.screens.base.BaseFragment
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.getPlurals
import com.example.my.telegram.utils.hideKeyboard
import com.example.my.telegram.utils.replaceFragment
import com.example.my.telegram.utils.showToast

class CreateGroupFragment(private var listContacts: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentCreateGroupBinding.inflate(layoutInflater)
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mUri = Uri.EMPTY

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        initRecyclerView()
        binding.createGroupPhoto.setOnClickListener { addPhoto() }
        binding.createGroupBtnComplete.setOnClickListener {
            val nameGroup = binding.createGroupInputName.text.toString()
            if (nameGroup.isEmpty()) {
                showToast("Введите имя")
            } else {
                createGroupToDatabase(nameGroup, mUri, listContacts) {
                    replaceFragment(MainListFragment())
                }
            }
        }
        binding.createGroupInputName.requestFocus()
        binding.createGroupCounts.text = getPlurals(listContacts.size)
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.createGroupRecycleView
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach { mAdapter.updateListItems(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* Активность которая запускается для получения картинки для фото пользователя */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            mUri = CropImage.getActivityResult(data).uri
            binding.createGroupPhoto.setImageURI(mUri)
        }
    }

}