package com.yl.lib.plugin.sentry

import AndroidGradlePluginHelper
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformTask
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.task.ManifestProcessor
import com.yl.lib.plugin.sentry.task.assets.MoveAssetsTask
import com.yl.lib.plugin.sentry.transform.*
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
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
        ReplaceClassManager.MANAGER.clear()
        var privacyExtension = project.extensions.create("privacy", PrivacyExtension::class.java)
        var android = project.extensions.getByType(AppExtension::class.java)
        // 收集注解信息的任务
        android?.registerTransform(PrivacyCollectTransform(project))

        // 执行字节码代理的任务
        android?.registerTransform(PrivacySentryTransform(project))

        privacyExtension.replaceFileName.let {
            // replaceFile 生成完后，把文件挪到assets目录下
            project.tasks.create("MoveAssetsTask", MoveAssetsTask::class.java)
            project.afterEvaluate {
                android.applicationVariants.forEach { variant ->
                    var variantName = variant.name.capitalize()
                    var pluginHelper = AndroidGradlePluginHelper(project, variant)
                    registerAssetsTask(variantName, pluginHelper, project, privacyExtension)
                    registerManifestTask(variantName, pluginHelper, project, privacyExtension)
                }
            }
        }
    }

    // 把代理的api列表同步到assets目录下，方便查看
    private fun registerAssetsTask(
        variantName: String,
        pluginHelper: AndroidGradlePluginHelper,
        project: Project,
        privacyExtension: PrivacyExtension
    ) {
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
        // 在任务结束之后执行指定的 Task, 也就是mergeAssetsTask执行完后，执行moveTask，把我们代理的api列表同步到assets目录下
        pluginHelper.mergeAssetsTask.finalizedBy(moveTask)
        // moveTask 在privacySentryTask执行
        moveTask.mustRunAfter(privacySentryTask)
    }

    // 后期想做的是自动屏蔽危险权限，自动删除危险权限，目前看好像没必要
    private fun registerManifestTask(
        variantName: String,
        pluginHelper: AndroidGradlePluginHelper,
        project: Project,
        privacyExtension: PrivacyExtension
    ) {
        var manifestFile = pluginHelper.mergedManifestFile
        var processManifestTask = project.tasks.getByName("process${variantName}Manifest")
        processManifestTask?.let {
            it.doLast {
                ManifestProcessor.Processor.process(
                    manifestFile.absolutePath,
                    privacyExtension,
                    project.logger
                )
            }
        }
    }
}