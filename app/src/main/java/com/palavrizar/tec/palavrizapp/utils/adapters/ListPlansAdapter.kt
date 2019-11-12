    package com.palavrizar.tec.palavrizapp.utils.adapters

    import android.content.Context
    import android.support.v7.widget.RecyclerView
    import android.view.View
    import android.view.ViewGroup
    import android.widget.LinearLayout
    import com.android.billingclient.api.SkuDetails
    import com.palavrizar.tec.palavrizapp.R
    import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
    import com.palavrizar.tec.palavrizapp.utils.interfaces.OnPlanClicked
    import kotlinx.android.synthetic.main.item_plan_billing.view.*

    class ListPlansAdapter(var listener: OnPlanClicked)  : RecyclerView.Adapter<ListPlansAdapter.ViewHolder>()  {

        var context: Context? = null
        var skuSelectd: String? = null
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

            if (skuSelectd != null && skuSelectd == plan.sku){
                holder.planRoot?.alpha = 0.3f
            }else{
                holder.planRoot?.alpha = 1.0f
            }


            holder.planTitle = plan.title.replace("(Palavrizar)", "")
            holder.planDescription = plan.description
            holder.planPrice = plan.price

            when(plan.subscriptionPeriod){
                "P1W" ->  holder.planPeriod = context?.getString(R.string.p1w)
                "P1M" ->  holder.planPeriod = context?.getString(R.string.p1m)
                "P3M" ->  holder.planPeriod = context?.getString(R.string.p3m)
                "P6M" ->  holder.planPeriod = context?.getString(R.string.p6m)
                "P1Y" ->  holder.planPeriod = context?.getString(R.string.p1y)

            }


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

            var planRoot: LinearLayout? = null

            init{
                planRoot = view.findViewById(R.id.rootPlanLayout)
            }


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

            var planPeriod:  String? = null
                set(value) {
                    field = value
                    view.tv_plan_period?.text = value
                }

        }
    }