package com.heng.main
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.component.datastore.database.RoomData
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.common.define.Preference
import com.heng.common.define.toast
import com.heng.common.log.doAppLog
import com.heng.common.network.retrofit.RetrofitHelper
import com.heng.common.network.retrofit.bean.LoginResponse
import com.heng.main.presenter.LoginPresenterImpl
import com.heng.main.view.ILoginView
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.android.synthetic.main.main_activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory

@Route(path = CommonConstant.TO_LOGIN_ACTIVITY)
class LoginActivity : BaseActivity() ,ILoginView {

    //是否已经登录
    private var mIsLogin by Preference(CommonConstant.LOGIN_KEY, false)

    //用户名
    private var mUserName by Preference(CommonConstant.USERNAME_KEY, "")

    //密码
    private var mPassword by Preference(CommonConstant.PASSWORD_KEY, "")

    private val loginPresenterImpl : LoginPresenterImpl by lazy {
        LoginPresenterImpl(this, this)
    }

//    private val gitHubServiceApi by lazy {
//        val retrofit = retrofit2.Retrofit.Builder()
//            .baseUrl("https://api.github.com")
//            //.baseUrl(CommonConstant.REQUEST_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            //添加对 Deferred 的支持
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
//            .build()
//        retrofit.create(GitHubServiceApi::class.java)
//    }

    override fun getContentLayoutId(): Int = R.layout.main_activity_login

    override fun initWidget() {
        super.initWidget()
        login_btn.setOnClickListener(onClickListener)
        register_btn.setOnClickListener(onClickListener)
        login_exit_iv.setOnClickListener(onClickListener)
        doAppLog("mIsLogin:$mIsLogin")
        if (mIsLogin) {
            username_et.setText(mUserName)
            password_et.setText(mPassword)
        }
    }

    override fun onStop() {
        super.onStop()
        login_progressbar?.let {
            it.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenterImpl.releaseObj()
    }

    override fun loginSuccess(resultCode: Int) {

        toast(getString(R.string.main_login_success))

        ARouter.getInstance().build(CommonConstant.TO_MAIN_ACTIVITY).greenChannel()
            .navigation(this)
        finish()
    }

    override fun loginFailed(resultCode: Int) {

        toast(getString(R.string.main_login_failed))

        mIsLogin = false
        login_progressbar.visibility = View.GONE
    }

    override fun registerSuccess(resultCode: Int) {

        toast(getString(R.string.main_register_success))

        ARouter.getInstance().build(CommonConstant.TO_MAIN_ACTIVITY).greenChannel()
               .navigation(this)
        finish()
    }

    override fun registerFailed(resultCode: Int) {

        toast(getString(R.string.main_register_failed))

        mIsLogin = false
        login_progressbar.visibility = View.GONE
        username_et.requestFocus()
        username_et.setText("")
        password_et.setText("")
    }

    override fun loginRegisterAfter(resultCode: Int) {
        login_progressbar.visibility = View.GONE

        mIsLogin = true

        val succeed = (resultCode == CommonConstant.REGISTER_SUCCEED) or (resultCode == CommonConstant.LOGIN_SUCCEED)
        doAppLog("succeed:$succeed")
        if (succeed) {
            mUserName = username_et.text.toString().trim()
            mPassword = password_et.text.toString().trim()
        }

//        mUserName = result.data.username
//        mPassword = result.data.password
//        setResult(Activity.RESULT_OK, Intent().apply {
//            putExtra(
//                CommonConstant.CONTENT_TITLE_KEY,
//                ""
//            ) })

//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
    }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.register_btn->{
                if (checkContent()) {
                    login_progressbar.visibility = View.VISIBLE
                    loginPresenterImpl.registerToWanAndroidAsync(
                        username_et.text.trim().toString(),
                        password_et.text.trim().toString(),
                        password_et.text.trim().toString())
                }
            }
            R.id.login_btn->{
                if (checkContent()) {
                    login_progressbar.visibility = View.VISIBLE
//                    loginPresenterImpl.loginToWanAndroidAsync(
//                        username_et.text.trim().toString(),
//                        password_et.text.trim().toString()
//                    )

//                    try {
//                        GlobalScope.launch(Dispatchers.Main) {
//                            //val loginAsync = RetrofitHelper.retrofitService.loginToWanAndroidAsync(username_et.text.trim().toString(), password_et.text.trim().toString())
//                            //val loginAsync =  gitHubServiceApi.loginToWanAndroidAsync(username_et.text.trim().toString(), password_et.text.trim().toString())
//                            val loginAsync = gitHubServiceApi.getMyUserAsync("bennyhuo")
//                            val result = loginAsync.await()
//                            result?.let {
//                                doAppLog("failed!!")
//                            }
//                            doAppLog("success!!")
//                        }
//                    } catch (e: Exception) {
//                        doAppLog(e.toString())
//                    }

                    loginPresenterImpl.loginToWanAndroidAsync(
                        username_et.text.trim().toString(),
                        password_et.text.trim().toString()
                    )
                }
            }
            R.id.login_exit_iv->{
                finish()
            }
            else->{}
        }
    }

    //用户用户输入的内容
    private fun checkContent() : Boolean {

        username_et.error = null
        password_et.error = null

        var cancel = false
        var focusView : View? = null

        val mUserName = username_et.text.trim().toString()
        val mPassword = password_et.text.trim().toString()

        //检查密码
        if (TextUtils.isEmpty(mPassword)) {
            password_et.error = getString(R.string.main_password_cannot_null)
            focusView = password_et
            cancel = true
        } else if (mPassword.length < 6) {
            password_et.error = getString(R.string.main_password_cannot_too_short)
            focusView = password_et
            cancel = true
        }

        //检查用户名
        if (TextUtils.isEmpty(mUserName)) {
            username_et.error = getString(R.string.main_username_cannot_null)
            focusView = username_et
            cancel = true
        }

        return if (cancel) {
            focusView?.requestFocus()
            false
        } else {
            true
        }
    }
}
