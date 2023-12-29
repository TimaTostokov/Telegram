package com.example.my.telegram.ui.screens.register

import androidx.fragment.app.Fragment
import com.example.my.telegram.R
import com.example.my.telegram.database.AUTH
import com.example.my.telegram.database.CHILD_ID
import com.example.my.telegram.database.CHILD_PHONE
import com.example.my.telegram.database.CHILD_USERNAME
import com.example.my.telegram.database.NODE_PHONES
import com.example.my.telegram.database.NODE_USERS
import com.example.my.telegram.database.REF_DATABASE_ROOT
import com.example.my.telegram.databinding.FragmentEnterCodeBinding
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AppTextWatcher
import com.example.my.telegram.utils.AppValueEventListener
import com.example.my.telegram.utils.restartActivity
import com.example.my.telegram.utils.showToast
import com.google.firebase.auth.PhoneAuthProvider

/* Фрагмент для ввода кода подтверждения при регистрации */

class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentEnterCodeBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            val string = binding.registerInputCode.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    private fun enterCode() {
        /* Функция проверяет код, если все нормально, производит создания информации о пользователе в базе данных.*/
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener {

                        if (!it.hasChild(CHILD_USERNAME)) {
                            dateMap[CHILD_USERNAME] = uid
                        }

                        REF_DATABASE_ROOT.child(
                            NODE_PHONES
                        ).child(phoneNumber).setValue(uid)
                            .addOnFailureListener { showToast(it.message.toString()) }
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(
                                    NODE_USERS
                                ).child(uid).updateChildren(dateMap)
                                    .addOnSuccessListener {
                                        showToast("Добро пожаловать")
                                        restartActivity()
                                    }
                                    .addOnFailureListener { showToast(it.message.toString()) }
                            }
                    })
            } else showToast(task.exception?.message.toString())
        }
    }

}