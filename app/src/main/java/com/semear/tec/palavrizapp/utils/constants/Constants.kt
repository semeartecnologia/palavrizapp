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
    const val EXTRA_VIDEO_KEY = "key_video"
    const val EXTRA_VIDEO_COMMENT = "key_comments"

    //Dialog
    const val POSITIVE = 1
    const val NEGATIVE = 2
    const val NEUTRAL = 0

    //Video Upload
    const val EXTRA_VIDEO_PATH = "video_path"
    const val EXTRA_IS_EDIT = "is_edit"
    const val EXTRA_VIDEO_TITLE = "extra_video_title"
    const val EXTRA_VIDEO_DESCRIPTION = "extra_video_description"

    const val NOTIFICATION_ID = 1
    const val EXTRA_FILE_URI = "extra_file_uri"
    const val EXTRA_FILE_TITLE= "extra_file_title"
    const val EXTRA_FILE_DESCRIPTION= "extra_file_description"
    const val EXTRA_FILE_CATEGORY = "extra_file_cateogory"
    const val EXTRA_FILE_THUMBNAIL = "extra_file_thumbnail"
    const val EXTRA_FILE_PLANS = "extra_file_plans"

    //Broadcast
    const val BROADCAST_UPLOAD_DONE = "upload_done"
    const val BROADCAST_UPLOAD_PROGRESS = "upload_progress"

    //Essay
    const val EXTRA_IMAGE_CHECK = "extra_image_check"
    const val RESULT_NEGATIVE = 404;
    const val EXTRA_ESSAY = "extra_essay"
    const val EXTRA_IMAGE_FULL_SCREEN = "extra_image_full"
}
