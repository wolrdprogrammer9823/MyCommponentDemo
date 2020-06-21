package com.heng.main.model

import com.heng.common.CommonConstant
import com.heng.common.define.cancelByActive
import com.heng.common.define.tryCatch
import com.heng.common.network.retrofit.RetrofitHelper
import com.heng.common.network.retrofit.bean.LoginResponse
import com.heng.main.presenter.ILoginPresenter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class LoginModelImpl : ILoginModel {

    private var loginAsync: Deferred<LoginResponse>? = null

    override fun loginWanAndroid(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String
    ) {
        GlobalScope.async(Dispatchers.Main) {
           tryCatch({
               it.printStackTrace()
               loginPresenter.longFailed(it.toString())
           }){
               loginAsync?.cancelByActive()
               loginAsync = RetrofitHelper.retrofitService.loginToWanAndroid(userName, password)
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
}