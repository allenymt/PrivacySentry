package com.yl.lib.plugin.sentry.util

import org.gradle.api.logging.Logger
import kotlin.math.log

/**
 * @author yulun
 * @since 2023-07-20 19:23
 */
class PrivacyPluginUtil {
    companion object {
        val privacyPluginUtil: PrivacyPluginUtil by lazy { PrivacyPluginUtil() }

    }

    var logger: Logger? = null
        set(value) {
            field = value
        }

    fun i(info: Any) {
        logger?.info(info.toString())
    }

    fun d(info: Any) {
        logger?.info(info.toString())
    }

    fun e(info: Any) {
        logger?.info(info.toString())
    }

    fun isActivity(name: String, superName: String?): Boolean {
        return lastIndexOfDot(name).endsWith("Activity") && lastIndexOfDot(
            superName
        ).endsWith("")
    }

    fun isService(name: String, superName: String?): Boolean {
        return (lastIndexOfDot(name).endsWith("Service") || lastIndexOfDot(
            superName
        ).endsWith("Service"))
    }

    private fun lastIndexOfDot(name: String?): String {
        logger?.info("lastIndexOfDot name = $name")
        var index = name?.lastIndexOf(".") ?: 0
        if (index == -1) {
            return name ?: ""
        }
        logger?.info(
            "lastIndexOfDot name = $name , index = $index ， result is ${
                name?.substring(
                    index,
                    name.length
                )
            }"
        )
        return name?.substring(index + 1, name.length) ?: ""
    }

    fun ignoreClass(className: String, blackList: Set<String>? = emptySet()): Boolean {
        val entryName = className.replace("/", ".")
        blackList?.forEach {
            if (entryName.contains(it))
                return true
        }
        if (entryName.endsWith("R.class")
            || entryName.endsWith("BuildConfig.class")
            || entryName.contains("android.support")
            || entryName.contains("android.arch")
            || entryName.contains("android.app")
            || entryName.contains("android.material")
            || entryName.contains("androidx")
            || entryName.endsWith(".SF")
            || entryName.contains(".DSA")
            || entryName.contains(".RSA")
            || entryName.contains(".MF")
            || entryName.contains("META-INF")
            // 过滤掉库本身
            || entryName.contains("com.yl.lib.privacy_annotation")
        ) {
            return true
        }
        return false
    }

}