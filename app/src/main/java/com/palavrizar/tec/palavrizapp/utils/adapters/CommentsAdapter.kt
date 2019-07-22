package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Comment
import com.palavrizar.tec.palavrizapp.utils.commons.DateFormatHelper
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnReplyClicked
import kotlinx.android.synthetic.main.item_question.view.*
import kotlinx.android.synthetic.main.item_reply.view.*


class CommentsAdapter(val canReply: Boolean, val listener: OnReplyClicked) :  RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var context: Context? = null
    var commentsList: ArrayList<Comment> = arrayListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CommentsAdapter.ViewHolder {
        val view = inflate(R.layout.item_question, p0)
        context = p0.context

        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return commentsList.size
    }



    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, index: Int) {
        val comments = commentsList[index]

        holder.authorComment = comments.author?.fullname ?: "Anônimo"
        holder.textComment = comments.comment ?: "Comentário não encontrado"

        if (comments.replyCount > 0){
            holder.view.view_replies.visibility = View.VISIBLE
            holder.view.layout_reply.visibility = View.VISIBLE

            holder.view.reply_username.text = comments.listReply?.first()?.author?.fullname
            holder.view.reply_text.text = comments.listReply?.first()?.comment

            if (comments.replyCount > 1) {
                holder.viewReplies = context?.getString(R.string.view_replies, comments.replyCount - 1)

            }else{
                holder.view.view_replies.visibility = View.GONE

            }


            /*val layout = findViewById(R.id.layout) as LinearLayout

            layout.addView(child)*/

        }else{
            holder.view.layout_reply.visibility = View.GONE
            holder.view.view_replies.visibility = View.GONE
        }


        val timePost = comments.time
        if (timePost != null) {
            val timePostAgo = System.currentTimeMillis() - timePost
            holder.timeComment = "- ${DateFormatHelper.formatTimeComment(context, timePostAgo)}"
        }

        holder.btnReply = canReply
        holder.view.btn_reply?.setOnClickListener {
            listener.onReplyClicked(comments.id)
        }



    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var authorComment: String? = null
            set(value){
                field = value
                view.question_username.text = value
            }

        var textComment: String? = null
            set(value){
                field = value
                view.question_text.text = value
            }

        var viewReplies: String? = null
            set(value){
                field = value
                view.view_replies.text = value
            }

        var timeComment: String? = null
            set(value){
                field = value
                view.question_time.text = value
            }

        var btnReply: Boolean? = null
            set(value){
                field = value
                if (value == true){
                    view.btn_reply.visibility = View.VISIBLE
                }else{
                    view.btn_reply.visibility = View.GONE
                }

            }


    }
}