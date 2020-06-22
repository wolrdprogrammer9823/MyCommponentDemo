package com.heng.common.network.retrofit
import com.heng.common.network.retrofit.bean.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitService {

    //登录操作
    @POST(value = "value/login")
    @FormUrlEncoded
    fun loginToWanAndroid(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Deferred<LoginResponse>


    //注册操作
    @POST(value = "user/register")
    @FormUrlEncoded
    fun registerToWanAndroid(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ) : Deferred<LoginResponse>
}