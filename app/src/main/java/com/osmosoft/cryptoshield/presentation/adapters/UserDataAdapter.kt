package com.osmosoft.cryptoshield.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.osmosoft.cryptoshield.R
import com.osmosoft.cryptoshield.data.UserDataConst

class UserDataAdapter :
    RecyclerView.Adapter<UserDataAdapter.ViewHolder>(){

    private lateinit var userDataSet: MutableSet<String>

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val resourceTextView: TextView = view.findViewById(R.id.user_data_resource_text_view)
        val passwordTextView: TextView = view.findViewById(R.id.user_data_password_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_data_list_item,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //split one line to the two lines
        val lines = userDataSet.elementAt(position).split(UserDataConst.lineDivider)
        val resource = lines[0]
        val password = lines[1]

        holder.resourceTextView.text = resource
        holder.passwordTextView.text = password
    }

    override fun getItemCount(): Int {
        return userDataSet.size
    }

    fun setUserDataSet(userDataSet: Set<String>){
        this.userDataSet = mutableSetOf()
        this.userDataSet.clear()
        this.userDataSet.addAll(userDataSet)
        notifyDataSetChanged()
    }

    //return used userDataSet
    fun getUserDataSet() : MutableSet<String> = userDataSet

    //add user data to the dataset
    fun addUserDataItem(userData: String){
        userDataSet.add(userData)
        notifyDataSetChanged()
    }
}