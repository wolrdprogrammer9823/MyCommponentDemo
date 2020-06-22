package com.heng.common.define
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.heng.common.CommonConstant
import kotlinx.coroutines.Deferred
import java.lang.StringBuilder
import java.util.HashSet

//保存cookie字符串
fun encodeCookie(cookies : List<String>): String {
    val sb = StringBuilder()
    val set = HashSet<String>()
    cookies.map {cookie ->
        cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }.forEach { value ->
        value.filterNot { set.contains(it) }.forEach { set.add(it) }
    }
    val ite = set.iterator()
    while (ite.hasNext()) {
        val cookie = ite.next()
        sb.append(cookie)
        sb.append(if (ite.hasNext()) ";" else "")
    }
    return sb.toString()
}

//判断协程的状态
fun Deferred<Any>?.cancelByActive() = this?.run {
    tryCatch {
        if (isActive) {
            cancel()
        }
    }
}

//异常处理
inline fun tryCatch(catchBlock: (Throwable) -> Unit = {}, tryBlock: () -> Unit) {
    try {
        tryBlock()
    } catch (e: Throwable) {
        catchBlock(e)
    }
}

//显示toast
fun Context.toast(message: String) {
   CommonConstant.toast?.apply {
       setText(message)
       show()
   } ?: run {
       Toast.makeText(this.applicationContext,message,Toast.LENGTH_SHORT).apply {
          CommonConstant.toast = this
       }.show()
   }
}

fun Context.toast(@StringRes resId: Int) {
    toast(getString(resId))
}