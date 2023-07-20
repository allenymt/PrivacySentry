package com.yl.lib.plugin.sentry.transform.hook

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.*
import org.gradle.api.logging.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author yulun
 * @since 2023-07-20 17:30
 * 方法代理，代理PrivacyClassProxy下的PrivacyMethodProxy注解过的方法
 * @see com.yl.lib.privacy_annotation.PrivacyMethodProxy
 * 方法代理支持反射

 * 类代理，代理PrivacyClassReplace注解过的类
 * @see com.yl.lib.privacy_annotation.PrivacyClassReplace
 *
 * 代理PrivacyFieldProxy注解过的字段
 * @see com.yl.lib.privacy_annotation.PrivacyFieldProxy
 */
class HookMethodVisitor : AdviceAdapter {

    private var privacyExtension: PrivacyExtension? = null
    private var className: String? = null
    private var methodName: String? = null
    private var logger: Logger

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
        logger: Logger
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.privacyExtension = privacyExtension
        this.methodName = name
        this.className = className
        this.logger = logger

    }

    var bFindReplace = false
    override fun visitTypeInsn(opcode: Int, type: String?) {
        if (privacyExtension?.hookConstructor == true && opcode == Opcodes.NEW && ReplaceClassManager.MANAGER.contains(
                originClassName = type
            )
        ) {
            var replaceItem = ReplaceClassManager.MANAGER.findItemByName(originClassName = type)
            logger.info("visitTypeInsn-ReplaceClassItem - ${replaceItem.toString()}- type is $type- className is $className - methodName is $methodName - descriptor is $methodDesc - isConstructor ${"<init>" == name}")
            mv.visitTypeInsn(
                Opcodes.NEW,
                ReplaceClassManager.MANAGER.findItemByName(originClassName = type)?.proxyClassName?.replace(
                    ".",
                    "/"
                )
            )
            logger.info("visitTypeInsn-ReplaceClassItem - ${replaceItem.toString()}- end")
            bFindReplace = true
            return
        }
        super.visitTypeInsn(opcode, type)
    }

    // 访问方法指令
    override fun visitMethodInsn(
        opcodeAndSource: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        var methodItem =
            HookMethodManager.MANAGER.findHookItemByName(name, owner, descriptor, opcodeAndSource)
        if (methodItem != null && shouldHook(name)) {
            ReplacedMethodManger.MANAGER.addReplaceMethodItem(
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

        if (privacyExtension?.hookConstructor == true && name == "<init>" && opcodeAndSource == Opcodes.INVOKESPECIAL && bFindReplace) {
            var replaceClassItem =
                ReplaceClassManager.MANAGER.findItemByName(originClassName = owner)
            logger.info("visitMethodInsn-find-ReplaceClassItem - ${replaceClassItem.toString()}- owner is $owner - className is $className - methodName is $methodName - - descriptor is $methodDesc - isConstructor ${"<init>" == name}")
            if (replaceClassItem != null && !className.equals(replaceClassItem.proxyClassName)) {
                logger.info("visitMethodInsn-ReplaceClassItem - ${replaceClassItem.toString()}- owner is $owner - className is $className - methodName is $methodName - - descriptor is $methodDesc - isConstructor ${"<init>" == name}")
                mv.visitMethodInsn(
                    opcodeAndSource,
                    replaceClassItem.proxyClassName.replace(".", "/"),
                    name,
                    descriptor,
                    isInterface
                )
                logger.info("visitMethodInsn-ReplaceClassItem end- ${replaceClassItem.toString()}")
                bFindReplace = false
                return
            }
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }


    override fun visitFrame(
        type: Int,
        numLocal: Int,
        local: Array<out Any>?,
        numStack: Int,
        stack: Array<out Any>?
    ) {
        super.visitFrame(type, numLocal, local, numStack, stack)
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

