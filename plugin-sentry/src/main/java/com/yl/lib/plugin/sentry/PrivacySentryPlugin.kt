package com.yl.lib.plugin.sentry

import AndroidGradlePluginHelper
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformTask
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.task.assets.MoveAssetsTask
import com.yl.lib.plugin.sentry.transform.HookFieldManager
import com.yl.lib.plugin.sentry.transform.HookMethodManager
import com.yl.lib.plugin.sentry.transform.PrivacyCollectTransform
import com.yl.lib.plugin.sentry.transform.PrivacySentryTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

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
        var privacyExtension = project.extensions.create("privacy", PrivacyExtension::class.java)
        var android = project.extensions.getByType(AppExtension::class.java)
        // 收集注解信息的任务
        android?.registerTransform(PrivacyCollectTransform(project))

        // 执行字节码替换的任务
        android?.registerTransform(PrivacySentryTransform(project))

        // 暂时先屏蔽上传任务
        // 注入上传任务，在sentryTransform任务之后执行 ,
//        project.afterEvaluate {
//            project.logger.info("project afterEvaluate add  HoutuFileUploadTask")
//            project.tasks.create("HoutuFileUploadTask",HoutuFileUploadTask::class.java)
//            android.applicationVariants.forEach { variant ->
//                var variantName = variant.name.capitalize()
//                project.logger.info("project variantName is $variantName")
//                var transformTask = project.tasks.withType(TransformTask::class.java)
//                var privacySentryTask =
//                    transformTask.first { task ->
//                        task.variantName.equals(
//                            variantName,
//                            ignoreCase = true
//                        ) && task.transform is PrivacySentryTransform
//                    }
//                project.logger.info("project privacySentryTask is $privacySentryTask")
//                var houtuFileUploadTask = project.tasks.findByName("HoutuFileUploadTask")
//                project.logger.info("project houtuFileUploadTask mustRunAfter privacySentryTask houtuFileUploadTask is $houtuFileUploadTask")
//                privacySentryTask.finalizedBy(houtuFileUploadTask)
////                houtuFileUploadTask?.mustRunAfter(privacySentryTask)
//
//            }
//        }

        privacyExtension.replaceFileName.let {
            // replaceFile 生成完后，把文件挪到assets目录下
            project.tasks.create("MoveAssetsTask",MoveAssetsTask::class.java)
            project.afterEvaluate {
                android.applicationVariants.forEach { variant ->
                    var variantName = variant.name.capitalize()
                    var pluginHelper = AndroidGradlePluginHelper(project, variant)
                    var moveTask = project.tasks.findByName("MoveAssetsTask") as MoveAssetsTask
                    moveTask.fileName = privacyExtension.replaceFileName
                    moveTask.assetsPathList?.add(pluginHelper?.mergedAssetsDir?.absolutePath + File.separator + "privacy" + File.separator + privacyExtension.replaceFileName)
                    var transformTask = project.tasks.withType(TransformTask::class.java)
                    var privacySentryTask =
                        transformTask.first { task ->
                            task.variantName.equals(
                                variantName,
                                ignoreCase = true
                            ) && task.transform is PrivacySentryTransform
                        }
                    project.logger.info("project MoveAssetsTask finalizedBy privacySentryTask variantName is ${privacySentryTask.variantName} MoveAssetsTask is $moveTask $variantName after fileName is ${moveTask.fileName} ")
                    pluginHelper.mergeAssetsTask.finalizedBy(moveTask)
                    moveTask.mustRunAfter(privacySentryTask)
                }
            }
        }
    }
}