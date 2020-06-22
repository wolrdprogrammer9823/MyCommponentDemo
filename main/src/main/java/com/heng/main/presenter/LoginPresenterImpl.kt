package com.heng.main.presenter
import com.heng.common.network.retrofit.bean.LoginResponse
import com.heng.main.model.ILoginModel
import com.heng.main.model.LoginModelImpl
import com.heng.main.view.ILoginView

class LoginPresenterImpl(private val loginView: ILoginView) : ILoginPresenter {

    private val loginModel: ILoginModel = LoginModelImpl()

    override fun loginWanAndroid(userName: String, password: String) {
        loginModel.loginWanAndroid(this, userName, password)
    }

    override fun loginSuccess(result: LoginResponse) {
        if (result.errorCode != 0) {
            loginView.loginFailed(result.errorMessage)
        } else {
            loginView.loginSuccess(result)
            loginView.loginRegisterAfter(result)
        }
    }

    override fun longFailed(message: String?) {
       loginView.loginFailed(message)
    }

    override fun registerWanAndroid(userName: String, password: String, repassword: String) {
        loginModel.registerWanAndroid(this, userName, password, repassword)
    }

    override fun registerSuccess(result: LoginResponse) {
        if (result.errorCode != 0) {
            loginView.registerFailed(result.errorMessage)
        } else {
            loginView.registerSuccess(result)
            loginView.loginRegisterAfter(result)
        }
    }

    override fun registerFailed(message: String?) {
        loginView.registerFailed(message)
    }

    fun cancelRequest() {
        loginModel.cancelRegisterRequest()
        loginModel.cancelLoginRequest()
    }
}