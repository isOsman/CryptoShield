package com.osmosoft.cryptoshield.presentation

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
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


        val userDataSet = UserDataManager.getInstance(this).getUserDataSet()

        userDataAdapter = UserDataAdapter()
        userDataAdapter.setUserDataSet(userDataSet)

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
                    KeyHelper.init(this)
                    val encData = KeyHelper.encrypt(this,data)
                    userDataAdapter.addUserDataItem(encData)
                }
            }

            builder.setNegativeButton("Cancel"){
                    dialog, which -> dialog.cancel()
            }

            builder.show()


        }

    }
}