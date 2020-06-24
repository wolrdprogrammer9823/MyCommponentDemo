package com.heng.main

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubServiceApi {

    @GET("users/{login}")
    fun getMyUserAsync(@Path("login") login: String): Deferred<String>
}