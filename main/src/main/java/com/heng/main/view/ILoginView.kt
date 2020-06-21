package com.heng.main.view
import com.heng.common.network.retrofit.bean.LoginResponse

interface ILoginView {

    //登录成功
    fun loginSuccess(result: LoginResponse)

    //登录失败
    fun loginFailed(message: String?)

    //登录注册后操作
    fun loginRegisterAfter(result: LoginResponse)
}