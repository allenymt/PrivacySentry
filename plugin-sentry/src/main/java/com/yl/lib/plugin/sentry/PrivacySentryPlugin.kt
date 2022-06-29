package com.yl.lib.plugin.sentry

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformTask
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.PrivacyCollectTransform
import com.yl.lib.plugin.sentry.transform.PrivacySentryTransform
import com.yl.lib.plugin.sentry.upload.HoutuFileUploadTask
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
        project.extensions.create("privacy", PrivacyExtension::class.java)
        var android = project.extensions.getByType(AppExtension::class.java)
        // 收集注解信息的任务
        android?.registerTransform(PrivacyCollectTransform(project))

        // 执行字节码替换的任务
        android?.registerTransform(PrivacySentryTransform(project))

        // 注入上传任务，在sentryTransform任务之后执行
        project.afterEvaluate {
            project.logger.info("project afterEvaluate add  HoutuFileUploadTask")
            project.tasks.create("HoutuFileUploadTask",HoutuFileUploadTask::class.java)
            android.applicationVariants.forEach { variant ->
                var variantName = variant.name.capitalize()
                project.logger.info("project variantName is $variantName")
                var transformTask = project.tasks.withType(TransformTask::class.java)
                var privacySentryTask =
                    transformTask.first { task ->
                        task.variantName.equals(
                            variantName,
                            ignoreCase = true
                        ) && task.transform is PrivacySentryTransform
                    }
                project.logger.info("project privacySentryTask is $privacySentryTask")
                var houtuFileUploadTask = project.tasks.findByName("HoutuFileUploadTask")
                project.logger.info("project houtuFileUploadTask mustRunAfter privacySentryTask houtuFileUploadTask is $houtuFileUploadTask")
                privacySentryTask.finalizedBy(houtuFileUploadTask)
//                houtuFileUploadTask?.mustRunAfter(privacySentryTask)

            }
        }

    }
}