package com.semear.tec.palavrizapp.modules.admin.users

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.adapters.ListUserAdapter
import com.semear.tec.palavrizapp.utils.interfaces.OnUserClicked
import kotlinx.android.synthetic.main.list_user_fragment.*


class ListUserFragment : Fragment(), OnUserClicked {

    private lateinit var adapter: ListUserAdapter

    companion object {
        fun newInstance() = ListUserFragment()
    }

    private lateinit var viewModel: ListUserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListUserViewModel::class.java)
        adapter = ListUserAdapter(this)
        setupRecyclerUsers()
        registerObservers()
        viewModel.fetchUserList()
    }

    private fun registerObservers() {
        viewModel.listUser.observe(this, Observer {
            if (it != null) {
                progress_loading_users.visibility = View.GONE
                adapter.userList = it
            }
        })
    }

    override fun onUserClicked(user: User) {
        Commons.createEditUserAdminDialog(activity as Activity
                ,
                user
        ) { newUser ->
            viewModel.setUserType(newUser.userId, newUser.userType){
                Commons.showAlert(activity as Activity, getString(R.string.sucess_edit_user_title), getString(R.string.sucess_edit_user_text), "OK")
            }
        }
    }

    private fun setupRecyclerUsers() {
        recycler_users?.layoutManager = LinearLayoutManager(context)
        recycler_users?.adapter = adapter
    }


}
