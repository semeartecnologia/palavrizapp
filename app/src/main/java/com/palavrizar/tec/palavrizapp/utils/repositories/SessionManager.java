package com.palavrizar.tec.palavrizapp.utils.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.palavrizar.tec.palavrizapp.models.User;
import com.palavrizar.tec.palavrizapp.models.UserType;
import com.palavrizar.tec.palavrizapp.utils.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe Cache do Usuario
 */
public class SessionManager {

    private SharedPreferences sharedPref;

    private static final String IS_LOGGED_IN = "is_logged_in";
    private static final String USER_UID = "user_uid";
    private static final String USER_FULLNAME = "user_fullname";
    private static final String USER_TYPE = "user_type";
    private static final String USER_PLAN = "user_plan";
    private static final String FIRST_TIME = "first_time";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_PHOTO_URI = "user_photo_uri";
    private static final String USER_VIDEO_PROGRESS = "user_video_progress";
    private static final String USER_VIDEO_POSITION = "user_video_position";
    private static final String USER_CREDITS = "user_credits";

    private static final String VIDEO_FILER_THEME = "video_filter_theme";
    private static final String VIDEO_FILER_STRUCTURE = "video_filter_structure";
    private static final String VIDEO_FILER_CONCEPT = "video_filter_concept";

    public SessionManager(Context context){
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
        editor.putString(USER_PHOTO_URI, user.getPhotoUri());
        editor.putString(USER_EMAIL, user.getEmail());
        editor.putString(USER_PLAN, user.getPlan());
        editor.putInt(USER_CREDITS, user.getEssayCredits());
        editor.apply();
    }

    public void setUserOffline(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(USER_UID, "");
        editor.putString(USER_FULLNAME, "");
        editor.putInt(USER_TYPE, -1);
        editor.putString(USER_PLAN, "");
        editor.putString(USER_PHOTO_URI, "");
        editor.putString(USER_EMAIL, "");
        editor.putInt(USER_CREDITS, 0);
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
        user.setEmail(sharedPref.getString(USER_EMAIL,""));
        user.setPhotoUri(sharedPref.getString(USER_PHOTO_URI,""));
        user.setPlan(sharedPref.getString(USER_PLAN,""));
        user.setUserType(UserType.values()[sharedPref.getInt(USER_TYPE,0)]);
        user.setEssayCredits(sharedPref.getInt(USER_TYPE,0));
        return user;
    }

    public String getUserPlan(){
        return sharedPref.getString(USER_PLAN,"");
    }

    public void setUserPlan(String userPlan){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_PLAN, userPlan);
        editor.apply();
    }

    public void saveVideosPosition(JSONObject jsonVideosProgress){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_VIDEO_POSITION, jsonVideosProgress.toString());
        editor.commit();
    }

    public JSONObject getVideosPosition(){
        String strjSON = sharedPref.getString(USER_VIDEO_POSITION,"");
        if (strjSON != null && !strjSON.isEmpty() ){
            try {
                return new JSONObject(strjSON);
            } catch (JSONException e) {

            }
        }
        return null;
    }

    public void saveVideosProgess(JSONObject jsonVideosProgress){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_VIDEO_PROGRESS, jsonVideosProgress.toString());
        editor.commit();
    }

    public JSONObject getVideosProgress(){
        String strjSON = sharedPref.getString(USER_VIDEO_PROGRESS,"");
        if (strjSON != null && !strjSON.isEmpty() ){
            try {
                return new JSONObject(strjSON);
            } catch (JSONException e) {

            }
        }
        return null;
    }

    //------------------
    public void setVideoConceptFilter(Boolean isChecked){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(VIDEO_FILER_CONCEPT, isChecked);
        editor.apply();
    }


    public void setVideoStructureFilter(Boolean isChecked){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(VIDEO_FILER_STRUCTURE, isChecked);
        editor.apply();
    }


    public void setVideoThemeFilter(Boolean isChecked){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(VIDEO_FILER_THEME, isChecked);
        editor.apply();
    }


    public Boolean getVideoConceptFilter(){
        return sharedPref.getBoolean(VIDEO_FILER_CONCEPT,false);
    }


    public Boolean getVideoStructureFilter(){
        return sharedPref.getBoolean(VIDEO_FILER_STRUCTURE,false);
    }


    public Boolean getVideoThemeFilter(){
        return sharedPref.getBoolean(VIDEO_FILER_THEME,false);
    }



    //------------------

    /**
     * seta primeira vez do usuario no sistema
     * @param firstTime
     */
    public void setUserFirstTime(boolean firstTime){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(FIRST_TIME, firstTime);
        editor.apply();
    }

    public boolean isUserFirstTime(){return sharedPref.getBoolean(FIRST_TIME, true);}
}
