package com.heng.main.model
import android.content.Context
import com.component.datastore.dao.UserDao
import com.component.datastore.database.RoomData
import com.component.datastore.entity.User
import com.heng.common.CommonConstant
import com.heng.common.define.cancelByActive
import com.heng.common.define.tryCatch
import com.heng.common.network.retrofit.RetrofitHelper
import com.heng.common.network.retrofit.bean.LoginResponse
import com.heng.common.util.MD5Util
import com.heng.main.presenter.ILoginPresenter
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class LoginModelImpl(context: Context) : ILoginModel {

    private var contextWeakReference : WeakReference<Context> = WeakReference(context)

    private val roomData : RoomData by lazy {
        contextWeakReference.get()!!.let { RoomData.getInstance(it) }
    }

    private val userDao : UserDao by lazy {
        roomData.getUserDao()
    }

    //登录逻辑处理
    private var loginAsync: Deferred<LoginResponse>? = null

    //注册逻辑处理
    private var registerAsync : Deferred<LoginResponse>? = null

    override fun loginToWanAndroidAsync(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String
    ) {
//       GlobalScope.async(Dispatchers.Main) {
//            tryCatch({
//                it.printStackTrace()
//                loginPresenter.longFailed(it.toString())
//            }) {
//                loginAsync?.cancelByActive()
//                loginAsync = RetrofitHelper.retrofitService.loginToWanAndroidAsync(userName, password)
//                val result = loginAsync?.await()
//                result?.let {
//                    loginPresenter.longFailed(CommonConstant.RESULT_NULL)
//                    return@async
//                }
//                loginPresenter.loginSuccess(result!!)
//            }
//       }

        val succeed = runBlocking(Dispatchers.IO) {
            //数据库中存储的MD5加密后的数据,传入的数据已经加密过则直接查询,没有则加密后再查询.
            userDao.queryUser(userName, password)
                    || userDao.queryUser(userName, MD5Util.getMD5Code(password))
        }

        loginPresenter.loginSuccess(succeed = succeed)
    }

    override fun cancelLoginRequest() {
        loginAsync?.cancelByActive()
    }

    override fun registerToWanAndroidAsync(
        loginPresenter: ILoginPresenter,
        userName: String,
        password: String,
        repassword: String
    ) {

//        GlobalScope.async(Dispatchers.Main) {
//            tryCatch({
//                it.printStackTrace()
//                loginPresenter.registerFailed(it.toString())
//            }){
//                registerAsync?.cancelByActive()
//                registerAsync = RetrofitHelper.retrofitService.registerToWanAndroidAsync(userName,password,repassword)
//                val result = registerAsync?.await()
//                result?.let {
//                    loginPresenter.registerFailed(it.toString())
//                    return@async
//                }
//                loginPresenter.registerSuccess(result!!)
//            }
//        }

        val user = User()

        user.username = userName
        user.password = password

        val userExited = runBlocking(Dispatchers.IO) {

            userDao.queryUsername(userName)
        }

        if (userExited) {

            loginPresenter.registerSuccess(false)
        } else {

            runBlocking(Dispatchers.IO) {

                userDao.insertUser(user)
            }

            loginPresenter.registerSuccess(true)
        }
    }

    override fun cancelRegisterRequest() {
        registerAsync?.cancelByActive()
    }

    override fun detach() {
        contextWeakReference.clear()
    }
}