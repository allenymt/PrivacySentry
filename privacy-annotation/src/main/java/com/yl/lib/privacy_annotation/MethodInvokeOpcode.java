package com.yl.lib.privacy_annotation;

/**
 * @author yulun
 * @sinice 2021-12-31 11:12
 * 猜测是编译顺序的关系，当插件里引用这个类时，插件里是kotlin代码，opcode计算出来都是0
 */
public class MethodInvokeOpcode {

    /***
     *   int INVOKEVIRTUAL = 182; // visitMethodInsn
     *   int INVOKESPECIAL = 183; // -
     *   int INVOKESTATIC = 184; // -
     *   int INVOKEINTERFACE = 185; // -
     *   int INVOKEDYNAMIC = 186; // visitInvokeDynamicInsn
     */
    // 调用对象的实例方法
    public static final int INVOKEVIRTUAL = 182;

    // 调用特殊方法，比如初始化，私有方法，父类方法
    public static final int INVOKESPECIAL = 183;

    // 调用类方法，也就是静态方法
    public static final int INVOKESTATIC = 184;

    // 接口方法
    public static final int INVOKEINTERFACE = 185;

    // 动态方法,支持解释性语言
    public static final int INVOKEDYNAMIC = 186;

}
