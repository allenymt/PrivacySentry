package com.yl.lib.sentry.hook.cache

import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-10-17 11:39
 */
class MemoryCache : BasePrivacyCache(PrivacyCacheType.MEMORY) {

    //部分字段只需要读取一次
    // 部分SDK在子线程读取，需要声明可见性
    private var paramMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    override fun <T> get(key: String, default: T): T? {
        return paramMap[key] as? T ?: default
    }

    override fun put(key: String, value: Any) {
        paramMap[key] = value
    }
}