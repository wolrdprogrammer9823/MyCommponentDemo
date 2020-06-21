package com.heng.common.network.retrofit
import com.heng.common.CommonConstant
import com.heng.common.define.Preference
import com.heng.common.define.encodeCookie
import com.heng.common.log.doHttpLog
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    private const val READ_TIMEOUT = 10L
    private const val CONNECT_TIMEOUT = 10L

    private const val TAG = "com.heng.common.network.retrofit.RetrofitHelper"
    private const val CONTENT_PRE = "OkHttp: "

    private const val SAVE_USER_LOGIN_KEY = "user/login"
    private const val SAVE_USER_REGISTER_KEY = "user/register"
    private const val SET_COOKIE_KEY = "set-cookie"
    private const val COOKIE_NAME = "cookie"

    val retrofitService = getService(CommonConstant.REQUEST_BASE_URL, RetrofitService::class.java)

    //get ServiceApi
    private fun <T> getService(url: String, serviceClass: Class<T>): T =
        create(url).create(serviceClass)

    private fun create(url: String) : Retrofit {
        val okHttpClientBuilder = OkHttpClient().newBuilder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            //获取响应的cookies
            addInterceptor {
               val request = it.request()
               val response = it.proceed(request)
               val requestUrl = request.url().toString()
               val domain = request.url().host()
               if ((requestUrl.contains(SAVE_USER_LOGIN_KEY) || requestUrl.contains(
                       SAVE_USER_REGISTER_KEY)) && request.header(SET_COOKIE_KEY)!!.isNotEmpty()) {
                   val cookies = response.headers(SET_COOKIE_KEY)
                   val cookie = encodeCookie(cookies)
                   saveCookie(requestUrl, domain, cookie)
               }
               response
            }

            //设置请求的cookie
            addInterceptor {
                val request = it.request()
                val builder = request.newBuilder()
                val domain = request.url().host()
                if (domain.isNotEmpty()) {
                    val spDomain: String by Preference(domain, "")
                    val cookie = if (spDomain.isNotEmpty()) spDomain else ""
                    if (cookie.isNotEmpty()) {
                        builder.addHeader(COOKIE_NAME, cookie)
                    }
                }
                it.proceed(builder.build())
            }

            if (CommonConstant.INTERCEPTOR_ENABLE) {
                addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                    doHttpLog(TAG, CONTENT_PRE + it)
                }).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        return RetrofitBuild(
            url = url,
            client = okHttpClientBuilder.build(),
            gsonConverterFactory = GsonConverterFactory.create(),
            coroutineCallbackAdapter = CoroutineCallAdapterFactory.invoke()
        ).retrofit
    }

    //保存cookie
    private fun saveCookie(requestUrl: String?, domain: String?, cookies: String) {
        requestUrl ?: return
        var spUrl: String by Preference(requestUrl, cookies)
        spUrl = cookies
        domain ?: return
        var spDomain: String by Preference(domain, cookies)
        spDomain = cookies
    }
}

class RetrofitBuild
    (url : String,
     client : OkHttpClient,
     gsonConverterFactory : GsonConverterFactory,
     coroutineCallbackAdapter : CoroutineCallAdapterFactory) {
    val retrofit: Retrofit = Retrofit.Builder().apply {
        baseUrl(url)
        client(client)
        addConverterFactory(gsonConverterFactory)
        addCallAdapterFactory(coroutineCallbackAdapter)
    }.build()
}