package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.PlanDetails
import com.semear.tec.palavrizapp.utils.extensions.inflate
import kotlinx.android.synthetic.main.item_plan_billing.view.*

class ListPlansAdapter  : RecyclerView.Adapter<ListPlansAdapter.ViewHolder>()  {

    var context: Context? = null
    var plansList: ArrayList<PlanDetails> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListPlansAdapter.ViewHolder {
        val view = inflate(R.layout.item_plan_billing, p0)
        context = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return plansList.size
    }

    override fun onBindViewHolder(holder: ListPlansAdapter.ViewHolder, index: Int) {
        val plan = plansList[holder.adapterPosition]

        holder.planTitle = plan.title
        holder.planDescription = plan.description
        holder.planId = plan.plan_id
        holder.planPrice = plan.price
     /*   holder.userName = user.fullname
        holder.userEmail = user.email
        if (!user.photoUri.isNullOrBlank()) {
            Picasso.get().load(user.photoUri).into(holder.view.photo_user)
        }else{
            Picasso.get().load(R.drawable.avatar_man_512).into(holder.view.photo_user)
        }

        holder.userType = String.format(context?.getString(R.string.user_type_label) ?: "Tipo: %s", user.userType.name)

        if (user.registerDate != null){
            holder.userRegisterDate = String.format(context?.getString(R.string.register_date_label) ?: "", DateFormatHelper.stringDate(user.registerDate))
        }else{
            holder.userRegisterDate = ""
        }

        holder.userPlan = user.plan.getPlanTitle(context)

        holder.view.setOnClickListener {
            listener.onUserClicked(user)
        }*/

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }




    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var planId: String? = null

        var planTitle: String? = null
            set(value) {
                field = value
                view.tv_plan_title?.text = value
            }

            var planDescription: String? = null
            set(value) {
                field = value
                view.tv_plan_description?.text = value
            }

            var planPrice: String? = null
            set(value) {
                field = value
                view.tv_plan_text?.text = value
            }


    }
}