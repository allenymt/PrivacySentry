package com.yl.lib.plugin.sentry.transform.booster.classtransform.collect

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.asmtransform.AbsClassTransformer
import com.yl.lib.plugin.sentry.transform.manager.HookFieldItem
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodItem
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.util.*
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import org.gradle.api.Project
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

/**
 * @author yulun
 * @since 2023-08-08 11:08
 * @see com.yl.lib.privacy_annotation.PrivacyMethodProxy
 * 解析和收集PrivacyMethodProxy注解的方法
 * @see com.yl.lib.privacy_annotation.PrivacyFieldProxy
 * 解析和收集PrivacyFieldProxy注解的变量
 */
class MethodProxyCollectTransform : AbsClassTransformer() {
    override fun ignoreClass(context: TransformContext, klass: ClassNode): Boolean {
        var ignore = super.ignoreClass(context, klass)
        if (ignore) {
            return true
        }

        klass.invisibleAnnotations?.find {
            it.desc.privacyClassProxy()
        } ?: return true

        return false
    }

    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        // 收集方法
        klass.methods.filter { methodNode ->
            methodNode.invisibleAnnotations?.find {
                it.desc.privacyMethodProxy()
            } != null
        }.forEach { methodNode ->
            var hookMethodItem = HookMethodItem(klass.formatName(), methodNode.name, methodNode.desc)

            var annotationNode = methodNode.invisibleAnnotations?.find {
                it.desc.privacyMethodProxy()
            }

            var classSourceName = annotationNode?.privacyGetValue<Type>("originalClass").toString()
            hookMethodItem.originClassName =
                classSourceName.substring(1, classSourceName.length - 1)

            hookMethodItem.ignoreClass = annotationNode?.privacyGetValue<Boolean>("ignoreClass") == true

            hookMethodItem.originMethodName = annotationNode?.privacyGetValue<String>("originalMethod")

            hookMethodItem.originMethodAccess = annotationNode?.privacyGetValue<Int>("originalOpcode")


            if (hookMethodItem.originMethodAccess == MethodInvokeOpcode.INVOKESTATIC) {
                hookMethodItem.originMethodDesc = hookMethodItem.proxyMethodDesc
            } else if (hookMethodItem.originMethodAccess == MethodInvokeOpcode.INVOKEVIRTUAL ||
                hookMethodItem.originMethodAccess == MethodInvokeOpcode.INVOKEINTERFACE ||
                hookMethodItem.originMethodAccess == MethodInvokeOpcode.INVOKESPECIAL
            ) {
                // 如果是调用实例方法，代理方法的描述会比原始方法多了一个实例，这里需要裁剪，方便做匹配 、、、
                hookMethodItem.originMethodDesc =
                    hookMethodItem.proxyMethodDesc.replaceFirst(
                        "L${hookMethodItem.originClassName};",
                        ""
                    )
            }
            HookMethodManager.MANAGER.appendHookMethod(hookMethodItem)
        }

        // 收集变量
        klass.fields.filter { fieldNode ->
            fieldNode.invisibleAnnotations?.find {
                it.desc.privacyFieldProxy()
            } != null
        }.forEach { fieldNode ->
            var annotationNode = fieldNode.invisibleAnnotations?.find {
                it.desc.privacyFieldProxy()
            }

            var classSourceName = annotationNode?.privacyGetValue<Type>("originalClass").toString()
            var item = HookFieldItem(
                proxyClassName = klass.formatName(),
                proxyFieldDesc =  fieldNode.desc,
                proxyFieldName =  fieldNode.name,
                originClassName = classSourceName?.substring(1, classSourceName.length - 1) ?: "",
                originFieldName = annotationNode?.privacyGetValue<String>("originalFieldName") ?: ""
            )
            HookFieldManager.MANAGER.appendHookField(item)
            project.logger.info("CollectClassAnnotationVisitor-ReplaceClassItem - ${item.toString()}")
        }
        return klass
    }
}