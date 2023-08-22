package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassItem
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * @author yulun
 * @since 2023-08-08 10:55
 * 类代理，代理PrivacyClassReplace注解过的类
 * @see com.yl.lib.privacy_annotation.PrivacyClassReplace
 */
class ClassProxyTransform : BaseHookTransform() {

    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        if (!privacyExtension.hookConstructor) {
            return super.transform(project, privacyExtension, context, klass)
        }
        klass.methods.forEach { methodNode ->
            var proxyClassItem: ReplaceClassItem? = null
            methodNode.instructions?.iterator()?.asSequence()?.forEach { node ->
                if (node is TypeInsnNode) {
                    if (node.opcode == Opcodes.NEW && ReplaceClassManager.MANAGER.contains(node.desc)) {
                        proxyClassItem = ReplaceClassManager.MANAGER.findItemByName(node.desc)
                        node.desc = proxyClassItem?.proxyClassName?.replace(".", "/")
                    }
                } else if (node is MethodInsnNode) {
                    if (node.name == "<init>" && proxyClassItem != null && node.owner == proxyClassItem?.originClassName) {
                        node.owner = proxyClassItem?.proxyClassName?.replace(".", "/")
                        proxyClassItem = null
                    }
                }
            }
        }
        return klass
    }
}