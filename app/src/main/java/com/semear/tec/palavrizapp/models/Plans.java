package com.semear.tec.palavrizapp.models;

import android.content.Context;

import com.semear.tec.palavrizapp.R;

import java.util.Arrays;

public enum Plans {

    FREE_PLAN(0),
    BASIC_PLAN(1),
    ADVANCED_PLAN(2);

    private final int plan;

    Plans(int plan) {
        this.plan = plan;
    }

    public static String[] names() {
        return Arrays.toString(Plans.values()).replaceAll("^.|.$", "").split(", ");
    }



    public int getUserPlan(){
        return plan;
    }

    /**
     * retorna os ttitulos dos planos
     * @param context
     * @return
     */
    public String getPlanTitle(Context context){
        switch(this){
            case FREE_PLAN:
                return context.getString(R.string.free_plan_title);
            case BASIC_PLAN:
                return context.getString(R.string.basic_plan_title);
            case ADVANCED_PLAN:
                return context.getString(R.string.advanced_plan_title);
            default:
                return "";
        }
    }

    /**
     * retorna as descricoes dos planos
     * @param context
     * @return
     */
    public String getPlanDesctiption(Context context){
        switch(this){
            case FREE_PLAN:
                return context.getString(R.string.free_plan_text);
            case BASIC_PLAN:
                return context.getString(R.string.basic_plan_text);
            case ADVANCED_PLAN:
                return context.getString(R.string.advanced_plan_text);
            default:
                return "";
        }
    }

    }
