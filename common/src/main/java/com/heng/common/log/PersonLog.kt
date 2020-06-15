package com.heng.common.log
import android.util.Log

const val PERSON_LOG = "person_log"
const val PERSON_NAME = "name"
const val PERSON_NUMBER = "number"
const val PERSON_COMPUTER = "computer"

fun doPersonLog(msg : String) {
    doPersonLog(PERSON_LOG, msg)
}

fun doPersonLog(tag : String, msg: String) {
    Log.d(tag, msg)
}