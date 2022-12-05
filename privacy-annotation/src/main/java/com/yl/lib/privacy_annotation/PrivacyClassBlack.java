package com.yl.lib.privacy_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulun
 * @since 2022-12-05 11:08
 * 不会被代理，黑名单注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyClassBlack {
}
