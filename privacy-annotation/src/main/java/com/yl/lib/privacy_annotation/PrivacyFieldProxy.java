package com.yl.lib.privacy_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulun
 * @since 2022-08-30 11:39
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyFieldProxy {
    // 原始类
    Class originalClass();

    // 原始的变量名
    String originalFieldName() default "";
}
