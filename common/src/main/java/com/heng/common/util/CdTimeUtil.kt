package com.heng.common.util
import java.util.*

object CdTimeUtil {

    fun generateTime(duration: Long): String {

        val totalSeconds = duration.div(1000L)
        val seconds = totalSeconds.rem(60)
        val minutes = totalSeconds.div(60).rem(60)
        val hours = totalSeconds.div(3600)

        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}