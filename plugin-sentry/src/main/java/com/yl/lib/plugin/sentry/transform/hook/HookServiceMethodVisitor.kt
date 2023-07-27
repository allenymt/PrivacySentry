package com.yl.lib.plugin.sentry.transform.hook

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.logging.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author yulun
 * @since 2023-07-25 10:43
 * hook Service的onStartCommand方法, 强制修改返回值为START_NOT_STICKY
 */
class HookServiceMethodVisitor : AdviceAdapter {
    private var privacyExtension: PrivacyExtension? = null
    private var className: String? = null
    private var methodName: String? = null
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
        this.methodName = name
        this.className = className
        this.logger = logger
    }

    override fun visitInsn(opcode: Int) {
        if (opcode == Opcodes.IRETURN) {
            mv.visitInsn(Opcodes.ICONST_2);
            mv.visitInsn(Opcodes.IRETURN);
        } else {
            mv.visitInsn(opcode);
        }
    }
}