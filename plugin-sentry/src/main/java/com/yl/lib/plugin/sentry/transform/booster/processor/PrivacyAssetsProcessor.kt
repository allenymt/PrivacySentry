package com.yl.lib.plugin.sentry.transform.booster.processor

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.task.PrivacyMoveAssetsTask
import com.yl.lib.plugin.sentry.transform.booster.PrivacyHookTransform
import com.yl.lib.plugin.sentry.util.privacyPrintln

/**
 * @author yulun
 * @since 2023-08-18 15:08
 */
class PrivacyAssetsProcessor : VariantProcessor {
    override fun process(variant: BaseVariant) {
        // privacy插件不支持library单独引用，只支持application引用
        if (variant is ApplicationVariant) {
            "PrivacyAssetsProcessor ${variant.name}".privacyPrintln()
            var moveTask = variant.project.tasks.create(
                "Privacy${variant.name}AssetsTask",
                PrivacyMoveAssetsTask::class.java
            )
            "PrivacyAssetsProcessor ${variant.name} MoveAssetsTask is $moveTask".privacyPrintln()
            var privacyExtension = variant.project.extensions.findByType(
                PrivacyExtension::class.java
            )
            moveTask.fileName = privacyExtension?.replaceFileName ?: ""
            moveTask.assetsPathList.add(variant.mergeAssetsProvider.get().outputDir.get().asFile.absolutePath)
            "${moveTask.assetsPathList}".privacyPrintln()

            var transformTask = variant.project.tasks.withType(TransformTask::class.java)
            var privacySentryTask =
                transformTask.first { task ->
                    task.variantName.equals(
                        variant.name,
                        ignoreCase = true
                    ) && task.transform is PrivacyHookTransform
                }
            variant.project.logger.info("project MoveAssetsTask finalizedBy privacySentryTask variantName is ${privacySentryTask.variantName} MoveAssetsTask is $moveTask ${variant.name} after fileName is ${moveTask.fileName} ")
            // 在任务结束之后执行指定的 Task, 也就是mergeAssetsTask执行完后，执行moveTask，把我们代理的api列表同步到assets目录下
            variant.mergeAssetsProvider.get().finalizedBy(moveTask)
            // moveTask 在privacySentryTask执行
            moveTask.mustRunAfter(privacySentryTask)
        }
    }

}