package com.example.telegram.ui.fragments.register

import android.R.attr
import androidx.fragment.app.Fragment

import com.example.telegram.R
import com.example.telegram.database.AUTH
import com.example.telegram.utilits.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import java.util.concurrent.TimeUnit
import android.R.attr.phoneNumber
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.PhoneAuthOptions





class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()

        mAuth = FirebaseAuth.getInstance()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать")
                        restartActivity()
                    } else showToast(task.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(
                    EnterCodeFragment(
                        mPhoneNumber,
                        id
                    )
                )
            }
        }
        register_btn_next.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (register_input_phone_number.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = register_input_phone_number.text.toString()
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(mPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(APP_ACTIVITY)
            .setCallbacks(mCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
