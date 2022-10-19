package com.yl.lib.sentry.hook.cache

import java.util.concurrent.ConcurrentHashMap

/**
 * @author yulun
 * @since 2022-10-17 16:56
 */
class DiskCache : BasePrivacyCache<String>(PrivacyCacheType.PERMANENT_DISK) {

    // 加个内存级别，避免频繁读sp
    private var paramMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()


    override fun get(key: String, default: String): Pair<Boolean, String?> {
        if (paramMap.containsKey(key)) {
            return Pair(true, paramMap[key] as String)
        }

        // 读操作可以频繁，文件io只会触发一次
        var cacheResult: Pair<Boolean, String?> =
            CacheUtils.Utils.loadFromSp(key, default) as Pair<Boolean,String?>
        if (cacheResult.first) {
            paramMap[key] = cacheResult.second!!
        }
        return cacheResult
    }


    // TODO 如何优化，避免频繁写入sp
    override fun put(key: String, value: String) {
        paramMap[key] = value
        CacheUtils.Utils.saveToSp(key, value)
    }
}