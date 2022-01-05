package com.yl.lib.privacy_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulun
 * @sinice 2021-12-31 09:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyMethodProxy {
    // 原始类
    Class originalClass();

    // 原始的方法名
    String originalMethod() default "";

    // 原始方法的描述信息，对于方法的返回值，我们默认为代理方法和原始方法要保持一致，所以这里只需要记录描述信息即可
    // 默认是MethodInvokeOpcode.INVOKESTATIC
    MethodInvokeOpcode originalOpcode() default MethodInvokeOpcode.INVOKESTATIC;

    // 文档注释描述信息
    String documentDesc() default "";
}
