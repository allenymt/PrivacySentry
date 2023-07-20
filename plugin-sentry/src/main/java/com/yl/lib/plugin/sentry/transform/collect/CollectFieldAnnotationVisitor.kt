package com.yl.lib.plugin.sentry.transform.collect

import com.yl.lib.plugin.sentry.transform.manager.HookFieldItem
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import org.objectweb.asm.AnnotationVisitor

/**
 * @author yulun
 * @since 2023-07-20 17:16
 * 解析注解PrivacyFieldProxy
 */
class CollectFieldAnnotationVisitor : AnnotationVisitor {
    private var hookFieldItem: HookFieldItem? = null

    constructor(
        api: Int,
        annotationVisitor: AnnotationVisitor?,
        hookFieldItem: HookFieldItem
    ) : super(api, annotationVisitor) {
        this.hookFieldItem = hookFieldItem
    }

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        if (name.equals("originalClass")) {
            var classSourceName = value.toString()
            hookFieldItem?.originClassName =
                classSourceName.substring(1, classSourceName.length - 1)
        } else if (name.equals("originalFieldName")) {
            hookFieldItem?.originFieldName = value.toString()
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        hookFieldItem?.let {
            HookFieldManager.MANAGER.appendHookField(it)
        }
    }
}