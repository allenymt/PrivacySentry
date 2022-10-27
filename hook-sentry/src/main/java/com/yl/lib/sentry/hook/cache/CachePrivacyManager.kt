package com.yl.lib.sentry.hook.cache

import android.location.Location
import android.text.TextUtils
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil
import java.io.File

/**
 * @author yulun
 * @since 2022-10-18 10:39
 */
class CachePrivacyManager {
    object Manager {
        private val dickCache: DiskCache by lazy {
            DiskCache()
        }

        // 不同字段可能对时间的要求不一样
        private val timeDiskCache: TimeLessDiskCache by lazy {
            TimeLessDiskCache()
        }

        private val memoryCache: MemoryCache<Any> by lazy {
            MemoryCache<Any>()
        }

        fun <T> loadWithMemoryCache(
            key: String,
            methodDocumentDesc: String,
            defaultValue: T,
            getValue: () -> T
        ): T {
            var result = getCacheParam(key, defaultValue, PrivacyCacheType.MEMORY)
            return handleData(
                key,
                methodDocumentDesc,
                defaultValue,
                getValue,
                result,
                PrivacyCacheType.MEMORY
            )
        }

        fun <T> loadWithDiskCache(
            key: String,
            methodDocumentDesc: String,
            defaultValue: T,
            getValue: () -> T
        ): T {
            var result = getCacheParam(key, defaultValue, PrivacyCacheType.PERMANENT_DISK)
            return handleData(
                key,
                methodDocumentDesc,
                defaultValue,
                getValue,
                result,
                PrivacyCacheType.PERMANENT_DISK
            )
        }

        fun <T> loadWithTimeCache(
            key: String,
            methodDocumentDesc: String,
            defaultValue: T,
            duration: Long = CacheUtils.Utils.MINUTE * 30,
            getValue: () -> T
        ): T {
            var result = getCacheParam(
                TimeLessDiskCache.Util.buildKey(key, duration),
                defaultValue,
                PrivacyCacheType.TIMELINESS_DISK
            )
            return handleData(
                key,
                methodDocumentDesc,
                defaultValue,
                getValue,
                result,
                PrivacyCacheType.TIMELINESS_DISK
            )
        }

        private fun <T> handleData(
            key: String,
            methodDocumentDesc: String,
            defaultValue: T,
            getValue: () -> T,
            cacheResult: Pair<Boolean, T>,
            cacheType: PrivacyCacheType
        ): T {
            if (cacheResult.first) {
                PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc, bCache = true)
                return cacheResult.second
            }
            PrivacyProxyUtil.Util.doFilePrinter(key, methodDocumentDesc)
            var value: T? = null
            try {
                value = getValue()
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                putCacheParam(value ?: defaultValue, key, cacheType)
            }
            return value ?: defaultValue
        }


        /**
         * 获取该进程内已经缓存的静态字段
         * @param defaultValue T
         * @param key String
         * @return T
         */
        private fun <T> getCacheParam(
            key: String,
            defaultValue: T,
            cacheType: PrivacyCacheType
        ): Pair<Boolean, T> {
            var cacheValue = when (cacheType) {
                PrivacyCacheType.MEMORY -> memoryCache.get(key, defaultValue as Any)
                PrivacyCacheType.PERMANENT_DISK -> dickCache.get(key, defaultValue.toString())
                PrivacyCacheType.TIMELINESS_DISK -> timeDiskCache.get(key, defaultValue.toString())
            }
            return if (cacheValue.first) {
                Pair(true, cacheValue.second as T)
            } else {
                var value = cacheValue.second
                if (isEmpty(value)) {
                    value = defaultValue
                }
                Pair(false, value as T)
            }
        }

        /**
         * 设置字段
         * @param value T
         * @param key String
         */
        private fun <T> putCacheParam(value: T, key: String, cacheType: PrivacyCacheType) {
            value?.also {
                when (cacheType) {
                    PrivacyCacheType.MEMORY -> memoryCache.put(key, value)
                    PrivacyCacheType.PERMANENT_DISK -> dickCache.put(key, value.toString())
                    PrivacyCacheType.TIMELINESS_DISK -> timeDiskCache.put(key, value.toString())
                }
            }
        }

        private fun isEmpty(value: Any?): Boolean {
            if (value == null) {
                return true
            } else if (value is String && TextUtils.isEmpty(value)) {
                return true
            } else if (value is List<*> && value.isEmpty()) {
                return true
            } else if (value is Map<*, *> && value.isEmpty()) {
                return true
            } else if (value is File && value.absolutePath.equals("/")) {
                return true
            } else if (value is Location && (value.latitude == 0.0 || value.longitude == 0.0)) {
                return true
            }
            return false
        }

    }

}
