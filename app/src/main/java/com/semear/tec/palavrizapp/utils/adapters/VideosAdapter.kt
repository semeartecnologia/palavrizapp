package com.semear.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.commons.ItemDragCallback
import com.semear.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class VideosAdapter(var listener: OnVideoEvent) : RecyclerView.Adapter<VideosAdapter.ViewHolder>(), ItemDragCallback.ItemTouchHelperContract {

    var listVideos: ArrayList<Video> = ArrayList()
    var listVideosBackup: ArrayList<Video> = ArrayList()
    private lateinit var ctx: Context
    private var videoRepository: VideoRepository? = null

    fun addAllVideo(v: ArrayList<Video>) {
        this.listVideos.clear()
        v.forEach {
            it.orderVideo = this.listVideos.size.toString()
            this.listVideos.add(it)
        }
        this.listVideosBackup.clear()
        this.listVideosBackup.addAll(listVideos)

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

    fun refreshOrder(onCompletion: () -> Unit){
        var i = 0
        listVideos.forEach {
            it.orderVideo = i.toString()
            i++
        }
        listVideosBackup.clear()
        listVideosBackup.addAll(listVideos)
        onCompletion()

    }


    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(listVideos, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(listVideos, i, i - 1)
            }
        }
        listener.onVideoMoved()
        notifyItemMoved(fromPosition, toPosition)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val video = this.listVideos[i]

        viewHolder.title.text = video.title
        viewHolder.description.text = video.description
        viewHolder.videoPath = video.path
        viewHolder.videoKey = video.videoKey
        viewHolder.structure.text = video.structure
        viewHolder.concept.text = video.concept
        viewHolder.theme.text = video.themeName

        videoRepository?.getThumnailDownloadUrl(video.videoThumb ?: "") {
            if (!it.isBlank()) {
                Picasso.get().load(it).into(viewHolder.videoThumb)
            }
        }

        viewHolder.itemView.setOnClickListener {
            listener.onVideoClicked(video)
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
        var structure: TextView
        var concept: TextView
        var theme: TextView
        internal var videoThumb: ImageView

        init {
            title = itemView.findViewById(R.id.video_title)
            description = itemView.findViewById(R.id.video_description)
            videoThumb = itemView.findViewById(R.id.video_thumbnail)
            structure = itemView.findViewById(R.id.tv_structure_video)
            concept = itemView.findViewById(R.id.tv_concept_video)
            theme = itemView.findViewById(R.id.tv_theme_video)
        }
    }
}
