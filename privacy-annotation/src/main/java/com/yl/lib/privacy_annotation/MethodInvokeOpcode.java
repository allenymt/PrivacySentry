package com.yl.lib.privacy_annotation;


import org.objectweb.asm.Opcodes;

/**
 * @author yulun
 * @sinice 2021-12-31 11:12
 */
public enum MethodInvokeOpcode {

    /***
     *   int INVOKEVIRTUAL = 182; // visitMethodInsn
     *   int INVOKESPECIAL = 183; // -
     *   int INVOKESTATIC = 184; // -
     *   int INVOKEINTERFACE = 185; // -
     *   int INVOKEDYNAMIC = 186; // visitInvokeDynamicInsn
     */
    // 调用对象的实例方法
    INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL),

    // 调用特殊方法，比如初始化，私有方法，父类方法
    INVOKESPECIAL(Opcodes.INVOKESPECIAL),

    // 调用类方法，也就是静态方法
    INVOKESTATIC(Opcodes.INVOKESTATIC),

    // 调用接口方法
    INVOKEINTERFACE(Opcodes.INVOKEINTERFACE),

    // 暂时还不清楚
    INVOKEDYNAMIC(Opcodes.INVOKEDYNAMIC);

    private int opcode;

    MethodInvokeOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }
}
