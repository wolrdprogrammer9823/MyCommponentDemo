package com.heng.common

object CommonConstant {

    const val TO_MAIN_ACTIVITY = "/module/MainActivity"
    const val TO_SHARE_ACTIVITY = "/person/ShareActivity"

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

    const val REQUEST_BASE_URL = "http://wanandroid.com/"

    const val SHARED_NAME = "_preferences"

    //debug
    const val INTERCEPTOR_ENABLE = false
}