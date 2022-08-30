package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author yulun
 * @sinice 2021-12-21 14:29
 * 收集待替换方法的代理类
 */
class CollectHookMethodClassAdapter : ClassVisitor {
    private var className: String = ""

    private var bHookClass: Boolean = false
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

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
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
            return CollectHookMethodAdapter(
                api,
                methodVisitor,
                access,
                name,
                descriptor,
                privacyExtension,
                className
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
        if (bHookClass) {
            var methodVisitor = cv.visitField(access, name, descriptor, signature, value)
            return CollectHookFieldVisitor(
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


class CollectHookMethodAdapter : AdviceAdapter {

    private var privacyExtension: PrivacyExtension? = null
    private var className: String

    constructor(
        api: Int,
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        privacyExtension: PrivacyExtension?,
        className: String
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.privacyExtension = privacyExtension
        this.className = className
    }


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyMethodProxy;") == true) {
            var avr = mv.visitAnnotation(descriptor, visible)
            return CollectMethodHookAnnotationVisitor(
                api,
                avr,
                HookMethodItem(
                    proxyClassName = className,
                    proxyMethodName = name,
                    proxyMethodReturnDesc = methodDesc
                )
            )
        }
        return super.visitAnnotation(descriptor, visible)
    }


}

class CollectMethodHookAnnotationVisitor : AnnotationVisitor {
    private var hookMethodItem: HookMethodItem? = null

    constructor(
        api: Int,
        annotationVisitor: AnnotationVisitor?,
        hookMethodItem: HookMethodItem?
    ) : super(api, annotationVisitor) {
        this.hookMethodItem = hookMethodItem
    }

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        if (name.equals("originalClass")) {
            var classSourceName = value.toString()
            hookMethodItem?.originClassName =
                classSourceName.substring(1, classSourceName.length - 1)
        } else if (name.equals("originalMethod")) {
            hookMethodItem?.originMethodName = value.toString()
        }
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
        super.visitEnum(name, descriptor, value)
        if (name.equals("originalOpcode")) {
            hookMethodItem?.originMethodAccess = MethodInvokeOpcode.valueOf(value.toString()).opcode
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        if (hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKESTATIC.opcode) {
            hookMethodItem?.originMethodDesc = hookMethodItem?.proxyMethodDesc
        } else if (hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKEVIRTUAL.opcode ||
            hookMethodItem?.originMethodAccess == MethodInvokeOpcode.INVOKEINTERFACE.opcode
        ) {
            // 如果是调用实例方法，代理方法的描述会比原始方法多了一个实例，这里需要裁剪，方便做匹配 、、、
            hookMethodItem?.originMethodDesc =
                hookMethodItem?.proxyMethodDesc?.replace("L${hookMethodItem?.originClassName};", "")
        }
        HookMethodManager.MANAGER.appendHookMethod(hookMethodItem!!)
    }
}

class CollectHookFieldVisitor : FieldVisitor {
    private var className: String
    private var fieldName: String?
    private var proxyDescriptor: String?

    constructor(
        api: Int,
        fieldVisitor: FieldVisitor,
        className: String,
        fieldName: String?,
        descriptor: String?
    ) : super(api, fieldVisitor) {
        this.className = className
        this.fieldName = fieldName
        this.proxyDescriptor = descriptor
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor?.equals("Lcom/yl/lib/privacy_annotation/PrivacyFieldProxy;") == true) {
            var avr = fv.visitAnnotation(descriptor, visible)
            return CollectFieldHookAnnotationVisitor(
                api,
                avr,
                HookFieldItem(
                    proxyClassName = className,
                    proxyFieldName = fieldName ?: "",
                    proxyFieldDesc = proxyDescriptor ?: ""
                )
            )
        }
        return super.visitAnnotation(descriptor, visible)
    }

}

class CollectFieldHookAnnotationVisitor : AnnotationVisitor {
    private var hookFieldItem: HookFieldItem? = null

    constructor(
        api: Int,
        annotationVisitor: AnnotationVisitor?,
        hookFieldItem: HookFieldItem
    ) : super(api, annotationVisitor) {
        this.hookFieldItem = hookFieldItem
    }

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        if (name.equals("originalClass")) {
            var classSourceName = value.toString()
            hookFieldItem?.originClassName =
                classSourceName.substring(1, classSourceName.length - 1)
        } else if (name.equals("originalFieldName")) {
            hookFieldItem?.originFieldName = value.toString()
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        hookFieldItem?.let {
            HookFieldManager.MANAGER.appendHookField(it)
        }

    }
}