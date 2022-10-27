package com.yl.lib.sentry.hook.cache

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.text.TextUtils
import com.yl.lib.sentry.hook.PrivacySentry

/**
 * @author yulun
 * @since 2022-10-17 14:47
 * 封装部分通用函数
 */
class CacheUtils {
    object Utils {
        // 带有时间属性的缓存格式
        // 0-12位 xxx 13-？有效期，单位毫秒 |分隔符 实际值
        private const val separator: String = "|"

        const val MINUTE = 60 * 1000L
        const val HOUR = MINUTE * 60
        const val DAY = HOUR * 24
        const val EMPTY = "|PrivacyEmpty|"

        /**
         * 当前值是否在有效期
         * @param value String
         * @return Boolean
         */
        fun isValid(value: String): Boolean {
            if (!isPrivacyTimeData(value)) {
                return false
            }
            try {
                val starTime = value.substring(0, 13)
                val validTime = value.substring(13, value.indexOf(separator))
                return System.currentTimeMillis() - starTime.toLong() < validTime.toLong()
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: Exception) {
                throw e
            }
            return false
        }

        fun parseValue(value: String, default: String): String {
            if (TextUtils.isEmpty(value)) {
                return default
            }
            if (!isPrivacyTimeData(value)) {
                return value
            }
            return value.substring(value.indexOf(separator) + 1, value.length)
        }

        /**
         *
         * @param value String 实际的值
         * @param validTime Long 单位ms
         */
        fun buildTimeValue(value: String, validTime: Long): String {
            return "${System.currentTimeMillis()}$validTime$separator$value"
        }

        private fun isPrivacyTimeData(value: String): Boolean {
            return value != null && value.length > 15 && value.indexOf(separator) > 13
        }

        fun saveToSp(key: String, value: String) {
            getSp()?.edit()?.putString(key, value)?.apply()
        }

        fun loadFromSp(key: String, defaultValue: String): Pair<Boolean, Any?> {
            if (getSp() == null) {
                return Pair(false, defaultValue)
            }
            var result = getSp()?.getString(key, EMPTY)

            var success = false
            if (EMPTY == result) {
                result = defaultValue
                success = false
            } else {
                success = true
            }
            return Pair(success, result)
        }

        fun clearData(key: String) {
            getSp()?.edit()?.remove(key)?.apply()
        }

        private var sp: SharedPreferences? = null
        private fun getSp(): SharedPreferences? {
            if (sp != null) {
                return sp
            }
            return PrivacySentry.Privacy.getContext()?.getSharedPreferences("sentry", MODE_PRIVATE)
        }
    }

}