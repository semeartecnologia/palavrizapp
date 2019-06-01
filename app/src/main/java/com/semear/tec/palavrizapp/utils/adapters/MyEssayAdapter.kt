package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.StatusEssay
import com.semear.tec.palavrizapp.utils.extensions.inflate
import kotlinx.android.synthetic.main.item_my_esay.view.*

class MyEssayAdapter : RecyclerView.Adapter<MyEssayAdapter.ViewHolder>() {

    var context: Context? = null

    var essayList: ArrayList<Essay> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyEssayAdapter.ViewHolder {
        val view = inflate(R.layout.item_my_esay, p0)
        context = p0.context
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return essayList.size
    }

    override fun onBindViewHolder(holder: MyEssayAdapter.ViewHolder, index: Int) {
        holder.essayTitle = essayList[index].title
        holder.essayTheme = essayList[index].theme

        val status = essayList[index].status
        when (status) {
            StatusEssay.UPLOADED -> holder.essayStatus = context?.getString(R.string.upload_status_waiting)
            StatusEssay.CORRECTING -> holder.essayStatus = context?.getString(R.string.upload_status_correcting)
            StatusEssay.FEEDBACK_READY -> holder.essayStatus = context?.getString(R.string.upload_status_done)
        }
    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {

            }
        }

        var essayTitle: String? = null
            set(value) {
                field = value
                view.tv_essay_title.text = value
            }

        var essayTheme: String? = null
            set(value) {
                field = value
                view.tv_essay_theme.text = value
            }

        var essayStatus: String? = null
            set(value) {
                field = value
                view.tv_essay_status.text = value
            }
    }
}