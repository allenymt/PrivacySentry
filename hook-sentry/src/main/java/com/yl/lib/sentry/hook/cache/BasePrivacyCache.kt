package com.yl.lib.sentry.hook.cache

/**
 * @author yulun
 * @since 2022-10-17 11:32
 */
abstract class BasePrivacyCache<T> {
    var cacheType: PrivacyCacheType = PrivacyCacheType.MEMORY

    constructor(cacheType: PrivacyCacheType) {
        this.cacheType = cacheType
    }

    abstract fun get(key: String, default: T): Pair<Boolean,T?>

    abstract fun put(key: String, value: T)
}