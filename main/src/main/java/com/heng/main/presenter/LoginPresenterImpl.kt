package com.heng.main.presenter
import android.content.Context
import com.heng.common.CommonConstant
import com.heng.main.model.ILoginModel
import com.heng.main.model.LoginModelImpl
import com.heng.main.view.ILoginView
import java.lang.ref.WeakReference

class LoginPresenterImpl(context: Context, loginView: ILoginView) : ILoginPresenter {

    private val loginModel : ILoginModel = LoginModelImpl(context)
    private val loginViewReference : WeakReference<ILoginView> = WeakReference(loginView)

    override fun loginToWanAndroidAsync(userName: String, password: String) {
        loginModel.loginToWanAndroidAsync(this, userName, password)
    }

    override fun loginSuccess(succeed: Boolean) {

        if (succeed) {

            loginViewReference.get()?.loginSuccess(CommonConstant.LOGIN_SUCCEED)
            loginViewReference.get()?.loginRegisterAfter(CommonConstant.LOGIN_SUCCEED)
        } else {

            loginViewReference.get()?.loginFailed(CommonConstant.LOGIN_FAILED)
        }
    }

    override fun longFailed(message: String?) {

        loginViewReference.get()?.loginFailed(CommonConstant.LOGIN_FAILED)
    }

    override fun registerToWanAndroidAsync(userName: String, password: String, repassword: String) {
        loginModel.registerToWanAndroidAsync(this, userName, password, repassword)
    }

    override fun registerSuccess(succeed: Boolean) {

        if (succeed) {

            loginViewReference.get()?.registerSuccess(CommonConstant.REGISTER_SUCCEED)
            loginViewReference.get()?.loginRegisterAfter(CommonConstant.REGISTER_SUCCEED)
        } else {

            loginViewReference.get()?.registerFailed(CommonConstant.REGISTER_FAILED)
        }
    }

    override fun registerFailed(message: String?) {
        loginViewReference.get()?.registerFailed(CommonConstant.REGISTER_FAILED)
    }

    fun cancelRequest() {

        loginModel.cancelRegisterRequest()
        loginModel.cancelLoginRequest()
    }

    fun releaseObj() {

        loginViewReference.clear()
        loginModel.detach()
        loginModel.cancelLoginRequest()
    }
}