package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.manager.HookedDataManger
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import com.yl.lib.plugin.sentry.util.formatName
import com.yl.lib.plugin.sentry.util.formatSuperName
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode

/**
 * @author yulun
 * @since 2023-08-08 10:55
 * 处理Service的onStartCommand方法，把返回值强行修改为
 */
class ServiceHookTransform : BaseHookTransform() {

    override fun ignoreClass(context: TransformContext, klass: ClassNode): Boolean {
        var ignore = super.ignoreClass(context, klass)
        if (!ignore) {
            ignore = !PrivacyPluginUtil.privacyPluginUtil.isService(klass.formatName(), klass.formatSuperName())
        }
        return ignore
    }

    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        if (!privacyExtension.enableHookServiceStartCommand){
            return klass
        }
        HookedDataManger.MANAGER.addHookService(klass.formatName())
        var onStartCommandMethod = klass.methods.find { it.name == "onStartCommand" }
        if (onStartCommandMethod == null) {
            val onStartCommand = klass.visitMethod(
                Opcodes.ACC_PUBLIC,
                "onStartCommand",
                "(Landroid/content/Intent;II)I",
                null,
                null
            )
            onStartCommand.visitCode()
            onStartCommand.visitInsn(Opcodes.ICONST_2)
            onStartCommand.visitInsn(groovyjarjarasm.asm.Opcodes.IRETURN)
            onStartCommand.visitMaxs(1, 1)
            onStartCommand.visitEnd()
        } else {
            onStartCommandMethod.instructions.iterator().asSequence().forEach {
                if (it is InsnNode) {
                    if (it.opcode == Opcodes.IRETURN) {
                        val newInstructions = InsnList()
                        newInstructions.add(InsnNode(Opcodes.ICONST_2))
                        newInstructions.add(InsnNode(Opcodes.IRETURN))
                        onStartCommandMethod.instructions.insertBefore(it, newInstructions)
                        onStartCommandMethod.instructions.remove(it)
                    }
                }
            }
        }
        return klass
    }
}