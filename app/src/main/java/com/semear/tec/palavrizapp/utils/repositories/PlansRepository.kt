package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.PlansBilling
import com.semear.tec.palavrizapp.models.User

class PlansRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var sessionManager = SessionManager(context)

    fun savePlan(plansBilling: PlansBilling, onCompletion: () -> Unit){
        realtimeRepository.savePlan(plansBilling, onCompletion)
    }

    fun getPlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        realtimeRepository.getPlans(onCompletion)
    }

    fun editUserPlan(plan: String, onCompletion: () -> Unit){
        realtimeRepository.editUserPlan(plan, sessionManager.userLogged, onCompletion)
    }

    fun getSinglePlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        realtimeRepository.getSinglePlans(onCompletion)
    }
}