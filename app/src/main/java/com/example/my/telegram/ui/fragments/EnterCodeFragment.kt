package com.example.my.telegram.ui.fragments

import androidx.fragment.app.Fragment
import com.example.my.telegram.MainActivity
import com.example.my.telegram.R
import com.example.my.telegram.activities.RegisterActivity
import com.example.my.telegram.databinding.FragmentEnterCodeBinding
import com.example.my.telegram.utils.AppTextWatcher
import com.example.my.telegram.utils.CHILD_ID
import com.example.my.telegram.utils.CHILD_PHONE
import com.example.my.telegram.utils.CHILD_USERNAME
import com.example.my.telegram.utils.NODE_PHONES
import com.example.my.telegram.utils.NODE_USERS
import com.example.my.telegram.utils.REF_DATABASE_ROOT
import com.example.my.telegram.utils.replaceActivity
import com.example.my.telegram.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment(private val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {


    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentEnterCodeBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).title = phoneNumber
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            val string = binding.registerInputCode.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    private fun enterCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid.toString()
                    val dateMap = mutableMapOf<String, Any>()
                    CHILD_ID to uid
                    CHILD_PHONE to phoneNumber
                    CHILD_USERNAME to uid

                    REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                        .addOnFailureListener { showToast(it.message.toString()) }
                        .addOnSuccessListener {
                            REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                .addOnSuccessListener {
                                    showToast("Добро пожаловать!")
                                    (activity as RegisterActivity).replaceActivity(MainActivity())
                                }
                                .addOnFailureListener { showToast(it.message.toString()) }
                        }
                } else showToast(task.exception?.message.toString())
            }
    }

}