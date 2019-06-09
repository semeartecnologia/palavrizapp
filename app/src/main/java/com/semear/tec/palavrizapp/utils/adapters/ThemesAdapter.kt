package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository
import com.squareup.picasso.Picasso

import java.util.ArrayList

import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_KEY

class ThemesAdapter : RecyclerView.Adapter<ThemesAdapter.ViewHolder>() {

    var listVideos: ArrayList<Video> = ArrayList()
    private lateinit var ctx: Context
    private var videoRepository: VideoRepository? = null

    fun addAllVideo(v: ArrayList<Video>) {
        this.listVideos.clear()
        this.listVideos.addAll(v)
        this.notifyDataSetChanged()
    }

    fun clearVideoList() {
        this.listVideos.clear()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        this.ctx = viewGroup.context
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_video_class, viewGroup, false)
        videoRepository = VideoRepository(ctx)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val video = this.listVideos[i]

        viewHolder.title.text = video.title
        viewHolder.description.text = video.description

        viewHolder.videoPath = video.path
        viewHolder.videoKey = video.videoKey

        videoRepository?.getThumnailDownloadUrl(video.videoThumb ?: "") {
            if (!it.isBlank()) {
                Picasso.get().load(it).into(viewHolder.videoThumb)
            }
        }



    }

    override fun getItemCount(): Int {
        return this.listVideos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var id: Int = 0
        var title: TextView
        var description: TextView
        var videoPath: String? = null
        var videoKey: String? = null
        internal var videoThumb: ImageView


        init {

            title = itemView.findViewById(R.id.video_title)
            description = itemView.findViewById(R.id.video_description)
            videoThumb = itemView.findViewById(R.id.video_thumbnail)

            itemView.setOnClickListener { v ->
                val it = Intent(ctx, ClassroomActivity::class.java)
                it.putExtra(EXTRA_COD_VIDEO, videoPath)
                it.putExtra(EXTRA_TITLE_VIDEO, title.text.toString())
                //it.putExtra(EXTRA_SUBTITLE_VIDEO, description.getText().toString());
                it.putExtra(EXTRA_DESCRPTION_VIDEO, description.text)
                it.putExtra(EXTRA_VIDEO_KEY, videoKey)
                ctx?.startActivity(it)
            }


        }


    }
}
