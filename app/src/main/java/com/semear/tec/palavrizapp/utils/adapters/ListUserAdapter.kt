package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.utils.extensions.inflate
import com.semear.tec.palavrizapp.utils.interfaces.OnUserClicked
import com.semear.tec.palavrizapp.utils.interfaces.OnUserSearch
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_admin_list.view.*

class ListUserAdapter(val listener: OnUserClicked) : RecyclerView.Adapter<ListUserAdapter.ViewHolder>() {

    var context: Context? = null
    var userListCopy: ArrayList<User>? = arrayListOf()
    var userList: ArrayList<User> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListUserAdapter.ViewHolder {
        val view = inflate(R.layout.item_user_admin_list, p0)
        context = p0.context
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ListUserAdapter.ViewHolder, index: Int) {
        val user = userList[holder.adapterPosition]

        holder.userName = user.fullname
        holder.userEmail = user.email
        if (!user.photoUri.isNullOrBlank()) {
            Picasso.get().load(user.photoUri).into(holder.view.photo_user)
        }else{
            Picasso.get().load(R.drawable.avatar_man_512).into(holder.view.photo_user)
        }

        holder.userType = String.format(context?.getString(R.string.user_type_label) ?: "Tipo: %s", user.userType.name)


        holder.userPlan = user.plan.getPlanTitle(context)

        holder.view.setOnClickListener {
            listener.onUserClicked(user)
        }

    }

    private fun copyUserList() {
        this.userListCopy = arrayListOf()
        this.userListCopy?.clear()
        this.userListCopy?.addAll(userList)

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun filter(text: String, onUserSearch: OnUserSearch) {
        val arrayUsers = arrayListOf<User>()

        if (this.userListCopy?.isEmpty() == true)
            copyUserList()

        if (text.isEmpty()) {
            arrayUsers.addAll(userListCopy!!)
        } else {
            val mText = text.toLowerCase()
            for (user in userListCopy!!) {

                    if (user.fullname.contains(mText) || user.email.contains(mText)) {
                        arrayUsers.add(user)
                    }

            }
        }
        onUserSearch.onUsersSearch(arrayUsers)


    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var userName: String? = null
            set(value) {
                field = value
                view.tv_user_name?.text = value
            }

        var userEmail: String? = null
            set(value) {
                field = value
                view.tv_user_email?.text = value
            }

        var userType: String? = null
            set(value) {
                field = value
                view.tv_user_type?.text = value
            }

        var userPlan: String? = null
            set(value) {
                field = value
                view.tv_user_plan?.text = value
            }
    }
}