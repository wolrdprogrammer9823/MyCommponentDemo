package com.heng.main.model
import com.heng.main.presenter.ILoginPresenter

interface ILoginModel {

    //登录
    fun loginWanAndroid(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String
    )


    //取消
    fun cancelLoginRequest()

}