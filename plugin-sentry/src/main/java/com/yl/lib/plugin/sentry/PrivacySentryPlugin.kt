package com.yl.lib.plugin.sentry

import com.android.build.gradle.AppExtension
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.*
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author yulun
 * @sinice 2021-12-13 17:05
 */
class PrivacySentryPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        //只在application下生效
        if (!project.plugins.hasPlugin("com.android.application")) {
            return
        }
        HookFieldManager.MANAGER.clear()
        HookMethodManager.MANAGER.clear()
        ReplaceClassManager.MANAGER.clear()
        var privacyExtension = project.extensions.create("privacy", PrivacyExtension::class.java)
        var android = project.extensions.getByType(AppExtension::class.java)
        // 收集注解信息的任务
        android?.registerTransform(PrivacyCollectTransform(project.logger, privacyExtension))

        // 执行字节码替换的任务
        android?.registerTransform(PrivacySentryTransform(project.logger, privacyExtension,project.buildDir.absolutePath))
    }
}
