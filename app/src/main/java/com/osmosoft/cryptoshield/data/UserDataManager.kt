package com.osmosoft.cryptoshield.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.osmosoft.cryptoshield.common.SingletonHolder

private const val TAG = "user_data_manager_tag"
private const val USER_DATA_SHARED_PREF = "user_data_shared_pref"
private const val USER_DATA_SET = "user_data_set"

class UserDataManager private constructor(private val context: Context){

    //allows use singleton
    companion object : SingletonHolder<UserDataManager,Context>(::UserDataManager)

    fun init(): UserDataManager{
        Log.d(TAG, "init: ${getUserDataList()}")
        return UserDataManager(context)
    }

    //add new item to the shared pref
    fun add(userData: String){
        Log.d(TAG, "add")
        val set = getUserDataSet()
        set.add(userData)
        getSharedPref().edit()
                .putStringSet(USER_DATA_SET,set)
                .apply()
    }

    //remove item from shared pref
    fun remove(userData: String){
        Log.d(TAG, "remove")
        val set = getUserDataSet()
        set.remove(userData)
        getSharedPref().edit()
                .putStringSet(USER_DATA_SET,set)
                .apply()
    }

    fun getUserDataList(): MutableList<String>?{
        return getUserDataSet().toMutableList()
    }

    //return raw user data set
    fun getUserDataSet(): MutableSet<String> {
        var set = getSharedPref().getStringSet(USER_DATA_SET,null)
        //if set in shared pref is null, return empty set
        return set ?: mutableSetOf()

    }

    //return local shared preferences
    private fun getSharedPref(): SharedPreferences{
        Log.d(TAG, "getSharedPref")
        return context.getSharedPreferences(
                USER_DATA_SHARED_PREF,
                Context.MODE_PRIVATE
        )
    }

}

