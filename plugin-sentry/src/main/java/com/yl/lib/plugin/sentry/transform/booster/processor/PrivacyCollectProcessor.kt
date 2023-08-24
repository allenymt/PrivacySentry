package com.yl.lib.plugin.sentry.transform.booster.processor

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.PrivacyHookTransform
import com.yl.lib.plugin.sentry.transform.booster.task.PrivacyCollectTask
import com.yl.lib.plugin.sentry.util.privacyPrintln

/**
 * @author yulun
 * @since 2023-08-24 10:54
 */
class PrivacyCollectProcessor : VariantProcessor {
    override fun process(variant: BaseVariant) {
        if (variant is ApplicationVariant) {
            "PrivacyCollectProcessor ${variant.name}".privacyPrintln()
            var collectTask = variant.project.tasks.create(
                "Privacy${variant.name}CollectTask",
                PrivacyCollectTask::class.java
            )
            "PrivacyCollectProcessor ${variant.name} CollectTask is $collectTask".privacyPrintln()
            var privacyExtension = variant.project.extensions.findByType(
                PrivacyExtension::class.java
            )

            collectTask.variant = variant

            var transformTask = variant.project.tasks.withType(TransformTask::class.java)
            var privacyHookTransformTask =
                transformTask.first { task ->
                    task.variantName.equals(
                        variant.name,
                        ignoreCase = true
                    ) && task.transform is PrivacyHookTransform
                }
            variant.project.logger.info("project CollectTask finalizedBy privacySentryTask variantName is ${privacyHookTransformTask.variantName} collectTask is $collectTask ${variant.name}")
            // 在任务结束之后执行指定的 Task, 也就是mergeAssetsTask执行完后，执行moveTask，把我们代理的api列表同步到assets目录下
            // collectTask 在privacySentryTask之前执行
            privacyHookTransformTask.dependsOn(collectTask)
        }
    }
}