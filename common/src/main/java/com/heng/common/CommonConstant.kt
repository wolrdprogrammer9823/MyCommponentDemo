package com.heng.common

import android.widget.Toast

object CommonConstant {

    const val TO_LOGIN_ACTIVITY = "/module/LoginActivity"
    const val TO_MAIN_ACTIVITY = "/module/MainActivity"
    const val TO_SHARE_ACTIVITY = "/person/ShareActivity"
    const val TO_CARDS_ACTIVITY = "/video/CardsActivity"

    private const val APP_APPLICATION = "com.heng.mycommponentdemo.AppApplication"
    private const val MAIN_APPLICATION = "com.heng.main.MainApplication"
    private const val HOME_APPLICATION = "com.heng.home.HomeApplication"
    private const val PERSON_APPLICATION = "com.heng.person.PersonApplication"
    private const val VIDEO_APPLICATION = "com.heng.video.VideoApplication"

    val MODULE_INIT_LIST = arrayOf(
        APP_APPLICATION,
        MAIN_APPLICATION,
        HOME_APPLICATION,
        PERSON_APPLICATION,
        VIDEO_APPLICATION
    )

    const val RESULT_NULL = "result null!"
    const val REQUEST_BASE_URL = "http://wanandroid.com/"

    const val SHARED_NAME = "_preferences"
    const val LOGIN_KEY = "login"
    const val USERNAME_KEY = "username"
    const val PASSWORD_KEY = "password"

    const val CONTENT_TITLE_KEY = "title"

    //debug
    const val INTERCEPTOR_ENABLE = false


    //Toast 吐司框
    @JvmField
    var toast : Toast? = null
}