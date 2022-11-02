package com.yl.lib.sentry.hook.util

import androidx.annotation.Keep
import com.yl.lib.sentry.hook.PrivacySentry

/**
 * @author yulun
 * @since 2022-11-02 15:28
 */
@Keep
class PrivacyClipBoardManager {
    @Keep
    companion object{
        /**
         * 打开 读取系统剪贴板
         */
        fun openReadClipboard() {
            PrivacySentry.Privacy.getBuilder()?.enableReadClipBoard(true)
        }

        /**
         * 关闭 读取系统剪贴板
         */
        fun closeReadClipboard() {
            PrivacySentry.Privacy.getBuilder()?.enableReadClipBoard(false)
        }

        /**
         * 当前读取系统剪贴板是否开启，默认true
         * @return Boolean
         */
        fun isReadClipboardEnable(): Boolean {
            return PrivacySentry.Privacy.getBuilder()?.isEnableReadClipBoard() ?: true
        }
    }

}