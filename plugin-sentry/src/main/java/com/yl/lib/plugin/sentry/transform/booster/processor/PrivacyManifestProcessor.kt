package com.yl.lib.plugin.sentry.transform.booster.processor

import com.android.build.api.variant.Variant
import com.didiglobal.booster.gradle.getTaskName
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.task.PrivacyManifestTask
import com.yl.lib.plugin.sentry.util.privacyPrintln
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

/**
 * @author yulun
 * @since 2023-08-21 17:56
 */
class PrivacyManifestProcessor(private val project: Project) : VariantProcessor {

    override fun process(variant: Variant) {
        if (variant is com.android.build.api.variant.ApplicationVariant) {
            project.afterEvaluate {
                project.tasks.findByName(
                    variant.getTaskName("process", "Manifest")
                )?.let { manifestTask ->
                    var extension = variant.project.extensions.findByType(
                        PrivacyExtension::class.java
                    )!!
                    if (!extension.enableProcessManifest) {
                        return@let
                    }
                    val taskName = "update${variant.name.capitalize()}Manifest"
                    var privacyManifestProcessorTask =
                        variant.project.tasks.create(taskName, PrivacyManifestTask::class.java) {
                            it.variant = variant
                            it.privacyExtension = extension
                            it.outputs.upToDateWhen { false }
                        }
                    manifestTask.finalizedBy(privacyManifestProcessorTask)
                }
            }
        } else {
            "${project.name}-不建议在Library Module下引入插件".privacyPrintln()
        }
    }
}