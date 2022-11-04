package com.yl.lib.sentry.hook.util

import androidx.annotation.Keep
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.cache.DiskCache

/**
 * @author yulun
 * @since 2022-11-02 15:28
 */
@Keep
class PrivacyClipBoardManager {
    @Keep
    companion object {

        private val diskCache by lazy {
            DiskCache()
        }
        var bReadClipboardEnable: Boolean? = null

        /**
         * 打开 读取系统剪贴板
         */
        fun openReadClipboard() {
            PrivacySentry.Privacy.getBuilder()?.enableReadClipBoard(true)
            syncReadClipboardEnable(true)
        }

        /**
         * 关闭 读取系统剪贴板
         */
        fun closeReadClipboard() {
            PrivacySentry.Privacy.getBuilder()?.enableReadClipBoard(false)
            syncReadClipboardEnable(false)
        }

        /**
         * 当前读取系统剪贴板是否开启，默认true
         * @return Boolean
         */
        fun isReadClipboardEnable(): Boolean {
            if (bReadClipboardEnable == null) {
                var strEnable = diskCache.get("isReadClipboardEnable", "true")?.second
                bReadClipboardEnable = strEnable?.toBoolean() ?: true
            }
            return (PrivacySentry.Privacy.getBuilder()?.isEnableReadClipBoard()
                ?: true) && (bReadClipboardEnable ?: true)
        }

        private fun syncReadClipboardEnable(bEnable: Boolean) {
            if (bEnable != bReadClipboardEnable) {
                bReadClipboardEnable = bEnable
                diskCache.put("isReadClipboardEnable", bEnable.toString())
            }
        }
    }

}