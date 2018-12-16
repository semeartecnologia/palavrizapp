package com.semear.tec.palavrizapp.repositories;
import android.content.Context;
import android.content.SharedPreferences;

import com.semear.tec.palavrizapp.models.Plans;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.utils.Constants;

/**
 * Classe Cache do Usuario
 */
public class SessionManager {

    private Context context;
    private SharedPreferences sharedPref;

    private static final String IS_LOGGED_IN = "is_logged_in";
    private static final String USER_UID = "user_uid";
    private static final String USER_FULLNAME = "user_fullname";
    private static final String USER_TYPE = "user_type";
    private static final String USER_PLAN = "user_plan";

    public SessionManager(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(Constants.SESSION_USER, Context.MODE_PRIVATE);

    }

    /**
     * salva os dados do usuario no cache, e seta isLoggedIn
     * @param user
     * @param isLoggedIn
     */
    public void setUserOnline(User user, Boolean isLoggedIn){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.putString(USER_UID, user.getUserId());
        editor.putString(USER_FULLNAME, user.getFullname());
        editor.putInt(USER_TYPE, user.getUserType().getUserType());
        editor.putInt(USER_PLAN, user.getPlan().getUserPlan());
        editor.apply();
    }

    /**
     * verifica se o usuario esta logado
     * @return boolean
     */
    public boolean isUserLoggedIn(){
        return sharedPref.getBoolean(IS_LOGGED_IN, false);
    }

    /**
     * Retorna o usuario do cache
     * @return
     */
    public User getUserLogged(){
        User user = new User();
        user.setFullname(sharedPref.getString(USER_FULLNAME,""));
        user.setUserId(sharedPref.getString(USER_UID,""));
        user.setPlan(getUserPlan());
        return user;
    }

    public Plans getUserPlan(){
        return Plans.values()[sharedPref.getInt(USER_PLAN, Plans.FREE_PLAN.getUserPlan())];
    }

    public void setUserPlan(int userPlan){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(USER_PLAN, userPlan);
        editor.apply();
    }
}
