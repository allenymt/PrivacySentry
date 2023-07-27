package com.yl.lib.plugin.sentry.transform.hook

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import groovyjarjarasm.asm.Opcodes.IRETURN
import org.gradle.api.logging.Logger
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ICONST_2


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

    // 判断当前类是否为Service
    private var bIsService = false
    // 遍历过程中 是否找到OnStartCommand的方法
    private var bFindOnStartCommand = false

    /**
     *  ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    // 如果要新增的方法已经存在，则不需要新增
    if ("myNewMethod".equals(name)) {
    return null;
    }
    // 生成要新增的方法的字节码，生成对应的 MethodVisitor 对象
    if ("<init>".equals(name)) {
    // 如果是构造方法，则在 super() 后面调用 myNewMethod()
    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodVisitor(Opcodes.ASM7, mv) {
    @Override
    public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/example/MyClass", "myNewMethod", "()V", false);
    }
    super.visitInsn(opcode);
    }
    };
    } else {
    // 如果不是构造方法，则直接生成方法字节码
    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodVisitor(Opcodes.ASM7, mv) {
    @Override
    public void visitCode() {
    super.visitCode();
    visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/System", "currentTimeMillis", "()J", false);
    visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
    visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
    visitInsn(Opcodes.RETURN);
    }
    };
    }
    }
    };
     */
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
            bIsService = PrivacyPluginUtil.privacyPluginUtil.isService(className, superName?.replace("/", "."),logger)
            if (bIsService){
                logger.info("HookClassVisitor visit ServiceClassName = $className")
            }
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
        if (privacyExtension?.enableHookServiceStartCommand == true && bIsService && name.equals("onStartCommand") && descriptor.equals("(Landroid/content/Intent;II)I")) {
            logger.info("HookClassVisitor visit find bFindOnStartCommand, className is $className")
            bFindOnStartCommand = true
            var methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
            return HookServiceMethodVisitor( api,
                methodVisitor,
                access,
                name,
                descriptor,
                privacyExtension,
                className,
                logger)
        }

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

    override fun visitEnd() {
        if (privacyExtension?.enableHookServiceStartCommand == true && bIsService && !bFindOnStartCommand){
            logger.info("HookClassVisitor visit add OnStartCommand, className is $className")
            // 新增OnStartCommand 方法
            val onStartCommand = cv.visitMethod(ACC_PUBLIC, "onStartCommand", "(Landroid/content/Intent;II)I", null, null)
            onStartCommand.visitCode()
            onStartCommand.visitInsn(ICONST_2)
            onStartCommand.visitInsn(IRETURN)
            onStartCommand.visitMaxs(1, 1)
            onStartCommand.visitEnd()
        }
        cv.visitEnd()
    }
}
