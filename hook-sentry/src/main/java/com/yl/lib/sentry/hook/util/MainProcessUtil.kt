package com.yl.lib.sentry.hook.util

import android.app.Application
import android.content.Context
import android.os.Build
import android.text.TextUtils

/**
 * @author yulun
 * @sinice 2021-09-24 14:54
 * 主进程判断代码，绕过getRunningAppProcesses
 */
class MainProcessUtil {
    object MainProcessChecker {
        private var currentProcessName = ""

        /**
         * 是否主进程
         */
        fun isMainProcess(context: Context?): Boolean {
            if (context == null) {
                PrivacyLog.e("======> isMainProcess context == null")
                return false
            }
            if (TextUtils.isEmpty(currentProcessName)) {
                currentProcessName = getProcessName(context)
            }
            return context.packageName == currentProcessName
        }

        /**
         * 当前进程名
         */
        fun getProcessName(context: Context?): String {
            if (TextUtils.isEmpty(currentProcessName)) {
                currentProcessName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // 9.0开始直接读
                    Application.getProcessName()
                } else {
                    // 9.0以下反射拿
                    getProcessNameByReflect(context)
                }
            }
            return currentProcessName
        }

        // 以下代码 替代 getRunningProcessName
        private fun getProcessNameByReflect(context: Context?): String {
            val activityThread = getActivityThread(context)
            if (activityThread != null) {
                try {
                    val method =
                        activityThread.javaClass.getMethod("currentProcessName")
                    method.isAccessible = true
                    return method.invoke(activityThread) as String
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return ""
        }

        private fun getActivityThread(context: Context?): Any? {
            var activityThread = getActivityThreadInActivityThreadStaticField()
            if (activityThread != null) return activityThread
            activityThread = getActivityThreadInActivityThreadStaticMethod()
            return activityThread ?: getActivityThreadInLoadedApkField(context)
        }

        private fun getActivityThreadInActivityThreadStaticField(): Any? {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val sCurrentActivityThreadField =
                    activityThreadClass.getDeclaredField("sCurrentActivityThread")
                sCurrentActivityThreadField.isAccessible = true
                sCurrentActivityThreadField[null]
            } catch (e: Exception) {
                PrivacyLog.e("getActivityThreadInActivityThreadStaticField: " + e.message)
                null
            }
        }

        private fun getActivityThreadInActivityThreadStaticMethod(): Any? {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                activityThreadClass.getMethod("currentActivityThread").invoke(null)
            } catch (e: Exception) {
                PrivacyLog.e("getActivityThreadInActivityThreadStaticMethod: " + e.message)
                null
            }
        }

        private fun getActivityThreadInLoadedApkField(context: Context?): Any? {
            return try {
                val mLoadedApkField = Application::class.java.getDeclaredField("mLoadedApk")
                mLoadedApkField.isAccessible = true
                val mLoadedApk = mLoadedApkField[context]
                val mActivityThreadField = mLoadedApk.javaClass.getDeclaredField("mActivityThread")
                mActivityThreadField.isAccessible = true
                mActivityThreadField[mLoadedApk]
            } catch (e: Exception) {
                PrivacyLog.e("getActivityThreadInLoadedApkField: " + e.message)
                null
            }
        }
    }
}