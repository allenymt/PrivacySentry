package com.yl.lib.plugin.sentry

import com.android.build.gradle.AppExtension
import com.yl.lib.plugin.sentry.transform.PrivacySentryTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author yulun
 * @sinice 2021-12-13 17:05
 */
class PrivacySentryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        var android = target.extensions.getByType(AppExtension::class.java)
        android?.registerTransform(PrivacySentryTransform(target))
    }
}