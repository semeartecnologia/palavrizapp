package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnThemeClicked
import kotlinx.android.synthetic.main.item_essay_theme_chooser.view.*

class ThemesListPickerAdapter(val listener: OnThemeClicked) : RecyclerView.Adapter<ThemesListPickerAdapter.ViewHolder>() {


    var context: Context? = null
    var themesList: ArrayList<Themes> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ThemesListPickerAdapter.ViewHolder {
        val view = inflate(R.layout.item_essay_theme_chooser, p0)
        context = p0.context
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return themesList.size
    }

    override fun onBindViewHolder(holder: ThemesListPickerAdapter.ViewHolder, index: Int) {
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