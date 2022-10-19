package com.yl.lib.sentry.hook.cache

import android.text.TextUtils
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.Delegates

/**
 * @author yulun
 * @since 2022-10-17 11:46
 *
 */
class TimeLessDiskCache : BasePrivacyCache {
    var duration by Delegates.notNull<Long>()

    // 加个内存级别，避免频繁读sp
    private var paramMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    constructor(
        duration: Long
    ) : super(PrivacyCacheType.TIMELINESS_DISK) {
        this.duration = duration
    }

    override fun <T> get(key: String, default: T): T? {
        var value = if (paramMap.contains(key)) {
            paramMap[key]
        } else {
            CacheUtils.Utils.loadFromSp(key, "")
        }
        value?.let {
            return if (CacheUtils.Utils.isValid(it.toString())) {
                CacheUtils.Utils.parseValue(it.toString(),default)
            } else {
                CacheUtils.Utils.clearData(key)
                default
            }
        }
        return null
    }

    override fun put(key: String, value: Any) {
        if (TextUtils.isEmpty(key) || value == null) {
            return
        }

        var formatValue = CacheUtils.Utils.buildTimeValue(value.toString(), duration!!)
        paramMap[key] = formatValue
        CacheUtils.Utils.saveToSp(
            key,
            formatValue
        )
    }


}