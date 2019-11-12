package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.SkuDetails
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnPlanClicked
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnProductClicked
import kotlinx.android.synthetic.main.item_plan_billing.view.*

class ListProductsAdapter(var listener: OnProductClicked)  : RecyclerView.Adapter<ListProductsAdapter.ViewHolder>()  {

    var context: Context? = null
    var plansList: ArrayList<SkuDetails> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListProductsAdapter.ViewHolder {
        val view = inflate(R.layout.item_products, p0)
        context = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return plansList.size
    }

    override fun onBindViewHolder(holder: ListProductsAdapter.ViewHolder, index: Int) {
        val product = plansList[holder.adapterPosition]

        holder.planTitle = product.title.replace("(Palavrizar)", "")
        holder.planDescription = product.description
        holder.planPrice = product.price


        holder.view.setOnClickListener {
            listener.onProductClicked(product)
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