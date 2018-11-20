package com.example.essam.firebaserecycleviewpagination.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.essam.firebaserecycleviewpagination.Model.User
import com.example.essam.firebaserecycleviewpagination.R
import java.util.ArrayList

class UserAdapter(internal var context: Context) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var userList: MutableList<User>

    val lastItemId: String?
        get() = userList[userList.size - 1].id

    fun addAll(newUser: List<User>) {
        var begin = userList.size
        userList.addAll(newUser)
        notifyItemRangeChanged(begin, newUser.size)
    }

    fun removeLastItem() {
        userList.removeAt(userList.size - 1)
    }

    init {
        this.userList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user = userList[position]
        holder.name.text = user.name!!
        holder.email.text = user.email!!

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var email: TextView

        init {
            name = itemView.findViewById(R.id.name) as TextView
            email = itemView.findViewById(R.id.email) as TextView
        }

    }
}