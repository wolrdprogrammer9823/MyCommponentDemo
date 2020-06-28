package com.heng.common.log
import android.util.Log

const val VIDEO_LOG = "video_log"

const val VIDEO_PATH = "video_path"
const val VIDEO_CURRENT_POSITION = "video_current_position"
const val VIDEO_IS_PLAYED = "video_is_played"

fun doVideoLog(msg: String) {
    Log.d(VIDEO_LOG, msg)
}

fun doVideoLog(tag: String, msg: String) {
    Log.d(tag, msg)
}
