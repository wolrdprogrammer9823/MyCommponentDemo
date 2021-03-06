package com.heng.common.base
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.BuildConfig
import com.heng.common.CommonConstant
import com.heng.common.define.Preference
import com.heng.common.log.doCommonLog
import com.heng.common.router.IComponentApplication

class BaseApplication : MultiDexApplication() {

    private val commonTag = BaseApplication::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        instance = this

        Preference.setContext(instance!!)

        /*初始化ARouter*/
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        ARouter.init(this)
        moduleInit()

        MultiDex.install(this)
    }

    /*模块初始化*/
    private fun moduleInit() {
        for (applicationStr in CommonConstant.MODULE_INIT_LIST) {
            try {
                val clazz = Class.forName(applicationStr)
                val obj = clazz.newInstance()
                if (obj is IComponentApplication) {
                    obj.onCreate(instance!!)
                }
            } catch (e: ClassNotFoundException) {
                doCommonLog(commonTag,"ClassNotFoundException: ${e.message}")
            } catch (e: IllegalArgumentException) {
                doCommonLog(commonTag,"IllegalArgumentException: ${e.message}")
            } catch (e: InstantiationException) {
                doCommonLog(commonTag,"InstantiationException: ${e.message}")
            }
        }
    }

    companion object{
        @get:Synchronized
        var instance : BaseApplication? = null
        private set
    }

}