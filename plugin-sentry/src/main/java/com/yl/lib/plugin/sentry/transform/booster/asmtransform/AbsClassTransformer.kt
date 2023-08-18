package com.yl.lib.plugin.sentry.transform.booster.asmtransform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.PrivacyTransformContext
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import org.gradle.api.Project
import org.objectweb.asm.tree.ClassNode

/**
 * @author yulun
 * @since 2023-08-08 17:41
 * transform 基类
 */
open class AbsClassTransformer : ClassTransformer {

    open fun ignoreClass(context: TransformContext, klass: ClassNode) : Boolean {
        var  isBlack = klass.invisibleAnnotations?.find {
            it.desc.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassBlack;")
        }

        if (isBlack != null) {
            return true
        }

        if (context is PrivacyTransformContext) {
            var extension = context.privacyExtension()
            if (PrivacyPluginUtil.privacyPluginUtil.ignoreClass(
                    klass.className,
                    extension.blackList
                )
            ) {
                return true
            }
        }
        return false
    }


    final override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        //过滤kotlin module-info
        if (klass.className == "module-info") {
            return klass
        }

        if (ignoreClass(context, klass)) {
            return klass
        }

        if (context is PrivacyTransformContext) {
            var project = context.project()
            var extension = context.privacyExtension()
            // 在黑白名单里不作处理
            if (PrivacyPluginUtil.privacyPluginUtil.ignoreClass(
                    klass.className,
                    extension.blackList
                )
            ) {
                return klass
            }
            transform(project, extension, context, klass)
        }
        return klass
    }

    open fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode = klass
}