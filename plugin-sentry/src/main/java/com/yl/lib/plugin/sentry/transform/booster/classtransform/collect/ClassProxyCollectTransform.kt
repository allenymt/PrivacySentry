package com.yl.lib.plugin.sentry.transform.booster.classtransform.collect

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.asmtransform.AbsClassTransformer
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassItem
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import com.yl.lib.plugin.sentry.util.formatName
import com.yl.lib.plugin.sentry.util.privacyGetValue
import org.gradle.api.Project
import org.objectweb.asm.tree.ClassNode

/**
 * @author yulun
 * @since 2023-08-11 11:17
 * @see com.yl.lib.privacy_annotation.PrivacyClassReplace
 * 收集待代理的类
 */
class ClassProxyCollectTransform : AbsClassTransformer() {
    override fun ignoreClass(context: TransformContext, klass: ClassNode): Boolean {
        var ignore = super.ignoreClass(context, klass)
        if (ignore) {
            return true
        }
        klass.invisibleAnnotations?.find {
            it.desc.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassReplace;")
        } ?: return true

        return false
    }

    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        var annotationNode = klass.invisibleAnnotations?.find {
            it.desc.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassReplace;")
        }

        var classSourceName = annotationNode?.privacyGetValue<String>("originClass")
        var item = ReplaceClassItem(
            originClassName = classSourceName?.substring(1, classSourceName?.length - 1) ?: "",
            proxyClassName = klass.formatName()
        )
        ReplaceClassManager.MANAGER.appendHookItem(item)
        project.logger.info("CollectClassAnnotationVisitor-ReplaceClassItem - ${item.toString()}")
        return klass
    }
}