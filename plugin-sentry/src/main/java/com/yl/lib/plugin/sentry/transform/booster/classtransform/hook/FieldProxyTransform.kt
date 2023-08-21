package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import org.gradle.api.Project
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode

/**
 * @author yulun
 * @since 2023-08-21 15:34
 * 代理PrivacyFieldProxy注解过的字段
 * @see com.yl.lib.privacy_annotation.PrivacyFieldProxy
 */
class FieldProxyTransform : BaseHookTransform() {
    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        if (!privacyExtension.hookField) {
            return super.transform(project, privacyExtension, context, klass)
        }
        klass.methods.forEach { methodNode ->
            methodNode.instructions?.iterator()?.asSequence()?.forEach { node ->
                if (node is FieldInsnNode) {
                    if (HookFieldManager.MANAGER.contains(
                            fieldName = node.name,
                            classOwnerName = node.owner,
                            fieldDesc = node.desc
                        )
                    ) {
                        var proxyFieldItem = HookFieldManager.MANAGER.findHookItemByName(
                            fieldName = node.name,
                            classOwnerName = node.owner,
                            fieldDesc = node.desc
                        )
                        node.owner = proxyFieldItem?.proxyClassName?.replace(".", "/")
                        node.name = proxyFieldItem?.proxyFieldName
                    }
                }
            }
        }
        return klass
    }
}