package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnEssayClicked
import kotlinx.android.synthetic.main.item_essay_list.view.*

class EssayListAdapter(val listener: OnEssayClicked) : RecyclerView.Adapter<EssayListAdapter.ViewHolder>() {

    var context: Context? = null
    var essayList: ArrayList<Essay> = arrayListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EssayListAdapter.ViewHolder {
        val view = inflate(R.layout.item_essay_list, p0)
        context = p0.context
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return essayList.size
    }

    override fun onBindViewHolder(holder: EssayListAdapter.ViewHolder, index: Int) {
        val essay = essayList[index]

        holder.essayTitle = essay.theme
        holder.essayAuthor = essay.author?.fullname ?: context?.getString(R.string.user_not_found)
        holder.essayDate = essay.postDate
        when{
            essay.status == StatusEssay.UPLOADED -> holder.essayStatus = context?.getString(R.string.upload_status_waiting)
            essay.status == StatusEssay.CORRECTING -> holder.essayStatus = context?.getString(R.string.upload_status_correcting)
            essay.status == StatusEssay.FEEDBACK_READY -> holder.essayStatus = context?.getString(R.string.upload_status_done)
            essay.status == StatusEssay.NOT_READABLE -> holder.essayStatus = context?.getString(R.string.upload_status_not_readable)
        }

        holder.view.setOnClickListener {
            listener.onEssayClicked(essay)
        }

    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var essayTitle: String? = null
            set(value) {
                field = value
                view.tv_essay_title.text = value
            }

        var essayAuthor: String? = null
            set(value) {
                field = value
                view.tv_essay_author.text = String.format(view.context.getString(R.string.author), value)
            }

        var essayDate: String? = null
            set(value) {
                field = value
                view.tv_essay_date.text = String.format(view.context.getString(R.string.post_date), value)
            }

        var essayStatus: String? = null
            set(value) {
                field = value
                view.tv_essay_status.text = value
            }
    }
}