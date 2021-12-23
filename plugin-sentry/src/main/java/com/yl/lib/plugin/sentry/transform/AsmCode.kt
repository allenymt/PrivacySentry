package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.sentry.base.HookMethodManager
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

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
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
        if (HookMethodManager.MANAGER.contains(name, owner, descriptor)) {
            mv.visitMethodInsn(
                INVOKESTATIC,
                privacyExtension?.hookClassPath,
                name,
                getMethodDescriptor(owner, name, descriptor),
                false
            )
            return
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }

    override fun visitLdcInsn(value: Any?) {
        super.visitLdcInsn(value)
    }

    // 在原有的descriptor上把 owner注入进去
    private fun getMethodDescriptor(
        owner: String,
        name: String,
        descriptor: String
    ): String {
        if (owner == "android/provider/Settings\$Secure") {
            return descriptor
        }
        return descriptor.replace("(", "(L$owner;")
    }

}