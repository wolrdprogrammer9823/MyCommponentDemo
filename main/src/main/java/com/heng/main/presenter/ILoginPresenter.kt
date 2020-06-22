package com.heng.main.presenter
import com.heng.common.network.retrofit.bean.LoginResponse

interface ILoginPresenter {

    //登录操作
    fun loginWanAndroid(userName: String, password: String)

    //登录成功
    fun loginSuccess(result: LoginResponse)

    //登录失败
    fun longFailed(message: String?)

    //注册操作
    fun registerWanAndroid(userName: String, password: String, repassword: String)

    //注册成功
    fun registerSuccess(result: LoginResponse)

    //注册失败
    fun registerFailed(message: String?)
}