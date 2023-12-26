package com.example.my.telegram.ui.fragments

import com.example.my.telegram.R
import com.example.my.telegram.databinding.FragmentChangeBioBinding
import com.example.my.telegram.utils.CHILD_BIO
import com.example.my.telegram.utils.NODE_USERS
import com.example.my.telegram.utils.REF_DATABASE_ROOT
import com.example.my.telegram.utils.CURRENT_UID
import com.example.my.telegram.utils.USER
import com.example.my.telegram.utils.showToast

@Suppress("DEPRECATION")
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
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Success")
                    USER.bio = newBio
                    fragmentManager?.popBackStack()
                }
            }
    }

}