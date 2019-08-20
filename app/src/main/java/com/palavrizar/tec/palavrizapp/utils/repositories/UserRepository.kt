package com.palavrizar.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.palavrizar.tec.palavrizapp.models.LocationBlacklist
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.models.UserType

class UserRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var fDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()


    fun registerUser(user: User){/*
        if (user.fullname == "Nuage Laboratoire")
            return*/
        if (user.userId == null || user.userId.isEmpty()){
            user.userId = fDatabase.getReference("users/").push().key
        }
        val myRef = fDatabase.getReference("users/" + user.userId)
        myRef.setValue(user)
    }

    fun getUser(userId: String, onCompletion: (User?) -> Unit, onFail: () -> Unit){
        realtimeRepository.getUser(userId, onCompletion, onFail )
    }

    fun setUserType(userId: String, userType: UserType, onCompletion: () -> Unit){
        realtimeRepository.setUserType(userId, userType, onCompletion)
    }

    fun getUserList(lastVisible: String? = null, onCompletion: (ArrayList<User>) -> Unit){
        realtimeRepository.getUserList(lastVisible, onCompletion)
    }

    fun deleteUser(userId: String){
        realtimeRepository.deleteUser(userId)
    }

    fun giveUserCredits(userId: String, numCredits: Int){
        realtimeRepository.editUserCredits(numCredits, userId){}
    }

    fun getUserCredits(userId: String, onCompletion: (Int) -> Unit){
        realtimeRepository.getUserCredits(userId,onCompletion)
    }

    fun getUserSoloCredits(userId: String, onCompletion: (Int) -> Unit){
        realtimeRepository.getUserSoloCredits(userId,onCompletion)
    }

    fun userHasSoloCredits(userId: String, onCompletion: (Boolean) -> Unit){
        realtimeRepository.getUserSoloCredits(userId){
            if (it > 0) {
                onCompletion(true)
            }else{
                onCompletion(false)
            }
        }
    }


    fun userHasCredit(userId: String, onCompletion: (Boolean) -> Unit){
        realtimeRepository.getUserCredits(userId){
            if (it > 0) {
                onCompletion(true)
            }else{
                onCompletion(false)
            }
        }
    }

    fun consumeOneCreditIfPossible(userId: String, onCompletion: () -> Unit, onFail: () -> Unit){
        realtimeRepository.removeOneCreditIfPossible(userId,onCompletion,onFail)
    }

    fun getLocationBlacklist(onCompletion: ((ArrayList<LocationBlacklist>) -> Unit)){
        realtimeRepository.getLocationBlacklist(onCompletion)
    }

    fun saveLocationBlacklist(location: LocationBlacklist){
        realtimeRepository.saveLocationBlacklist(location){}
    }

    fun deleteLocationBlacklist(location: LocationBlacklist){
        realtimeRepository.deleteLocationBlacklist(location.key ?: return)
    }
}
