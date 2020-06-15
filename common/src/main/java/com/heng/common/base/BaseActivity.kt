package com.heng.common.base
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import com.heng.common.util.StatusBarUtil

open class BaseActivity : AppCompatActivity() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setNavigationBarBgColor(android.R.color.black)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        /*适配沉浸式状态栏*/
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
        StatusBarUtil.setStatusBarColor(this,statusBarColor())
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            /*不支持暗黑模式情况下  状态栏背景为白色  会看不清字体  所以设置状态栏颜色为半透明*/
            StatusBarUtil.setStatusBarColor(this,0x55000000.toInt())
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    open fun setNavigationBarBgColor(@ColorRes colorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            window.navigationBarColor = resources.getColor(colorRes)
        }
    }

    open fun statusBarColor() : Int {
        return 0xff2196F3.toInt()
    }
}
