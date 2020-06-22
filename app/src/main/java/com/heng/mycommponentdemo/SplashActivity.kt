package com.heng.mycommponentdemo
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.common.log.doAppLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun getContentLayoutId(): Int = R.layout.activity_splash

    @SuppressLint("CheckResult")
    override fun initBefore() {
        super.initBefore()
        disposable = Observable.timer(2000L, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ARouter.getInstance().build(CommonConstant.TO_LOGIN_ACTIVITY)
                    .navigation(this, object : NavigationCallback {
                        override fun onLost(postcard: Postcard?) {
                            doAppLog("override fun onLost: ${postcard.toString()}")
                        }

                        override fun onFound(postcard: Postcard?) {
                            doAppLog("override fun onFound: ${postcard.toString()}")
                        }

                        override fun onInterrupt(postcard: Postcard?) {
                            doAppLog("override fun onInterrupt: ${postcard.toString()}")
                        }

                        override fun onArrival(postcard: Postcard?) {
                            doAppLog("override fun onArrival: ${postcard.toString()}")
                        }
                    })

                finish()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
            disposable = null
        }
    }

    private var disposable: Disposable? = null
}
