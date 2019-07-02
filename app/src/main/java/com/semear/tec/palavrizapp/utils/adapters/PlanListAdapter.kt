package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.PlanSwitch
import com.semear.tec.palavrizapp.models.Plans
import com.semear.tec.palavrizapp.utils.extensions.inflate
import kotlinx.android.synthetic.main.item_plan_list.view.*

class PlanListAdapter(var listPlans: ArrayList<Plans>? = null) : RecyclerView.Adapter<PlanListAdapter.ViewHolder>() {

    var context: Context? = null
    var planList: ArrayList<PlanSwitch> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PlanListAdapter.ViewHolder {
        val view = inflate(R.layout.item_plan_list, p0)
        context = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    override fun onBindViewHolder(holder: PlanListAdapter.ViewHolder, index: Int) {
        val plan = planList[index]

        holder.planName = plan.plan?.name

        holder.view.check_plan.isChecked = plan.isChecked

        holder.view.check_plan.setOnCheckedChangeListener { buttonView, isChecked ->
            planList[index].isChecked = isChecked
        }

        if (!plan.isEnabled){
            holder.view.check_plan.isEnabled = false
        }

        if ( context != null) {
            if (index % 2 == 0) {
                holder.view.layout_plan_item?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.check_box_item_1))
            }else{
                holder.view.layout_plan_item?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.check_box_item_2))
            }
        }
    }

    fun disableAllCheckboxes(){
        planList.forEach {
            it.isEnabled = false
            notifyDataSetChanged()
        }
    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var planName: String? = null
            set(value) {
                field = value
                view.tv_name_plan?.text = value
            }
    }
}