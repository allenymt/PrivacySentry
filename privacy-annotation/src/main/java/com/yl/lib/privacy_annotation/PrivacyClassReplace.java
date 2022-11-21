package com.yl.lib.privacy_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulun
 * @since 2022-11-18 14:29
 * 类替换，
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyClassReplace {
    Class originClass();
}
