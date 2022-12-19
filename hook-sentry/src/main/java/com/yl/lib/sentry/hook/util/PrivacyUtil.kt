package com.yl.lib.sentry.hook.util

import android.app.Application
import android.location.Location
import android.location.LocationManager.GPS_PROVIDER
import android.text.TextUtils
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author yulun
 * @sinice 2021-09-24 15:33
 */
class PrivacyUtil {
    object Util {
        fun getStackTrace(): String {
            val st = Thread.currentThread().stackTrace
            val sbf = StringBuilder()
            for (e in st) {
                if (e.methodName.equals("getThreadStackTrace") || e.methodName.equals("getStackTrace")) {
                    continue
                }
                if (e.className.contains("PrivacyProxy")
                    || e.className.contains("PrivacySensorProxy")
                ) {
                    continue
                }
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

        fun formatTime(time: Long, formatStr: String? = "yy-MM-dd_HH-mm-ss.SSS"): String {
            val sdr = SimpleDateFormat(formatStr, Locale.CHINA)
            return sdr.format(time)
        }


        private fun convertStreamToByte(inputStream: InputStream?): ByteArray? {
            if (inputStream == null) {
                return null
            }
            var bos: ByteArrayOutputStream? = null
            try {
                bos = ByteArrayOutputStream()
                val buffer = ByteArray(2 * 1024)
                var read = -1
                while (inputStream.read(buffer)?.also { read = it } != -1) {
                    bos.write(buffer, 0, read)
                }
                return bos.toByteArray()
            } catch (e: java.lang.Exception) {
                Log.e("error:", e.toString())
            } finally {
                if (bos != null) {
                    try {
                        bos.close()
                    } catch (e2: java.lang.Exception) {
                    }
                }
            }
            return null
        }

        fun convertStreamToString(inputStream: InputStream?): String? {
            var result: String? = ""
            val data: ByteArray? = convertStreamToByte(inputStream)
            if (data != null) {
                result = String(data)
            }
            return result
        }

        fun formatLocation(location: Location?): String {
            if (location == null) {
                return ""
            }
            return "${location?.latitude},${location?.longitude},${location?.altitude},${location?.accuracy},${location?.speed},${location?.bearing}"
        }

        fun formatLocation(locationInfo: String): Location? {
            if (TextUtils.isEmpty(locationInfo)) {
                return null
            }
            var location :Location? = null
            val infoArray: Array<String> = locationInfo.split(",").toTypedArray()
            if (infoArray.size > 1) {
                location = Location(GPS_PROVIDER)
                location.latitude = infoArray[0].toDouble()
                location.longitude = infoArray[1].toDouble()
                location.altitude = infoArray[2].toDouble()
                location.accuracy = infoArray[3].toFloat()
                location.speed = infoArray[4].toFloat()
                location.bearing = infoArray[5].toFloat()
            }
            return location
        }

        fun getApplicationByReflect(): Application? {
            return getContextByActivityThread()
        }

        private fun getContextByActivityThread(): Application? {
            try {
                var activityThread = getActivityThreadInActivityThreadStaticField()
                if (activityThread == null) activityThread =
                    getActivityThreadInActivityThreadStaticMethod()
                val declaredField = activityThread?.javaClass?.getDeclaredField("mInitialApplication")
                declaredField?.isAccessible = true
                val `object` = declaredField?.get(activityThread)
                if (`object` is Application) {
                    return `object`
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return null
        }


        private fun getActivityThreadInActivityThreadStaticField(): Any? {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val activityThreadField =
                    activityThreadClass.getDeclaredField("sCurrentActivityThread")
                activityThreadField.isAccessible = true
                activityThreadField[null]
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun getActivityThreadInActivityThreadStaticMethod(): Any? {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                activityThreadClass.getMethod("currentActivityThread").invoke(null)
            } catch (e: Exception) {
               e.printStackTrace()
                null
            }
        }

    }

}