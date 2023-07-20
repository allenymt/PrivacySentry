package com.yl.lib.plugin.sentry.transform.collect

import com.yl.lib.plugin.sentry.transform.manager.HookFieldItem
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.FieldVisitor


/**
 * @author yulun
 * @since 2023-07-20 17:16
 * @see com.yl.lib.privacy_annotation.PrivacyFieldProxy
 * 解析和收集PrivacyFieldProxy注解的变量
 */
class CollectPrivacyFieldVisitor : FieldVisitor {
    private var className: String
    private var fieldName: String?
    private var proxyDescriptor: String?

    constructor(
        api: Int,
        fieldVisitor: FieldVisitor,
        className: String,
        fieldName: String?,
        descriptor: String?
    ) : super(api, fieldVisitor) {
        this.className = className
        this.fieldName = fieldName
        this.proxyDescriptor = descriptor
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyFieldProxy;") == true) {
            var avr = fv.visitAnnotation(descriptor, visible)
            return CollectFieldAnnotationVisitor(
                api,
                avr,
                HookFieldItem(
                    proxyClassName = className,
                    proxyFieldName = fieldName ?: "",
                    proxyFieldDesc = proxyDescriptor ?: ""
                )
            )
        }
        return super.visitAnnotation(descriptor, visible)
    }

}