package com.yl.lib.sentry.hook.cache

import android.text.TextUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-10-17 11:46
 *
 */
class TimeLessMemoryCache<T> : BasePrivacyCache<T>(PrivacyCacheType.TIMELINESS_DISK) {
    // 加个内存级别，避免频繁读sp
    private var paramMap: ConcurrentHashMap<String, T> = ConcurrentHashMap()

    private var timeMap: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    override fun get(key: String, default: T): Pair<Boolean, T?> {
        var value: T? = if (paramMap.containsKey(key)) {
            paramMap[key] ?: default
        } else {
            null
        }

        value?.let {
            if (System.currentTimeMillis() > timeMap[key]!!) {
                Pair(true, value)
            } else {
                paramMap.remove(key)
                timeMap.remove(key)
                Pair(false, default)
            }
        }
        return Pair(false, default)
    }

    override fun put(key: String, value: T) {
        put(key, value, 0)
    }

    fun put(key: String, value: T, duration: Long) {
        if (TextUtils.isEmpty(key) || value == null) {
            return
        }
        paramMap[key] = value
        timeMap[key] = System.currentTimeMillis() + duration
    }

}