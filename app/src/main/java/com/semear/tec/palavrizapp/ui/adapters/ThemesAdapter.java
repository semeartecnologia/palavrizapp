package com.semear.tec.palavrizapp.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.GroupThemes;

import java.util.ArrayList;
import java.util.List;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ViewHolder> {

    List<GroupThemes> listGroupThemes;

    public ThemesAdapter(){
        this.listGroupThemes = new ArrayList<>();
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
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout_themes, viewGroup, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final GroupThemes groupTheme = this.listGroupThemes.get(i);

        //viewHolder.title.setText(plan.getPlanTitle(context));
        viewHolder.groupIdText.setText("Grupo " + groupTheme.getId());
        viewHolder.groupName.setText(groupTheme.getGroupName());
        viewHolder.conceptTheme.setText("Tema conceito: " + groupTheme.getTemaConceito());

    }

    @Override
    public int getItemCount() {
        return this.listGroupThemes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupIdText;
        public TextView groupName;
        public TextView conceptTheme;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupIdText = itemView.findViewById(R.id.tv_group_id);
            groupName = itemView.findViewById(R.id.tv_group_name);
            conceptTheme = itemView.findViewById(R.id.tv_theme_concept);

        }

    }
}
