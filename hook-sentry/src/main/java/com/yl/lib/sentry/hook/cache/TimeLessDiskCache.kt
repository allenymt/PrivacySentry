package com.yl.lib.sentry.hook.cache

import android.text.TextUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-10-17 11:46
 *
 */
class TimeLessDiskCache : BasePrivacyCache<String>(PrivacyCacheType.TIMELINESS_DISK) {
    // 加个内存级别，避免频繁读sp
    private var paramMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    override fun get(key: String, default: String): Pair<Boolean, String?> {
        var value: String = if (paramMap.containsKey(key)) {
            paramMap[key] ?: default
        } else {
            var cache = CacheUtils.Utils.loadFromSp(key, "")
            if (cache.first) {
                cache.second?.toString() ?: default
            } else {
                cache.second?.toString() ?: default
            }
        }

        value?.let {
            return if (CacheUtils.Utils.isValid(it)) {
                Pair(true, CacheUtils.Utils.parseValue(it, default))
            } else {
                CacheUtils.Utils.clearData(key)
                Pair(false, default)
            }
        }
        return Pair(false, default)
    }

    override fun put(key: String, value: String) {
        put(key, value, 0)
    }


    fun put(key: String, value: String, duration: Long) {
        if (TextUtils.isEmpty(key) || value == null) {
            return
        }
        var formatValue = CacheUtils.Utils.buildTimeValue(value, duration)
        paramMap[key] = formatValue
        CacheUtils.Utils.saveToSp(
            key,
            formatValue
        )
    }

}