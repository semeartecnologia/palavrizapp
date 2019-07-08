package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.SkuDetails
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.PlanDetails
import com.semear.tec.palavrizapp.utils.extensions.inflate
import com.semear.tec.palavrizapp.utils.interfaces.OnPlanClicked
import kotlinx.android.synthetic.main.item_plan_billing.view.*

class ListPlansAdapter(var listener: OnPlanClicked)  : RecyclerView.Adapter<ListPlansAdapter.ViewHolder>()  {

    var context: Context? = null
    var plansList: ArrayList<SkuDetails> = arrayListOf()
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
        holder.planPrice = plan.price


        holder.view.setOnClickListener {
            listener.onPlanClicked(plan)
        }

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