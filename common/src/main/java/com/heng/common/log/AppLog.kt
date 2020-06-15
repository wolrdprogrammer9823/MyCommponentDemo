package com.heng.common.log
import android.util.Log

const val APP_LOG = "app_log"

fun doAppLog(msg : String) {
    Log.d(APP_LOG, msg)
}