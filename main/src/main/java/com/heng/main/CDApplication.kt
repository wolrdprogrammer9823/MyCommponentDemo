package com.heng.main
import android.app.Application
import com.heng.common.CommonModule

class CDApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CommonModule.init(this)
    }
}