package com.yl.lib.plugin.sentry.transform.booster.processor

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.util.PrivacyMoveAssetsUtil
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
//            var variantName = variant.name.capitalize()
//            var moveTask = variant.project.tasks.create(
//                "Privacy${variantName}AssetsTask",
//                PrivacyMoveAssetsTask::class.java
//            )
//           var findTask =  variant.project.tasks.findByName(
//                variant.getTaskName("Privacy", "AssetsTask")
//            )
//            "PrivacyAssetsProcessor findTask  is $findTask".privacyPrintln()
//            "PrivacyAssetsProcessor $variantName MoveAssetsTask is $moveTask".privacyPrintln()
            var privacyExtension = variant.project.extensions.findByType(
                PrivacyExtension::class.java
            )
            PrivacyMoveAssetsUtil.Asset.fileName = privacyExtension?.replaceFileName ?: ""
            PrivacyMoveAssetsUtil.Asset.assetsPathList.add(variant.mergeAssetsProvider.get().outputDir.get().asFile.absolutePath)
            PrivacyMoveAssetsUtil.Asset.buildDir = variant.project.buildDir
        }
    }

}