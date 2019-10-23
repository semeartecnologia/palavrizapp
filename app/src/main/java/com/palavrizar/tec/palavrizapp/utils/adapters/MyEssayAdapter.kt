package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.EssayViewActivity
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnUnreadableClicked
import kotlinx.android.synthetic.main.item_my_essay.view.*

class MyEssayAdapter(val listener: OnUnreadableClicked) : RecyclerView.Adapter<MyEssayAdapter.ViewHolder>() {

    var context: Context? = null

    var essayList: ArrayList<Essay> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflate(R.layout.item_my_essay, p0)
        context = p0.context
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return essayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        holder.essayTitle = essayList[index].theme

        val status = essayList[index].status
        holder.essayStatus?.setBackgroundResource(R.drawable.text_dark_purple_backgorund)
        when (status) {
            StatusEssay.UPLOADED -> holder.essayStatus?.text = context?.getString(R.string.upload_status_waiting)
            StatusEssay.CORRECTING -> holder.essayStatus?.text = context?.getString(R.string.upload_status_correcting)
            StatusEssay.FEEDBACK_READY -> holder.essayStatus?.text = context?.getString(R.string.upload_status_done)
            StatusEssay.NOT_READABLE -> {
                holder.essayStatus?.text = context?.getString(R.string.upload_status_not_readable)
                    holder.essayStatus?.setBackgroundResource(R.drawable.text_red_background)
            }
        }

        holder.view.setOnClickListener {
            startEssayActivity(essayList[index])
        }
    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var essayStatus: TextView? = null

        init{
            essayStatus = view.findViewById(R.id.tv_essay_status)
        }

        var essayTitle: String? = null
            set(value) {
                field = value
                view.tv_essay_title.text = value
            }

    }

    private fun startEssayActivity(essay: Essay){
        if (essay.status == StatusEssay.NOT_READABLE){
            listener.onUnreadableClicked(essay)
        }else {
            val it = Intent(context, EssayViewActivity::class.java)
            it.putExtra(Constants.EXTRA_ESSAY, essay)
            it.putExtra(Constants.EXTRA_ESSAY_READ_MODE, true)
            context?.startActivity(it)
        }
    }
}