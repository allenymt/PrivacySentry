package com.yl.lib.sentry.hook.util

import com.yl.lib.sentry.hook.PrivacySentry
import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-01-13 17:58
 */
class PrivacyProxyUtil {
    object Util{
        fun doFilePrinter(
            funName: String,
            methodDocumentDesc: String = "",
            args: String? = "",
            bVisitorModel: Boolean = false,
            bCache: Boolean = false
        ) {
//            if (PrivacySentry.Privacy.getBuilder()?.isEnableFileResult() == false) {
//                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc,EnableFileResult=false")
//                return
//            }
            if (bVisitorModel) {
                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc,isVisitorModel=true")
                return
            }
            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    funName + "-\n线程名: ${Thread.currentThread().name}",
                    (if (bCache) "命中缓存--" else "") + methodDocumentDesc + if (args?.isNotEmpty() == true) "--参数: $args" else "",
                    PrivacyUtil.Util.getStackTrace()
                )
            }
        }

        //部分字段只需要读取一次
        // 部分SDK在子线程读取，需要声明可见性
        private var staticParamMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

        /**
         * 获取该进程内已经缓存的静态字段
         * @param defaultValue T
         * @param key String
         * @return T
         */
        fun <T> getCacheStaticParam(defaultValue: T, key: String): T {
            var cacheValue = staticParamMap[key]
            return if (cacheValue == null) {
                staticParamMap.put(key, defaultValue as Any)
                defaultValue
            } else {
                cacheValue as T
            }
        }

        /**
         * 设置字段
         * @param value T
         * @param key String
         */
        fun <T> putCacheStaticParam(value: T, key: String) {
            value?.also {
                staticParamMap[key] = it as Any
            }
        }

        /**
         * 是否有读取过这个静态值
         * @param key String
         * @return Boolean
         */
        fun hasReadStaticParam(key: String): Boolean {
            var hasCacheValue = false
            hasCacheValue = staticParamMap[key] != null
            return hasCacheValue
        }
    }

}
