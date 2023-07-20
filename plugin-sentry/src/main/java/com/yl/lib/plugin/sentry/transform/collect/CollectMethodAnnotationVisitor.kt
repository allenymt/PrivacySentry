package com.yl.lib.plugin.sentry.transform.collect

import com.yl.lib.plugin.sentry.transform.manager.HookMethodItem
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import org.gradle.api.logging.Logger
import org.objectweb.asm.AnnotationVisitor

/**
 * @author yulun
 * @since 2023-07-20 17:16
 * @see com.yl.lib.privacy_annotation.PrivacyMethodProxy
 * 解析和收集PrivacyMethodProxy注解的方法
 */
class CollectMethodAnnotationVisitor : AnnotationVisitor {
    private var hookMethodItem: HookMethodItem? = null
    private var logger: Logger

    constructor(
        api: Int,
        annotationVisitor: AnnotationVisitor?,
        hookMethodItem: HookMethodItem?,
        logger: Logger
    ) : super(api, annotationVisitor) {
        this.hookMethodItem = hookMethodItem
        this.logger = logger
    }

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        if (name.equals("originalClass")) {
            var classSourceName = value.toString()
            hookMethodItem?.originClassName =
                classSourceName.substring(1, classSourceName.length - 1)
        } else if (name.equals("originalMethod")) {
            hookMethodItem?.originMethodName = value.toString()
        } else if (name.equals("ignoreClass")) {
            hookMethodItem?.ignoreClass = value as Boolean
        } else if (name.equals("originalOpcode")) {
            hookMethodItem?.originMethodAccess = value as Int
        }
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
        super.visitEnum(name, descriptor, value)
        if (name.equals("originalOpcode")) {
            hookMethodItem?.originMethodAccess = value?.toInt()
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        if (hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKESTATIC) {
            hookMethodItem?.originMethodDesc = hookMethodItem?.proxyMethodDesc
        } else if (hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKEVIRTUAL ||
            hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKEINTERFACE ||
            hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKESPECIAL
        ) {
            // 如果是调用实例方法，代理方法的描述会比原始方法多了一个实例，这里需要裁剪，方便做匹配 、、、
            hookMethodItem?.originMethodDesc =
                hookMethodItem?.proxyMethodDesc?.replace("L${hookMethodItem?.originClassName};", "")
        }
        HookMethodManager.MANAGER.appendHookMethod(hookMethodItem!!)
    }
}