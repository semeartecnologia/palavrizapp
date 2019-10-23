package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.utils.commons.ItemDragCallback
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnVideoSearched
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class VideosAdapter(var listener: OnVideoEvent) : RecyclerView.Adapter<VideosAdapter.ViewHolder>(), ItemDragCallback.ItemTouchHelperContract {

    var listVideos: ArrayList<Video> = ArrayList()
    var listVideosBackup: ArrayList<Video> = ArrayList()
    private lateinit var ctx: Context
    private var videoRepository: VideoRepository? = null
    private var jsonProgress: JSONObject? = null

    fun setVideoSearched(listVideos: ArrayList<Video>, jsonProgress: JSONObject? = null){
        this.listVideos.clear()
        listVideos.forEach {
            if (jsonProgress != null){
                try{
                    val getValue = jsonProgress.get(it.videoKey)
                    if (getValue != null){
                        val currentProgress = BigDecimal.valueOf(jsonProgress.getDouble(it.videoKey)).toFloat()
                        it.quantVideoWached = currentProgress.toInt()
                    }else{
                        it.quantVideoWached = 0
                    }
                }catch (e: Exception){

                }
            }
            this.listVideos.add(it)
        }
        this.notifyDataSetChanged()
    }

    fun addAllVideo(v: ArrayList<Video>, jsonProgress: JSONObject? = null) {
        this.listVideos.clear()
//        listVideosWatchedAlready.clear()
        v.forEach {
            if (jsonProgress != null){
                try{
                    val getValue = jsonProgress.get(it.videoKey)
                    if (getValue != null){
                        val currentProgress = BigDecimal.valueOf(jsonProgress.getDouble(it.videoKey)).toFloat()
                        it.quantVideoWached = currentProgress.toInt()
                    }else{
                        it.quantVideoWached = 0
                    }
                }catch (e: Exception){

                }
            }
            it.orderVideo = this.listVideos.size.toString()
            this.listVideos.add(it)



        }
        this.listVideosBackup.clear()
        this.listVideosBackup.addAll(listVideos)


        this.jsonProgress = jsonProgress

        this.notifyDataSetChanged()
    }

    fun getIndexToScroll(): Int {
        this.listVideos.forEachIndexed { index, video ->
            if (video.quantVideoWached < 100){
                return index
            }
        }
        return 0
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

    fun filter(text: String, onVideoSearched: OnVideoSearched) {
        val arrayVideos = arrayListOf<Video>()

        if (text.isEmpty()) {
            arrayVideos.addAll(listVideosBackup)
        } else {
            val mText = text.toLowerCase()
            for (video in listVideosBackup) {

                if (video.title.contains(text) || video.description.contains(mText) || video.themeName?.contains(mText) == true || video.concept?.contains(mText) == true
                        || video.structure?.contains(mText) == true) {
                    arrayVideos.add(video)
                }

            }
        }
        onVideoSearched.onVideosSearch(arrayVideos)
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

        if (!video.structure.isNullOrBlank()){
            viewHolder.title.setTextColor(ContextCompat.getColor(this.ctx, R.color.lightGreen))
        }else if (!video.concept.isNullOrBlank()){
            viewHolder.title.setTextColor(ContextCompat.getColor(this.ctx, R.color.colorSecondaryDark))
        }else if (!video.themeName.isNullOrBlank()){
            viewHolder.title.setTextColor(ContextCompat.getColor(this.ctx, R.color.colorPrimary))
        }else{
            viewHolder.title.setTextColor(ContextCompat.getColor(this.ctx, R.color.colorPrimary))
        }

        if (jsonProgress != null){
            try{
                val getValue = jsonProgress?.get(video.videoKey)
                if (getValue != null){
                    val currentProgress = BigDecimal.valueOf(jsonProgress?.getDouble(video.videoKey) ?: 0.toDouble()).toFloat()
                    viewHolder.progress.progress = currentProgress.toInt()
                }else{
                    viewHolder.progress.progress = 0
                }
            }catch (e: Exception){
                viewHolder.progress.progress = 0
            }

        }

        if (viewHolder.progress.progress > 0){
            viewHolder.progress.visibility = View.VISIBLE
        }else{
            viewHolder.progress.visibility = View.GONE
        }

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
        var progress: ProgressBar
        internal var videoThumb: ImageView

        init {
            title = itemView.findViewById(R.id.video_title)
            description = itemView.findViewById(R.id.video_description)
            videoThumb = itemView.findViewById(R.id.video_thumbnail)
            progress = itemView.findViewById(R.id.progress_video_watched)
            progress.progressDrawable.setColorFilter(
                    ContextCompat.getColor(ctx, R.color.searchLabel), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}
