package com.example.my.telegram.ui.screens.register

import androidx.fragment.app.Fragment
import com.example.my.telegram.R
import com.example.my.telegram.database.AUTH
import com.example.my.telegram.databinding.FragmentEnterPhoneNumberBinding
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.replaceFragment
import com.example.my.telegram.utils.restartActivity
import com.example.my.telegram.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

/* Фрагмент для ввода номера телефона при регистрации */

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentEnterPhoneNumberBinding.inflate(layoutInflater)
    }

    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()

        /* Callback который возвращает результат верификации */
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                /* Функция срабатывает если верификация уже была произведена,
                * пользователь авторизируется в приложении без потверждения по смс */
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать")
                        restartActivity()
                    } else showToast(task.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                /* Функция срабатывает если верификация не удалась*/
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                /* Функция срабатывает если верификация впервые, и отправлена смс */
                replaceFragment(
                    EnterCodeFragment(
                        mPhoneNumber,
                        id
                    )
                )
            }
        }
        binding.registerBtnNext.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        /* Функция проверяет поле для ввода номер телефона, если поле пустое выводит сообщение.
         * Если поле не пустое, то начинает процедуру авторизации/ регистрации */
        if (binding.registerInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        /* Инициализация */
        mPhoneNumber = binding.registerInputPhoneNumber.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS,
            APP_ACTIVITY,
            mCallback
        )
    }

}