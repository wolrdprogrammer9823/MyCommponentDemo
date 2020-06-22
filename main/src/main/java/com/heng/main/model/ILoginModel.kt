package com.heng.main.model
import com.heng.main.presenter.ILoginPresenter

interface ILoginModel {

    //登录
    fun loginWanAndroid(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String
    )


    //取消登录
    fun cancelLoginRequest()


    //注册
    fun registerWanAndroid(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String,
        repassword: String
    )

    //取消注册
    fun cancelRegisterRequest()

}