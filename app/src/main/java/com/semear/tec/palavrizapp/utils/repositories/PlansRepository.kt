package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.PlansBilling

class PlansRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)


    fun savePlan(plansBilling: PlansBilling, onCompletion: () -> Unit){
        realtimeRepository.savePlan(plansBilling, onCompletion)
    }

    fun getPlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        realtimeRepository.getPlans(onCompletion)
    }
}