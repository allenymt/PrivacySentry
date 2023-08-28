package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceMethodItem
import com.yl.lib.plugin.sentry.transform.manager.HookedDataManger
import com.yl.lib.plugin.sentry.util.formatName
import org.gradle.api.Project
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author yulun
 * @since 2023-08-08 10:55
 * @see com.yl.lib.privacy_annotation.PrivacyMethodProxy
 * hook方法
 */
class MethodHookTransform : BaseHookTransform() {

    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        klass.methods?.forEach { methodNode ->
            var bLdcHookMethod = false
            methodNode.instructions?.iterator()?.asSequence()?.forEach { node ->
                if (node is MethodInsnNode) {
                    node.apply {
                        var methodItem =
                            HookMethodManager.MANAGER.findHookItemByName(
                                node.name,
                                node.owner,
                                node.desc,
                                node.opcode
                            )
                        if (methodItem != null && shouldHook(
                                node.name,
                                bLdcHookMethod,
                                privacyExtension
                            )
                        ) {
                            HookedDataManger.MANAGER.addReplaceMethodItem(
                                ReplaceMethodItem(
                                    klass.formatName(),
                                    node.name,
                                    owner.replace("/", "."),
                                    name
                                )
                            )

                            node.opcode = AdviceAdapter.INVOKESTATIC
                            node.owner = methodItem.proxyClassName.replace(".", "/")
                            node.name = methodItem.proxyMethodName
                            node.desc = methodItem.proxyMethodDesc
                            node.itf = false
                        }
                    }
                } else if (node is LdcInsnNode) {
                    node.apply {
                        if (cst is String && !bLdcHookMethod) {
                            bLdcHookMethod =
                                HookMethodManager.MANAGER.findByClsOrMethod(cst as String)
                        }
                    }
                }

            }
        }
        return klass
    }

    private fun shouldHook(
        methodName: String,
        bLdcHookMethod: Boolean,
        privacyExtension: PrivacyExtension
    ): Boolean {
        // 反射需要特殊处理下，避免hook所有的反射方法
        return if (methodName == "invoke") {
//            log("shouldHook bLdcHookMethod is $bLdcHookMethod hookReflex is ${privacyExtension?.hookReflex}")
            bLdcHookMethod && privacyExtension.hookReflex
        } else {
            true
        }
    }
}