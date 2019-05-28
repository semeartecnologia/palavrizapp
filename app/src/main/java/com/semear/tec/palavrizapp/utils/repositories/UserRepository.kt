package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.semear.tec.palavrizapp.models.User

class UserRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var fDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()


    fun registerUser(user: User){
        if (user.userId == null || user.userId.isEmpty()){
            user.userId = fDatabase.getReference("users/").push().key
        }
        val myRef = fDatabase.getReference("users/" + user.userId)
        myRef.setValue(user)
    }

    fun getUser(userId: String, onCompletion: (User?) -> Unit, onFail: () -> Unit){
        realtimeRepository.getUser(userId, onCompletion, onFail )
    }

    /*public void registerUser(User user){
        //novo usuario cadastrando email na mao
        if (user.getUserId() == null || user.getUserId().isEmpty()){
            user.setUserId(fDatabase.getReference("users/").push().getKey());
        }
        myRef = fDatabase.getReference("users/" + user.getUserId());
        myRef.setValue(user);
    }*/

/*

package com.semear.tec.palavrizapp.utils.repositories;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.utils.interfaces.OnGetUser;

*/
/**
 * Repositorio para o Banco de Dados de Usuarios
 *//*

public class UserRepository {

    private FirebaseDatabase fDatabase;
    private DatabaseReference myRef;
    private RealtimeRepository  realtimeRepository;

    public UserRepository(Context context){

        fDatabase = FirebaseDatabase.getInstance();
        realtimeRepository = new RealtimeRepository(context);
    }

    */
/**
     * Registra usuario no firebase
     * @param user
     *//*

    public void registerUser(User user){
        //novo usuario cadastrando email na mao
        if (user.getUserId() == null || user.getUserId().isEmpty()){
            user.setUserId(fDatabase.getReference("users/").push().getKey());
        }
        myRef = fDatabase.getReference("users/" + user.getUserId());
        myRef.setValue(user);
    }

    public void getUser(String userId, OnGetUser onGetUser){
        realtimeRepository.getUser(userId, onGetUser);
    }
*/

}
