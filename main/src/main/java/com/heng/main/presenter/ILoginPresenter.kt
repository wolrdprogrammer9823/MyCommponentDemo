package com.heng.main.presenter

interface ILoginPresenter {

    //登录操作
    fun loginToWanAndroidAsync(userName: String, password: String)

    //登录成功
    fun loginSuccess(succeed: Boolean)

    //登录失败
    fun longFailed(message: String?)

    //注册操作
    fun registerToWanAndroidAsync(userName: String, password: String, repassword: String)

    //注册成功
    fun registerSuccess(succeed: Boolean)

    //注册失败
    fun registerFailed(message: String?)
}