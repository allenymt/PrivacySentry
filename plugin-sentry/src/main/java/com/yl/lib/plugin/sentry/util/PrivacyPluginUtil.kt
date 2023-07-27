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

    fun isActivity(name: String, superName: String?): Boolean {
        return name.contains("Activity") || superName?.contains("Activity") == true
    }

    fun isService(name: String, superName: String?, logger: Logger): Boolean {
        return lastIndexOfDot(name, logger).contains("Service") && lastIndexOfDot(
            superName,
            logger
        )?.contains("Service")
    }

    private fun lastIndexOfDot(name: String?, logger: Logger): String {
        logger.info("lastIndexOfDot name = $name")
        var index = name?.lastIndexOf(".") ?: 0
        if (index == -1) {
            return name ?: ""
        }
        logger.info(
            "lastIndexOfDot name = $name , index = $index ， result is ${
                name?.substring(
                    index,
                    name.length
                )
            }"
        )
        return name?.substring(index + 1, name.length) ?: ""
    }
}