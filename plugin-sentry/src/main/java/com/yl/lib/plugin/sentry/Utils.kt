package com.yl.lib.plugin.sentry

import com.android.build.api.variant.VariantInfo
import com.yl.lib.plugin.sentry.extension.PrivacyExtension

object Utils {
    @JvmStatic
    fun isApply(variant: VariantInfo?, privacyExtension: PrivacyExtension): Boolean {
        return if (variant == null) {
            false
        } else {
            variant.buildTypeName == "release" || privacyExtension.debugEnable
        }
    }
}