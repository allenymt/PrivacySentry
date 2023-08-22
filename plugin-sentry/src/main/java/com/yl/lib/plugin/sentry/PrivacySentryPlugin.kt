package com.yl.lib.plugin.sentry

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.getAndroid
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.PrivacyCollectTransform
import com.yl.lib.plugin.sentry.transform.booster.PrivacyHookTransform
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyAssetsProcessor
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyManifestProcessor
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import com.yl.lib.plugin.sentry.util.privacyPrintln
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author yulun
 * @sinice 2021-12-13 17:05
 */
class PrivacySentryPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.extensions.create("privacy", PrivacyExtension::class.java)
        PrivacyPluginUtil.privacyPluginUtil.logger = project.logger




        //只在application下生效
//        if (!project.plugins.hasPlugin("com.android.application")) {
//            return
//        }

        when {
            project.plugins.hasPlugin("com.android.application") -> {
                HookFieldManager.MANAGER.clear()
                HookMethodManager.MANAGER.clear()
                ReplaceClassManager.MANAGER.clear()
                project.getAndroid<AppExtension>().let { androidExt ->
                    androidExt.registerTransform(PrivacyCollectTransform(project))
                    androidExt.registerTransform(PrivacyHookTransform(project))
                    project.gradle.projectsEvaluated {
                        "===projectsEvaluated===".privacyPrintln()
                        androidExt.applicationVariants.forEach { variant ->
                            PrivacyAssetsProcessor().process(variant)
                            PrivacyManifestProcessor().process(variant)
                        }
                    }
                }
            }

        }
//        var android = project.extensions.getByType(AppExtension::class.java)
//         收集注解信息的任务
//        android?.registerTransform(PrivacyCollectTransform(project))

        // 执行字节码代理的任务
//        android?.registerTransform(PrivacyHookTransform(project))

//        privacyExtension.replaceFileName.let {
//            // replaceFile 生成完后，把文件挪到assets目录下
//            project.tasks.create("MoveAssetsTask", MoveAssetsTask::class.java)
//            project.afterEvaluate {
//                android.applicationVariants.forEach { variant ->
//                    var variantName = variant.name.capitalize()
////                    var pluginHelper = AndroidGradlePluginHelper(project, variant)
////                    registerAssetsTask(variantName, pluginHelper, project, privacyExtension)
////                    registerManifestTask(variantName, pluginHelper, project, privacyExtension)
//                }
//            }
//        }
    }


    // 处理manifest文件，目前是集中在Service的处理
//    private fun registerManifestTask(
//        variantName: String,
//        pluginHelper: AndroidGradlePluginHelper,
//        project: Project,
//        privacyExtension: PrivacyExtension
//    ) {
//        var manifestFile = pluginHelper.mergedManifestFile
//        var processManifestTask = project.tasks.getByName("process${variantName}Manifest")
//        processManifestTask?.let {
//            it.doLast {
//                ManifestProcessor.Processor.process(
//                    manifestFile.absolutePath,
//                    privacyExtension,
//                    project.logger
//                )
//            }
//        }
//    }
}