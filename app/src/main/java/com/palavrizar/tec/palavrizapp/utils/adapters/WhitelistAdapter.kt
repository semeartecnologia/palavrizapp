package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.EmailWhitelist
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnRemoveWhitelist
import kotlinx.android.synthetic.main.item_location_limit.view.*

class WhitelistAdapter(val listener: OnRemoveWhitelist) : RecyclerView.Adapter<WhitelistAdapter.ViewHolder>()  {

    var context: Context? = null
    var emailWhitelist: ArrayList<EmailWhitelist> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addWhitelist(email: EmailWhitelist){
        emailWhitelist.add(0, email)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflate(R.layout.item_location_limit, p0)
        context = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return emailWhitelist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        val email = emailWhitelist[index]

        holder.cityName = email.email
        holder.stateName = ""

        holder.ivRemove.setOnClickListener {
            listener.onRemoveClicked(email, index)
            emailWhitelist.remove(email)
            notifyDataSetChanged()
        }

    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var ivRemove: ImageView = view.findViewById(R.id.iv_remove_city)

        var cityName: String? = null
            set(value) {
                field = value
                view.tv_city_name?.text = value
            }

        var stateName: String? = null
            set(value) {
                field = value
                view.tv_state_name?.text = value
            }

    }
}