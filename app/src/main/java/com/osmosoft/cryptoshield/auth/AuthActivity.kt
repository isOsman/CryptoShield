package com.osmosoft.cryptoshield.auth

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.osmosoft.cryptoshield.R
import com.osmosoft.cryptoshield.common.AbstractTextWatcher
import com.osmosoft.cryptoshield.crypto.utils.CryptoUtils

private const val TAG = "AuthActivityTag"

class AuthActivity : AppCompatActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        Log.d(TAG,CryptoUtils().toMD5Hash("hello"))

        passwordEditText = findViewById(R.id.activity_auth_password_edit_text)
        continueButton = findViewById(R.id.activity_auth_continue_button)

        //validate user input
        passwordEditText.addTextChangedListener(object: AbstractTextWatcher(){
            override fun afterTextChanged(s: Editable?) {
                    continueButton.isEnabled = s.toString().isNotEmpty() and s.toString().isNotBlank()

            }
        })

    }

}