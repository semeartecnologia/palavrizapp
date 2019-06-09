package com.semear.tec.palavrizapp.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.Video;
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity;

import java.util.ArrayList;
import java.util.List;

import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_KEY;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ViewHolder> {

    List<Video> listVideos;
    Context ctx;

    public ThemesAdapter(){
        this.listVideos = new ArrayList<>();
    }

    public void addAllVideo(ArrayList<Video> v){
        this.listVideos.clear();
        this.listVideos.addAll(v);
        this.notifyDataSetChanged();
    }

    public void clearVideoList(){
        this.listVideos.clear();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.ctx = viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_video_class, viewGroup, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Video video = this.listVideos.get(i);

        viewHolder.title.setText(video.getTitle());
        viewHolder.description.setText(video.getDescription());

        viewHolder.videoPath = video.getPath();
        viewHolder.videoKey = video.getVideoKey();


    }

    @Override
    public int getItemCount() {
        return this.listVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public int id;
        public TextView title;
        public TextView description;
        public String videoPath;
        public String videoKey;
        ImageView videoThumb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.video_title);
            description = itemView.findViewById(R.id.video_description);
            videoThumb = itemView.findViewById(R.id.video_thumbnail);

            itemView.setOnClickListener(v -> {
                Intent it = new Intent(ctx, ClassroomActivity.class);
                it.putExtra(EXTRA_COD_VIDEO, videoPath);
                it.putExtra(EXTRA_TITLE_VIDEO, title.getText().toString());
                //it.putExtra(EXTRA_SUBTITLE_VIDEO, description.getText().toString());
                it.putExtra(EXTRA_DESCRPTION_VIDEO, description.getText());
                it.putExtra(EXTRA_VIDEO_KEY, videoKey);
                ctx.startActivity(it);
            });


        }


    }
}
