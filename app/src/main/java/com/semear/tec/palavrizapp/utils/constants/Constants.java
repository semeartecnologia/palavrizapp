package com.semear.tec.palavrizapp.utils.constants;

import com.semear.tec.palavrizapp.BuildConfig;

public class Constants {



    public static final String EXTRA_LOGIN = "login_extra";
    public static final String GOOGLE_API_SECRET = BuildConfig.GOOGLE_API_SECRET;
    public static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_SECRET;

    //Shared Preferences
    public static final String SESSION_USER = "session_user";

    //Database Local
    public static final int  DB_VERSION = 2;
    public static final String DB_NAME = "palavrizapp_db";
    public static final String TABLE_THEMES = "themes";
    public static final String TABLE_GROUPS = "groups";

    //Activity Classroom
    public static final String EXTRA_COD_VIDEO = "cod_video";
    public static final String EXTRA_TITLE_VIDEO = "title_video";
    public static final String EXTRA_SUBTITLE_VIDEO = "subtitle_video";
    public static final String EXTRA_DESCRPTION_VIDEO = "description_video";

}
