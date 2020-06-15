package com.heng.mycommponentdemo
import android.app.Application
import com.heng.common.CommonModule

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CommonModule.init(this)
    }
}