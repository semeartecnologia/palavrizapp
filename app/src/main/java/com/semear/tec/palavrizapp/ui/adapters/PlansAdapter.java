package com.semear.tec.palavrizapp.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.Plans;

import java.util.ArrayList;
import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> {

    List<Plans> listPlans;
    Context context;


    public PlansAdapter(){
        this.listPlans = new ArrayList<>();
    }

    public void startList(){
        this.listPlans.clear();

        //Pega todos os planos pssiveis
        Plans[] myPlans = Plans.class.getEnumConstants();

        //adiciona na lista e notifica
        for (Plans plan: myPlans) {
            this.listPlans.add(plan);
        }
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout_plan, viewGroup, false);

        context = viewGroup.getContext();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Plans plan = this.listPlans.get(i);

        viewHolder.title.setText(plan.getPlanTitle(context));
        viewHolder.description.setText(plan.getPlanDesctiption(context));
    }

    @Override
    public int getItemCount() {
        return this.listPlans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.plan_title);
            description = itemView.findViewById(R.id.plan_text);
        }
    }
}
