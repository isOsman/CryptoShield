package com.osmosoft.cryptoshield.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.osmosoft.cryptoshield.R
import com.osmosoft.cryptoshield.presentation.adapters.UserDataAdapter

class UserDataActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userDataAdapter: UserDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        val userDataset: Set<String> = setOf("res1#|#pass","res2#|#pass","res3#|#pass","res4#|#pass"
        ,"res5#|#pass","res6#|#pass","res7#|#pass","res8#|#pass","res9#|#pass","res10#|#pass")

        userDataAdapter = UserDataAdapter()
        userDataAdapter.setUserDataSet(userDataset)

        recyclerView = findViewById(R.id.activity_user_data_recycler_view)
        recyclerView.adapter = userDataAdapter

    }
}