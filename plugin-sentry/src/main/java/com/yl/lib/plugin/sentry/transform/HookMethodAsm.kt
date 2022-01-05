package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.sentry.base.HookMethodManager
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author yulun
 * @sinice 2021-12-21 14:29
 */
class SentryTraceClassAdapter : ClassVisitor {


    private var className: String = ""

    private var privacyExtension: PrivacyExtension? = null

    private var bHookClass = true

    constructor(api: Int, classVisitor: ClassVisitor?, privacyExtension: PrivacyExtension?) : super(
        api,
        classVisitor
    ) {
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
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassProxy;") == true) {
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
        if (!bHookClass) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return SentryTraceMethodAdapter(
            api,
            methodVisitor,
            access,
            name,
            descriptor,
            privacyExtension
        )
    }
}


class SentryTraceMethodAdapter : AdviceAdapter {

    private var privacyExtension: PrivacyExtension? = null

    constructor(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        privacyExtension: PrivacyExtension?
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.privacyExtension = privacyExtension
    }

    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        var methodItem = HookMethodManager.MANAGER.findHookItemByName(name, owner, descriptor)
        if (methodItem != null) {
            mv.visitMethodInsn(
                INVOKESTATIC,
                HookMethodManager.MANAGER.getHookClassPath(),
                methodItem.proxyMethodName,
                methodItem.proxyMethodDesc,
                false
            )
            return
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }

    override fun visitLdcInsn(value: Any?) {
        super.visitLdcInsn(value)
    }

}