package com.yl.lib.plugin.sentry.transform.booster

import com.didiglobal.booster.transform.Transformer
import com.yl.lib.plugin.sentry.transform.booster.asmtransform.PrivacyBaseTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.ClassProxyTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.FieldProxyTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.MethodHookTransform
import com.yl.lib.plugin.sentry.transform.booster.classtransform.hook.ServiceHookTransform
import com.yl.lib.plugin.sentry.transform.booster.transformer.PrivacyBaseTransformer
import org.gradle.api.Project

/**
 * @author yulun
 * @since 2023-08-10 17:33
 */
class PrivacyHookTransform (project:Project) : PrivacyBaseTransform(project){
    override val transformers: List<Transformer>
        get() = listOf<Transformer>(
            PrivacyBaseTransformer(
                listOf(
                    MethodHookTransform(),
                    FieldProxyTransform(),
                    ClassProxyTransform(),
                    ServiceHookTransform()
                )
            )
        )
}