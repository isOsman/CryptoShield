package com.osmosoft.cryptoshield.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.osmosoft.cryptoshield.common.SingletonHolder
import com.osmosoft.cryptoshield.crypto.utils.CryptoUtils

private const val TAG = "UserAuthTAG"

private const val USER_AUTH_SHARED_PREF = "user_auth_shared_pref"
private const val USER_AUTH_PASS_KEY = "user_auth_pass_key"

class UserAuthManager private constructor(private val context: Context){

    //allows use singleton
    companion object: SingletonHolder<UserAuthManager,Context>(::UserAuthManager)


    //auth user
    fun auth(password: String): Boolean{
        //check pass if exists
        return if(userHasPassword()){
            passwordIsValid(password)
        }else{
            //else savePass
            setPassword(password)
            true
        }
    }

    private fun setPassword(password: String){
        //transform pass to hash
        val hashPass = CryptoUtils.toMD5Hash(password)
        //save pass hash
        getSharedPref().edit()
                .putString(USER_AUTH_PASS_KEY,hashPass)
                .apply()
    }

    //check password is valid
    private fun passwordIsValid(password: String): Boolean{
        return if(userHasPassword()){
            val savedPass = getSharedPref().getString(USER_AUTH_PASS_KEY,null)
            val checkPass = CryptoUtils.toMD5Hash(password)
            checkPass == savedPass
        }else{
            false
        }
    }

    //check for password
    private fun userHasPassword(): Boolean{
        return getSharedPref().contains(USER_AUTH_PASS_KEY)
    }

    //return local shared preferences
    private fun getSharedPref(): SharedPreferences {
        Log.d(TAG, "getSharedPref")
        return context.getSharedPreferences(
                USER_AUTH_SHARED_PREF,
                Context.MODE_PRIVATE
        )
    }
}