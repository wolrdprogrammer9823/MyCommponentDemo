package com.heng.main.view
import com.heng.common.network.retrofit.bean.LoginResponse

interface ILoginView {

    //登录成功
    fun loginSuccess(result: LoginResponse)

    //登录失败
    fun loginFailed(message: String?)

    //注册成功
    fun registerSuccess(result: LoginResponse)

    //注册失败
    fun registerFailed(errorMessage: String?)

    //登录注册后操作
    fun loginRegisterAfter(result: LoginResponse)
}