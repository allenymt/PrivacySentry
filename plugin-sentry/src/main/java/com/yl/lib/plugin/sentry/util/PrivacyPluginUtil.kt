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


    fun isActivity(name: String, superName: String?): Boolean {
        return lastIndexOfDot(name).contains("Activity") && lastIndexOfDot(
            superName
        )?.contains("")
    }

    fun isService(name: String, superName: String?): Boolean {
        return lastIndexOfDot(name).contains("Service") && lastIndexOfDot(
            superName
        )?.contains("Service")
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
        val entryName = className.replace("/",".")
        blackList?.forEach{
            if (entryName.contains(it))
                return false
        }
        if (!entryName.endsWith(".class")
//                || entryName.contains("$") // kotlin object编译后都是内部类，因此这里要放开
            || entryName.endsWith("R.class")
            || entryName.endsWith("BuildConfig.class")
            || entryName.contains("android/support/")
            || entryName.contains("android/arch/")
            || entryName.contains("android/app/")
            || entryName.contains("android/material")
            || entryName.contains("androidx")
            || entryName.endsWith(".SF")
            || entryName.contains(".DSA")
            || entryName.contains(".RSA")
            || entryName.contains(".MF")
            ||entryName.contains("META-INF")
            // 过滤掉库本身
            || entryName.contains("com/yl/lib/privacy_annotation")
        ) {
            return false
        }
        return true
    }

}