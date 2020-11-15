package com.osmosoft.cryptoshield.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.osmosoft.cryptoshield.R
import com.osmosoft.cryptoshield.common.AbstractTextWatcher
import com.osmosoft.cryptoshield.crypto.storage.KeyHelper
import com.osmosoft.cryptoshield.crypto.utils.CryptoUtils
import com.osmosoft.cryptoshield.data.UserAuthManager
import com.osmosoft.cryptoshield.presentation.UserDataActivity

private const val TAG = "AuthActivityTag"

class AuthActivity : AppCompatActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        KeyHelper.init(this)

        passwordEditText = findViewById(R.id.activity_auth_password_edit_text)
        continueButton = findViewById(R.id.activity_auth_continue_button)

        //validate user input
        passwordEditText.addTextChangedListener(object: AbstractTextWatcher(){
            override fun afterTextChanged(s: Editable?) {
                    continueButton.isEnabled = s.toString().isNotEmpty() and s.toString().isNotBlank()
            }
        })

        continueButton.setOnClickListener {
            val userInput = passwordEditText.text.toString()
            val hashedUserInput = CryptoUtils.toMD5Hash(userInput)
            //user auth
            val auth = UserAuthManager.getInstance(this).auth(hashedUserInput)
            if(!auth){
                //if user is not authenticated - show message
                Toast.makeText(this@AuthActivity,getString(R.string.wrong_password),Toast.LENGTH_LONG).show()
            }else{
                navigateToUserDataActivity()
            }
        }
    }

    private fun navigateToUserDataActivity(){
        val intent = Intent(this,UserDataActivity::class.java)
        startActivity(intent)
    }
}