package com.heng.common
import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

object CommonModule {

    fun init(application: Application) {

        /*初始化ARouter*/
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        ARouter.init(application)
    }
}