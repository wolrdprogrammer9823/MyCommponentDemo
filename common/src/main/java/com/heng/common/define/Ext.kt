package com.heng.common.define
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