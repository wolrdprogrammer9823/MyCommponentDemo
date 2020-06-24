package com.heng.main.model
import com.heng.common.CommonConstant
import com.heng.common.define.cancelByActive
import com.heng.common.define.tryCatch
import com.heng.common.network.retrofit.RetrofitHelper
import com.heng.common.network.retrofit.bean.LoginResponse
import com.heng.main.presenter.ILoginPresenter
import kotlinx.coroutines.*

class LoginModelImpl : ILoginModel {

    //登录逻辑处理
    private var loginAsync: Deferred<LoginResponse>? = null

    //注册逻辑处理
    private var registerAsync : Deferred<LoginResponse>? = null

    override fun loginToWanAndroidAsync(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String
    ) {
       GlobalScope.async(Dispatchers.Main) {
            tryCatch({
                it.printStackTrace()
                loginPresenter.longFailed(it.toString())
            }) {
                loginAsync?.cancelByActive()
                loginAsync = RetrofitHelper.retrofitService.loginToWanAndroidAsync(userName, password)
                val result = loginAsync?.await()
                result?.let {
                    loginPresenter.longFailed(CommonConstant.RESULT_NULL)
                    return@async
                }
                loginPresenter.loginSuccess(result!!)
            }
       }
    }

    override fun cancelLoginRequest() {
        loginAsync?.cancelByActive()
    }

    override fun registerToWanAndroidAsync(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String,
        repassword: String
    ) {
        GlobalScope.async(Dispatchers.Main) {
            tryCatch({
                it.printStackTrace()
                loginPresenter.registerFailed(it.toString())
            }){
                registerAsync?.cancelByActive()
                registerAsync = RetrofitHelper.retrofitService.registerToWanAndroidAsync(userName,password,repassword)
                val result = registerAsync?.await()
                result?.let {
                    loginPresenter.registerFailed(it.toString())
                    return@async
                }
                loginPresenter.registerSuccess(result!!)
            }
        }
    }

    override fun cancelRegisterRequest() {
        registerAsync?.cancelByActive()
    }
}