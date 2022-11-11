package com.yl.lib.sentry.hook.util

import java.lang.reflect.Method

/**
 * @author yulun
 * @since 2022-11-10 15:06
 * https://juejin.cn/post/7018030594156134408 会触发反射数组长度的错误
 */
class ReflectUtils {
    object Utils {
        fun <T> invokeSuperMethod(
            obj: Any,
            name: String,
            types: Array<Class<*>>,
            args: Array<Any?>
        ): T? {
            try {
                val method: Method? = getMethod(obj.javaClass.superclass, name, types)
                if (null != method) {
                    method.isAccessible = true
                    return method.invoke(obj, *args) as T
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return null
        }

        private fun getMethod(klass: Class<*>, name: String, types: Array<Class<*>>): Method? {
            return try {
                klass.getDeclaredMethod(name, *types)
            } catch (e: NoSuchMethodException) {
                val parent = klass.superclass ?: return null
                getMethod(parent, name, types)
            }
        }

        fun <T> invokeMethod(obj: Any, name: String?, types: Array<Class<*>>, args: Array<Any?>): T? {
            try {
                val method = getMethod(obj.javaClass, name!!, types)
                if (null != method) {
                    method.isAccessible = true
                    return method.invoke(obj, *args) as T
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return null
        }
    }

}