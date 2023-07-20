package com.yl.lib.plugin.sentry.transform.collect

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookMethodItem
import org.gradle.api.logging.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author yulun
 * @since 2023-07-20 17:16
 * @see com.yl.lib.privacy_annotation.PrivacyMethodProxy
 * 解析收集PrivacyClassProxy下的PrivacyMethodProxy注解的方法
 */
class CollectPrivacyMethodVisitor : AdviceAdapter {
    private var privacyExtension: PrivacyExtension? = null
    private var className: String
    private var logger: Logger

    constructor(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        privacyExtension: PrivacyExtension?,
        className: String,
        logger: Logger
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.privacyExtension = privacyExtension
        this.className = className
        this.logger = logger
    }


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyMethodProxy;") == true) {
            var avr = mv.visitAnnotation(descriptor, visible)
            return CollectMethodAnnotationVisitor(
                api,
                avr,
                HookMethodItem(
                    proxyClassName = className,
                    proxyMethodName = name,
                    proxyMethodReturnDesc = methodDesc

                ),
                logger = logger
            )
        }
        return super.visitAnnotation(descriptor, visible)
    }
}
