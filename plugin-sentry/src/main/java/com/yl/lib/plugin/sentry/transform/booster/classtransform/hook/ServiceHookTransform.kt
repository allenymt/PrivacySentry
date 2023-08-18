package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.Project
import org.objectweb.asm.tree.ClassNode

/**
 * @author yulun
 * @since 2023-08-08 10:55
 */
class ServiceHookTransform : BaseHookTransform() {

    override fun ignoreClass(context: TransformContext, klass: ClassNode): Boolean {
        return super.ignoreClass(context, klass)
    }

    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        return super.transform(project, privacyExtension, context, klass)
    }
}