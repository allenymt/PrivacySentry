package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
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
        return if (!bHookClass) {
            super.visitMethod(access, name, descriptor, signature, exceptions)
        } else {
            var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
            SentryTraceMethodAdapter(
                api,
                methodVisitor,
                access,
                name,
                descriptor,
                privacyExtension,
                className
            )
        }
    }
}


class SentryTraceMethodAdapter : AdviceAdapter {

    private var privacyExtension: PrivacyExtension? = null
    private var className:String? = null
    private var methodName:String? = null
    constructor(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        privacyExtension: PrivacyExtension?,
        className:String
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.privacyExtension = privacyExtension
        this.methodName = name
        this.className = className
    }

    // 访问方法指令
    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        var methodItem = HookMethodManager.MANAGER.findHookItemByName(name, owner, descriptor)
        if (methodItem != null) {
            ReplaceMethodManger.MANAGER.addReplaceMethodItem(
                ReplaceMethodItem(
                    className!!,
                    methodName!!,
                    owner.replace("/","."),
                    name
                )
            )
            mv.visitMethodInsn(
                INVOKESTATIC,
                methodItem.proxyClassName.replace(".", "/"),
                methodItem.proxyMethodName,
                methodItem.proxyMethodDesc,
                false
            )
            return
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }

    //访问某个成员变量，变量拦截目前只有android/os/Build.SERIAL,所以直接写死了。
    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        if (HookFieldManager.MANAGER.contains(name,owner,descriptor)){
            var fieldItem = HookFieldManager.MANAGER.findHookItemByName(name, owner, descriptor)
            mv.visitFieldInsn(
                opcode,
                fieldItem?.proxyClassName?.replace(".", "/"),
                fieldItem?.proxyFieldName,
                descriptor
            )
            return
        }
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }
}