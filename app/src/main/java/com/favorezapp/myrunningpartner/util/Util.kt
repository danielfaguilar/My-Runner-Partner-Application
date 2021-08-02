package com.favorezapp.myrunningpartner.util

import java.util.concurrent.TimeUnit

fun formatTime(ms: Long, includeMillis: Boolean = false): String {
    var millis = ms

    val hours = TimeUnit.MICROSECONDS.toHours(millis)
    millis -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    millis -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

    if(!includeMillis)
        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds"

    millis -= TimeUnit.SECONDS.toMillis(seconds)
    millis /= 10

    return "${if(hours < 10) "0" else ""}$hours:" +
            "${if(minutes < 10) "0" else ""}$minutes:" +
            "${if(seconds < 10) "0" else ""}$seconds:" +
            "${if(millis < 10) "0" else ""}$millis"
}