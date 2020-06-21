package com.heng.common.network.retrofit
import com.heng.common.network.retrofit.bean.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitService {

    @POST(value = "value/login")
    @FormUrlEncoded
    fun loginToWanAndroid(
        @Field("userName") userName: String,
        @Field("password") password: String
    ): Deferred<LoginResponse>
}