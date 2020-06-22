package com.heng.main
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.common.define.Preference
import com.heng.common.define.toast
import com.heng.common.network.retrofit.bean.LoginResponse
import com.heng.main.presenter.LoginPresenterImpl
import com.heng.main.view.ILoginView
import kotlinx.android.synthetic.main.main_activity_login.*

@Route(path = CommonConstant.TO_LOGIN_ACTIVITY)
class LoginActivity : BaseActivity() ,ILoginView {

    //是否已经登录
    private var mIsLogin by Preference(CommonConstant.LOGIN_KEY, false)

    //用户名
    private var mUserName by Preference(CommonConstant.USERNAME_KEY, "")

    //密码
    private var mPassword by Preference(CommonConstant.PASSWORD_KEY, "")

    private val loginPresenterImpl : LoginPresenterImpl by lazy {
        LoginPresenterImpl(this)
    }

    override fun getContentLayoutId(): Int = R.layout.main_activity_login

    override fun initWidget() {
        super.initWidget()
        login_btn.setOnClickListener(onClickListener)
        register_btn.setOnClickListener(onClickListener)
        login_exit_iv.setOnClickListener(onClickListener)
    }

    override fun loginSuccess(result: LoginResponse) {
        toast(getString(R.string.main_login_success))
    }

    override fun loginFailed(message: String?) {
        mIsLogin = false
        login_progressbar.visibility = View.GONE
        message?.let {
            toast(it)
        }
    }

    override fun registerSuccess(result: LoginResponse) {
        toast(getString(R.string.main_register_success))
    }

    override fun registerFailed(errorMessage: String?) {
        mIsLogin = false
        login_progressbar.visibility = View.GONE
        username_et.requestFocus()
        errorMessage?.let {
            toast(it)
        }
    }

    override fun loginRegisterAfter(result: LoginResponse) {
        login_progressbar.visibility = View.GONE
        mIsLogin = true
        mUserName = result.data.userName
        mPassword = result.data.password
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(
                CommonConstant.CONTENT_TITLE_KEY,
                result.data.userName
            ) })
        finish()
    }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.register_btn->{
                if (checkContent()) {
                    login_progressbar.visibility = View.VISIBLE
                    loginPresenterImpl.registerWanAndroid(
                        username_et.text.trim().toString(),
                        password_et.text.trim().toString(),
                        password_et.text.trim().toString())
                }
            }
            R.id.login_btn->{
                if (checkContent()) {
                    login_progressbar.visibility = View.VISIBLE
                    loginPresenterImpl.loginWanAndroid(
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
