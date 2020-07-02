package com.heng.common.log
import android.util.Log

const val VIDEO_LOG = "video_log"

const val VIDEO_PATH = "video_path"
const val VIDEO_CURRENT_POSITION = "video_current_position"
const val VIDEO_IS_PLAYED = "video_is_played"

const val VIDEO_PLAY_PAUSE_CODE = 0x01

const val VIDEO_ZOOM_IN = 0x02
const val VIDEO_ZOOM_OUT = 0x03

fun doVideoLog(msg: String) {
    Log.d(VIDEO_LOG, msg)
}

fun doVideoLog(tag: String, msg: String) {
    Log.d(tag, msg)
}
