package com.heng.common.log
import android.util.Log

const val VIDEO_LOG = "video_log"

fun doVideoLog(msg: String) {
    Log.d(VIDEO_LOG, msg)
}

fun doVideoLog(tag: String, msg: String) {
    Log.d(tag, msg)
}
