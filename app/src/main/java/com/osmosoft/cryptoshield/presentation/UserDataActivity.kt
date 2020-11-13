package com.osmosoft.cryptoshield.presentation

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.osmosoft.cryptoshield.R
import com.osmosoft.cryptoshield.crypto.storage.KeyHelper
import com.osmosoft.cryptoshield.data.UserDataConst
import com.osmosoft.cryptoshield.data.UserDataManager
import com.osmosoft.cryptoshield.presentation.adapters.UserDataAdapter
import java.security.Key
import kotlin.random.Random

class UserDataActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userDataAdapter: UserDataAdapter

    private val addButton: ImageButton by lazy {
        findViewById(R.id.activity_user_data_image_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        KeyHelper.init(this)

        val encUserDataSet = UserDataManager.getInstance(this).getUserDataSet()
        val decUserDataSet = getDecUserDataSet(encUserDataSet)

        userDataAdapter = UserDataAdapter()
        userDataAdapter.setUserDataSet(decUserDataSet)

        recyclerView = findViewById(R.id.activity_user_data_recycler_view)
        recyclerView.adapter = userDataAdapter

        addButton.setOnClickListener {
            var resource: String
            var password: String

            var builder: AlertDialog.Builder =
                AlertDialog.Builder(this)

            builder.setTitle("Добавление данных")

            val resourceInput = EditText(this)
            resourceInput.inputType = InputType.TYPE_CLASS_TEXT
            resourceInput.hint = "Введите ресурс"

            val passwordInput = EditText(this)
            passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInput.hint = "Введите пароль"

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(resourceInput)
            linearLayout.addView(passwordInput)
            builder.setView(linearLayout)

            builder.setPositiveButton("OK") {
                    dialog, which ->
                run {
                    resource = resourceInput.text.toString()
                    password = passwordInput.text.toString()

                    val data = resource + UserDataConst.lineDivider + password
                    Log.d("DATA_Activity", "data: $data")
                    val encData = KeyHelper.encrypt(this,data)
                    Log.d("DATA_Activity", "enc_data: $encData")
                    val decData = KeyHelper.decrypt(this,encData)
                    Log.d("DATA_Activity", "enc_data: $encData")
                    userDataAdapter.addUserDataItem(data)
                }
            }

            builder.setNegativeButton("Cancel"){
                    dialog, which -> dialog.cancel()
            }

            builder.show()


        }

    }

    override fun onStop() {
        saveUserDataSet()
        super.onStop()
    }

    override fun onDestroy() {
        saveUserDataSet()
        super.onDestroy()
    }

    private fun saveUserDataSet(){
        val encSet: MutableSet<String> = mutableSetOf()
        val userDataSet: MutableSet<String> = userDataAdapter.getUserDataSet()
        var encString: String
        for(i in 0 until userDataSet.size){
            encString = KeyHelper.encrypt(this,userDataSet.elementAt(i))
            encSet.add(encString)
        }

        UserDataManager.getInstance(this).saveUserDataSet(encSet)
    }

    private fun getDecUserDataSet(encSet: MutableSet<String>): MutableSet<String>{
        val decSet: MutableSet<String> = mutableSetOf()
        var decString: String
        for(i in 0 until encSet.size){
            decString = KeyHelper.decrypt(this,encSet.elementAt(i))
            decSet.add(decString)
        }

        return decSet
    }
}