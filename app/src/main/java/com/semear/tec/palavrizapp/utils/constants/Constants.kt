package com.semear.tec.palavrizapp.utils.constants;

import com.semear.tec.palavrizapp.BuildConfig;

object Constants {
    const val EXTRA_LOGIN = "login_extra"
    const val GOOGLE_API_SECRET = BuildConfig.GOOGLE_API_SECRET
    const val YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_SECRET

    //Shared Preferences
    const val  SESSION_USER = "session_user"

    //Database Local
    const val   DB_VERSION = 2
    const val DB_NAME = "palavrizapp_db"
    const val TABLE_THEMES = "themes"
    const val TABLE_GROUPS = "groups"

    //Activity Classroom
    const val EXTRA_COD_VIDEO = "cod_video"
    const val EXTRA_TITLE_VIDEO = "title_video"
    const val EXTRA_SUBTITLE_VIDEO = "subtitle_video"
    const val EXTRA_DESCRPTION_VIDEO = "description_video"

    //Dialog
    const val POSITIVE = 1
    const val NEGATIVE = 2
    const val NEUTRAL = 0
}
