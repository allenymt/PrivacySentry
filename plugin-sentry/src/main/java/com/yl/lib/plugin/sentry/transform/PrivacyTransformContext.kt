package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.Project

/**
 * @author yulun
 * @since 2023-08-08 19:34
 */
interface PrivacyTransformContext {
    fun project(): Project

    fun privacyExtension(): PrivacyExtension
}