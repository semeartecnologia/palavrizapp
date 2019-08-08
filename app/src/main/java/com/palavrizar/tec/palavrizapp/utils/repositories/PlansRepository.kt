package com.palavrizar.tec.palavrizapp.utils.repositories

import android.content.Context
import com.palavrizar.tec.palavrizapp.models.PlansBilling

class PlansRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var sessionManager = SessionManager(context)

    fun savePlan(plansBilling: PlansBilling, onCompletion: () -> Unit){
        realtimeRepository.savePlan(plansBilling, onCompletion)
    }

    fun getPlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        realtimeRepository.getPlans(onCompletion)
    }

    fun getPlanById(planId: String, onCompletion: (PlansBilling?) -> Unit){
        realtimeRepository.getPlanById(planId, onCompletion)
    }

    fun getPlansByValue(value: String, onCompletion: (ArrayList<PlansBilling>) -> Unit){
        realtimeRepository.getPlansByValue(value, onCompletion)
    }

    fun editUserPlan(plan: String, onCompletion: () -> Unit){
        realtimeRepository.editUserPlan(plan, sessionManager.userLogged, onCompletion)
    }

    fun getSinglePlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        realtimeRepository.getSinglePlans(onCompletion)
    }

    fun deletePlan(planId: String){
        realtimeRepository.deletePlan(planId)
    }

    fun editPlan(planId: String, plansBilling: PlansBilling, onCompletion: () -> Unit){
        realtimeRepository.editPlan(planId, plansBilling, onCompletion)
    }
}