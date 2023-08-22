package com.yl.lib.plugin.sentry.transform.booster.processor

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.getTaskName
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.task.ManifestProcessorTask
import com.yl.lib.plugin.sentry.util.privacyPrintln

/**
 * @author yulun
 * @since 2023-08-21 17:56
 */
class PrivacyManifestProcessor : VariantProcessor {
    override fun process(variant: BaseVariant) {
        if (variant is ApplicationVariant) {
            variant.project.tasks.findByName(
                variant.getTaskName("process", "Manifest")
            )?.let { manifestTask ->
                val taskName = "update${variant.name.capitalize()}Manifest"
                var privacyManifestProcessorTask =
                    variant.project.tasks.create(taskName, ManifestProcessorTask::class.java) {
                        it.variant = variant
                        it.privacyExtension = variant.project.extensions.findByType(
                            PrivacyExtension::class.java
                        )!!
                        it.outputs.upToDateWhen { false }
                    }
                manifestTask.finalizedBy(privacyManifestProcessorTask)
            }
        } else {
            "${variant.project.name}-不建议在Library Module下引入插件".privacyPrintln()
        }
    }
}