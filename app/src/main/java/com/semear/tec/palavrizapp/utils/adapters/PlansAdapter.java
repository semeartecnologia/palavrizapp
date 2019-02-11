package com.semear.tec.palavrizapp.utils.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.Plans;
import com.semear.tec.palavrizapp.modules.plans.PlansFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> {

    List<Plans> listPlans;
    Context context;
    private  int lastCheckedPos = 0;
    private Plans currentPlan;
    PlansFragment plansFragment;


    public PlansAdapter(Plans currentPlan, PlansFragment plansFragment){
        this.listPlans = new ArrayList<>();
        this.currentPlan = currentPlan;
        this.plansFragment = plansFragment;
    }

    public void startList(){
        this.listPlans.clear();

        //Pega todos os planos pssiveis
        Plans[] myPlans = Plans.class.getEnumConstants();

        //adiciona na lista e notifica
        this.listPlans.addAll(Arrays.asList(myPlans));
        this.lastCheckedPos = currentPlan.getUserPlan();
        this.notifyDataSetChanged();
    }

    public int getLastCheckedPos(){
        return lastCheckedPos;
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
        viewHolder.radioButton.setChecked(i == lastCheckedPos);

    }

    @Override
    public int getItemCount() {
        return this.listPlans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;
        public RadioButton radioButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.plan_title);
            description = itemView.findViewById(R.id.plan_text);
            radioButton = itemView.findViewById(R.id.radio_plan);

            View.OnClickListener clickListener = v -> {
                lastCheckedPos = getAdapterPosition();
                if (currentPlan.getUserPlan() != lastCheckedPos){
                    plansFragment.setEnableUpdateButton(true);
                }else{
                    plansFragment.setEnableUpdateButton(false);
                }
                notifyDataSetChanged();
            };

            radioButton.setOnClickListener(clickListener);
            itemView.setOnClickListener(clickListener);
        }

    }
}
