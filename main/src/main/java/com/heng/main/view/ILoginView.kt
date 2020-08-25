package com.heng.main.view

interface ILoginView {

    //登录成功
    fun loginSuccess(resultCode: Int)

    //登录失败
    fun loginFailed(resultCode: Int)

    //注册成功
    fun registerSuccess(resultCode: Int)

    //注册失败
    fun registerFailed(resultCode: Int)

    //登录注册后操作
    fun loginRegisterAfter(resultCode: Int)
}