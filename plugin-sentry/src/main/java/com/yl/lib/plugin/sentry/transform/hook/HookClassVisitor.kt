package com.yl.lib.plugin.sentry.transform.hook

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import org.gradle.api.logging.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor

/**
 * @author yulun
 * @since 2023-07-20 17:29
 * 处理代理方法和变量
 * @see com.yl.lib.privacy_annotation.PrivacyClassProxy
 * @see com.yl.lib.privacy_annotation.PrivacyMethodProxy
 * @see com.yl.lib.privacy_annotation.PrivacyFieldProxy
 * @see com.yl.lib.privacy_annotation.PrivacyClassReplace
 */
class HookClassVisitor : ClassVisitor {

    private var className: String = ""

    private var privacyExtension: PrivacyExtension? = null

    private var bHookClass = true

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
        this.privacyExtension = privacyExtension
        this.logger = logger
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
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassProxy;") == true ||
            HookMethodManager.MANAGER.isProxyClass(className) ||
            HookFieldManager.MANAGER.isProxyClass(className) ||
            descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassReplace;") == true ||
            ReplaceClassManager.MANAGER.isProxyClass(className)  ||
            descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassBlack;") == true
        ) {
            bHookClass = false
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
        return if (!bHookClass) {
            super.visitMethod(access, name, descriptor, signature, exceptions)
        } else {
            var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
            HookMethodVisitor(
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
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value)
    }
}
