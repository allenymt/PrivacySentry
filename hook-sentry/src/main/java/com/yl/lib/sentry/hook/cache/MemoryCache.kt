package com.yl.lib.sentry.hook.cache

import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-10-17 11:39
 */
class MemoryCache<T> : BasePrivacyCache<T>(PrivacyCacheType.MEMORY) {

    //部分字段只需要读取一次
    // 部分SDK在子线程读取，需要声明可见性
    private var paramMap: ConcurrentHashMap<String, T> = ConcurrentHashMap()

    override fun get(key: String, default: T): Pair<Boolean, T?> {
        return if (paramMap.containsKey(key)) {
            Pair(true, paramMap[key] as? T ?: default)
        } else {
            Pair(false, null)
        }
    }

    override fun put(key: String, value: T) {
        paramMap[key] = value
    }
}