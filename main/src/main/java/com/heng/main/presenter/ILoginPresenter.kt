package com.heng.main.presenter
import com.heng.common.network.retrofit.bean.LoginResponse

interface ILoginPresenter {

    //登录操作
    fun loginWanAndroid(userName: String, password: String)

    //登录成功
    fun loginSuccess(result: LoginResponse)

    //登录失败
    fun longFailed(message: String?)
}