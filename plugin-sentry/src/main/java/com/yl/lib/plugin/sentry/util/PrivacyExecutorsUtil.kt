package com.yl.lib.plugin.sentry.util

import com.didiglobal.booster.kotlinx.NCPU
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author yulun
 * @since 2023-08-09 16:39
 */
class PrivacyExecutorsUtil {
    companion object {
        private val executors = Executors.newFixedThreadPool(NCPU)

        fun submit(runnable: Runnable): Future<*> {
            return executors.submit(runnable)
        }

    }

}