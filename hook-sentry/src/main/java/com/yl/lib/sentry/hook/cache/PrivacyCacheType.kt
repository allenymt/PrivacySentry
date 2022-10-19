package com.yl.lib.sentry.hook.cache

/**
 * @author yulun
 * @since 2022-10-14 17:55
 */
enum class PrivacyCacheType {
    /**
     * 内存缓存
     */
    MEMORY,

    /**
     * 磁盘，带有过期时间
     */
    TIMELINESS_DISK,

    /**
     * 磁盘，永久(app缓存不被删，就一直在)
     */
    PERMANENT_DISK

}