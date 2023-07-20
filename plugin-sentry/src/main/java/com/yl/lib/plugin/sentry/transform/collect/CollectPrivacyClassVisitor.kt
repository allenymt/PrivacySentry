package com.yl.lib.plugin.sentry.transform.collect

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.logging.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
/**
 * @author yulun
 * @sinice 2021-12-21 14:29
 * 收集
 * @see com.yl.lib.privacy_annotation.PrivacyClassProxy
 * @see com.yl.lib.privacy_annotation.PrivacyClassReplace
 * 注解的类
 */
class CollectPrivacyClassVisitor : ClassVisitor {
    private var className: String = ""

    private var bHookClass: Boolean = false
    private var privacyExtension: PrivacyExtension? = null
    private var logger: Logger

    constructor(
        api: Int,
        classVisitor: ClassVisitor?,
        privacyExtension: PrivacyExtension?,
        logger: Logger
    ) : super(
        api,
        classVisitor
    ) {
        this.logger = logger
        this.privacyExtension = privacyExtension
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        if (name != null) {
            className = name.replace("/", ".")
        }
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassReplace;") == true) {
            var avr = cv.visitAnnotation(descriptor, visible)
            return CollectClassAnnotationVisitor(api, avr, className, logger)
        }
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassProxy;") == true) {
            bHookClass = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (bHookClass) {
            var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
            return CollectPrivacyMethodVisitor(
                api,
                methodVisitor,
                access,
                name,
                descriptor,
                privacyExtension,
                className,
                logger
            )
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        if (bHookClass && privacyExtension?.hookField == true) {
            var methodVisitor = cv.visitField(access, name, descriptor, signature, value)
            return CollectPrivacyFieldVisitor(
                api,
                methodVisitor,
                className,
                name,
                descriptor
            )
        }
        return super.visitField(access, name, descriptor, signature, value)
    }
}

