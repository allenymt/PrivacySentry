package com.yl.lib.sentry.hook.util

import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author yulun
 * @sinice 2021-09-24 15:33
 */
class PrivacyUtil {
    object Util {
        fun getStackTrace(): String? {
            val st = Thread.currentThread().stackTrace
            val sbf = StringBuilder()
            for (e in st) {
                if (sbf.isNotEmpty()) {
                    sbf.append(" <- ")
                    sbf.append(System.getProperty("line.separator"))
                }
                sbf.append(
                    MessageFormat.format(
                        "{0}.{1}() {2}", e.className, e.methodName, e.lineNumber
                    )
                )
            }
            return sbf.toString()
        }

        fun formatTime(time: Long): String {
            val sdr = SimpleDateFormat("yy-MM-dd_HH-mm-ss", Locale.CHINA)
            return sdr.format(time)
        }

    }

}