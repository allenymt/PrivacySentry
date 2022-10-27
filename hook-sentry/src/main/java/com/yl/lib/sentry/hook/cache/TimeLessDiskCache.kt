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

    object Util {
        private var sep = "|"
        fun buildKey(key: String, duration: Long): String {
            return "$key$sep$duration"
        }

        fun parseKey(key: String): Pair<String, Long> {
            var index = key.lastIndexOf(sep)
            if (index == -1) {
                return Pair(key, 0)
            }
            return Pair(key.substring(0, index), key.substring(index + 1).toLong())
        }
    }

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
        if (TextUtils.isEmpty(key) || value == null) {
            return
        }
        var parseValue  = Util.parseKey(key)
        var formatValue = CacheUtils.Utils.buildTimeValue(value, parseValue.second)
        paramMap[key] = formatValue
        CacheUtils.Utils.saveToSp(
            parseValue.first,
            formatValue
        )
    }



}