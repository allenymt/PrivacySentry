package com.yl.lib.sentry.hook.cache

import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-10-17 16:56
 */
class DiskCache : BasePrivacyCache(PrivacyCacheType.PERMANENT_DISK) {

    // 加个内存级别，避免频繁读sp
    private var paramMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    override fun <T> get(key: String, default: T): T? {
        if (paramMap.contains(key)) {
            return paramMap[key] as T
        }
        var any = CacheUtils.Utils.loadFromSp(key, default.toString())
        any?.let { paramMap[key] }
        return any as T
    }


    override fun put(key: String, value: Any) {
        paramMap[key] = value
        CacheUtils.Utils.saveToSp(key, value?.toString() ?: "")
    }
}