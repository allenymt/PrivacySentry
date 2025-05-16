package com.yl.lib.plugin.sentry

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.kotlinx.capitalized
import com.didiglobal.booster.task.spi.VariantProcessor
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.classtransform.collect.ClassProxyCollectTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.collect.MethodProxyCollectTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.ClassProxyTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.FieldProxyTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.FlushHookDataTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.MethodHookTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.ServiceHookTransform
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyAssetsProcessor
import com.yl.lib.plugin.sentry.transform.booster.processor.PrivacyManifestProcessor
import com.yl.lib.plugin.sentry.transform.booster.transformer.PrivacyBaseTransformer
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author yulun
 * @sinice 2021-12-13 17:05
 */
class PrivacySentryPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        var extension = project.extensions.create("privacy", PrivacyExtension::class.java)
        if (!extension.enablePrivacy) {
            return
        }
        PrivacyPluginUtil.privacyPluginUtil.logger = project.logger
        when {
            project.plugins.hasPlugin(AppPlugin::class.java)  -> {
                HookFieldManager.MANAGER.clear()
                HookMethodManager.MANAGER.clear()
                ReplaceClassManager.MANAGER.clear()
                registerTransform(project)
                setupTasks(project)
            }
        }
    }

    private fun setupTasks(project: Project) {
        val processors = listOf(  PrivacyAssetsProcessor(project),
                PrivacyManifestProcessor(project))

        project.setup(processors)

        if (project.state.executed) {
            project.legacySetup(processors)
        } else {
            project.afterEvaluate {
                project.legacySetup(processors)
            }
        }
    }

    private fun Project.setup(processors: List<VariantProcessor>) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.beforeVariants { variantBuilder ->
            processors.forEach { processor ->
                processor.beforeProcess(variantBuilder)
            }
        }
        androidComponents.onVariants { variant ->
            processors.forEach { processor ->
                processor.process(variant)
            }
        }
    }

    private fun Project.legacySetup(processors: List<VariantProcessor>) {
        val android = project.getAndroid<BaseExtension>()
        when (android) {
            is AppExtension -> android.applicationVariants
            is LibraryExtension -> android.libraryVariants
            else -> emptyList<BaseVariant>()
        }.takeIf<Collection<BaseVariant>>(Collection<BaseVariant>::isNotEmpty)?.let { variants ->
            variants.forEach { variant ->
                processors.forEach { processor ->
                    processor.process(variant)
                }
            }
        }
    }

    private fun registerTransform(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            val transform = project.tasks.register(
                "transform${variant.name.capitalized()}ClassesWithBooster",
                PrivacyTransformTask::class.java
            ) {
                it.preTransformers = listOf(
                    PrivacyBaseTransformer(
                         listOf(
                            MethodProxyCollectTransform(),
                            ClassProxyCollectTransform()
                        )
                    )
                )
                it.transformers = listOf(
                    PrivacyBaseTransformer(
                        listOf(
                            MethodHookTransform(),
                            FieldProxyTransform(),
                            ClassProxyTransform(),
                            ServiceHookTransform(),
                            FlushHookDataTransform()
                        )
                    )
                )
                it.variant = variant
                it.applicationId = variant.namespace.get()
                it.bootClasspath = androidComponents.sdkComponents.bootClasspath
            }
            variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(transform).toTransform(
                    ScopedArtifact.CLASSES,
                    PrivacyTransformTask::allJars,
                    PrivacyTransformTask::allDirectories,
                    PrivacyTransformTask::output
                )
        }
    }
}