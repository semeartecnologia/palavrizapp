package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.utils.extensions.inflate
import com.semear.tec.palavrizapp.utils.interfaces.OnThemeClicked
import kotlinx.android.synthetic.main.item_essay_theme_list.view.*

class ThemesListAdapter(val listener: OnThemeClicked) : RecyclerView.Adapter<ThemesListAdapter.ViewHolder>() {


    var context: Context? = null
    var themesList: ArrayList<Themes> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ThemesListAdapter.ViewHolder {
        val view = inflate(R.layout.item_essay_theme_list, p0)
        context = p0.context
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return themesList.size
    }

    override fun onBindViewHolder(holder: ThemesListAdapter.ViewHolder, index: Int) {
        val theme = themesList[index]

        if (theme.urlPdf.isNullOrBlank()){
            holder.view.pdf_button.visibility = View.GONE
        }else{
            holder.view.pdf_button.visibility = View.VISIBLE
        }

        holder.essayTitle = theme.themeName

        holder.view.setOnClickListener {
            listener.onThemeClicked(theme)
        }

        holder.view.pdf_button.setOnClickListener {
            listener.onDownloadPdfClicked(theme.urlPdf ?: "")
        }

    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var essayTitle: String? = null
            set(value) {
                field = value
                view.tv_theme_name?.text = value
            }
    }
}