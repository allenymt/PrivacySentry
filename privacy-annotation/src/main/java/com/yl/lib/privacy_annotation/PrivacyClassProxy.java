package com.yl.lib.privacy_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulun
 * @sinice 2022-01-04 10:04
 * 有这个注解的类，才会被解析
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyClassProxy {

}
