package com.semear.tec.palavrizapp.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.GroupThemes;
import com.semear.tec.palavrizapp.models.VideoPreview;
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity;

import java.util.ArrayList;
import java.util.List;

import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_SUBTITLE_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ViewHolder> {

    List<GroupThemes> listGroupThemes;
    List<VideoPreview> listVideos;
    Context ctx;

    public ThemesAdapter(){
        this.listGroupThemes = new ArrayList<>();
        this.listVideos = new ArrayList<>();
    }

    public void addVideo(VideoPreview v){
        this.listVideos.add(v);
        this.notifyDataSetChanged();
        Log.d("AULA", v.getMovieTitle());
    }

    public void addThemes(List<GroupThemes> listThemes){
        this.listGroupThemes.clear();

        //adiciona na lista e notifica

        int currentId = -1;
        for (GroupThemes groupThemes : listThemes){

            if (currentId == -1) {
                currentId = groupThemes.getId();
                this.listGroupThemes.add(groupThemes);
            }else{
                if ( groupThemes.getId() != currentId){
                    this.listGroupThemes.add(groupThemes);
                }
                currentId = groupThemes.getId();
            }
        }

        this.notifyDataSetChanged();
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
        final VideoPreview vp = this.listVideos.get(i);

        viewHolder.title.setText(vp.getMovieTitle());
        viewHolder.description.setText(vp.getDescription());
        /**final GroupThemes groupTheme = this.listGroupThemes.get(i);

        //viewHolder.title.setText(plan.getPlanTitle(context));
        viewHolder.groupIdText.setText("Grupo " + groupTheme.getId());
        viewHolder.groupName.setText(groupTheme.getGroupName());
        viewHolder.conceptTheme.setText("Tema conceito: " + groupTheme.getTemaConceito());**/

    }

    @Override
    public int getItemCount() {
        return this.listVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public int id;
        public TextView title;
        public TextView description;
        public String urlyoutube;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.video_title);
            description = itemView.findViewById(R.id.video_description);


            itemView.setOnClickListener(v -> {
                Intent it = new Intent(ctx, ClassroomActivity.class);
                it.putExtra(EXTRA_COD_VIDEO, urlyoutube);
                it.putExtra(EXTRA_TITLE_VIDEO, title.getText().toString());
                it.putExtra(EXTRA_SUBTITLE_VIDEO, description.getText().toString());
                it.putExtra(EXTRA_DESCRPTION_VIDEO, "");
                ctx.startActivity(it);
            });


        }

        /**public TextView groupIdText;
        public TextView groupName;
        public TextView conceptTheme;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupIdText = itemView.findViewById(R.id.tv_group_id);
            groupName = itemView.findViewById(R.id.tv_group_name);
            conceptTheme = itemView.findViewById(R.id.tv_theme_concept);

        }**/

    }
}
