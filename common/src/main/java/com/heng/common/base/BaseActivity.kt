package com.heng.common.base
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import com.heng.common.util.StatusBarUtil

abstract class BaseActivity : AppCompatActivity() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setNavigationBarBgColor(android.R.color.black)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        if (initArgs(intent.extras)) {
            setContentView(getContentLayoutId())
            initBefore()
            initWidget()
            initData()
        } else {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        /*点击页面返回导航时,finish当前页面*/
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        /*得到当前activity下的所有Fragment*/
        val fragments = supportFragmentManager.fragments
        fragments.let {
            if (it.isNotEmpty()) {
                for (fragment in it) {
                    /*判断是否是目标类型的fragment*/
                    if (fragment is BaseFragment) {
                        /*判断fragment是否已处理onBackPressed逻辑*/
                        if (fragment.onBackPressed()) {
                            return
                        }
                    }
                }
            }
        }
    }

    open fun setNavigationBarBgColor(@ColorRes colorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            window.navigationBarColor = resources.getColor(colorRes)
        }
    }

    open fun statusBarColor() : Int {
        return 0xff2196F3.toInt()
    }

    /*初始化窗口*/
    open fun initWindow() {
        /*适配沉浸式状态栏*/
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
        StatusBarUtil.setStatusBarColor(this,statusBarColor())
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            /*不支持暗黑模式情况下  状态栏背景为白色  会看不清字体  所以设置状态栏颜色为半透明*/
            StatusBarUtil.setStatusBarColor(this,0x55000000)
        }
    }

    /*初始化控件之前调用*/
    open fun initBefore() {}

    /*初始化数据*/
    open fun initData() {}

    /*初始化控件*/
    open fun initWidget() {}

    /*初始相关的参数*/
    open fun initArgs(bundle: Bundle?) : Boolean{
        return true
    }

    abstract fun getContentLayoutId() : Int
}
