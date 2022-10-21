package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.Project
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

    private var project: Project

    constructor(
        api: Int,
        classVisitor: ClassVisitor?,
        privacyExtension: PrivacyExtension?,
        project: Project
    ) : super(
        api,
        classVisitor
    ) {
        this.privacyExtension = privacyExtension
        this.project = project
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
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyClassProxy;") == true || HookMethodManager.MANAGER.isProxyClass(
                className
            ) || HookFieldManager.MANAGER.isProxyClass(className)
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
            SentryTraceMethodAdapter(
                api,
                methodVisitor,
                access,
                name,
                descriptor,
                privacyExtension,
                className,
                project
            )
        }
    }
}


class SentryTraceMethodAdapter : AdviceAdapter {

    private var privacyExtension: PrivacyExtension? = null
    private var className: String? = null
    private var methodName: String? = null
    private var project: Project

    // 标识当前方法加载的常量是否为敏感方法。一般来说，反射调用某个方法时，会将方法名作为常量加载到栈中，这个时候就能拦截到
    // 如果是先加载常量再通过其他的方法调用反射，一般也会被内敛，这么做是为了减少拦截反射方法的数量
    private var bLdcHookMethod = false

    constructor(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        privacyExtension: PrivacyExtension?,
        className: String,
        project: Project
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.privacyExtension = privacyExtension
        this.methodName = name
        this.className = className
        this.project = project
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
        if (methodItem != null && shouldHook(name)) {
            ReplaceMethodManger.MANAGER.addReplaceMethodItem(
                ReplaceMethodItem(
                    className!!,
                    methodName!!,
                    owner.replace("/", "."),
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

    //访问某个成员变量
    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        if (HookFieldManager.MANAGER.contains(name, owner, descriptor)) {
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

    // 加载字符串常量
    override fun visitLdcInsn(value: Any?) {

        if (value is String && !bLdcHookMethod) {
            bLdcHookMethod = HookMethodManager.MANAGER.findByClsOrMethod(value)
        }
//        log("visitLdcInsn [$value] ， bLdcHookMethod is $bLdcHookMethod")
        super.visitLdcInsn(value)
    }

    private fun shouldHook(methodName: String): Boolean {
        // 反射需要特殊处理下，避免hook所有的反射方法
        return if (methodName == "invoke") {
//            log("shouldHook bLdcHookMethod is $bLdcHookMethod hookReflex is ${privacyExtension?.hookReflex}")
            bLdcHookMethod && privacyExtension?.hookReflex == true
        } else {
            true
        }
    }

//    private fun log(msg: String) {
//        if (className?.contains("TestReflexJava") == true) {
//            println("className is $className, methodName is $methodName, $msg")
//            project.logger.info("className is $className-， methodName is $methodName, $msg")
//        }
//    }

}