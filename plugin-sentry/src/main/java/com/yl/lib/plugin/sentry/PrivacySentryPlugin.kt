package com.yl.lib.plugin.sentry

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.getAndroid
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.PrivacyCollectTransform
import com.yl.lib.plugin.sentry.transform.booster.PrivacyHookTransform
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyAssetsProcessor
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyCollectProcessor
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyManifestProcessor
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import com.yl.lib.plugin.sentry.util.privacyPrintln
import com.yl.lib.privacy_annotation.PrivacyClassTestProxy
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

/**
 * @author yulun
 * @sinice 2021-12-13 17:05
 */
class PrivacySentryPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.extensions.create("privacy", PrivacyExtension::class.java)
        PrivacyPluginUtil.privacyPluginUtil.logger = project.logger

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
                            PrivacyCollectProcessor().process(variant)
                        }
                    }
                }
            }

        }
    }
}