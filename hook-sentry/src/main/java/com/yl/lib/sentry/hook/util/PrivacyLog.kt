package com.yl.lib.sentry.hook.util

import com.yl.lib.sentry.hook.PrivacySentry


/**
 * @author yulun
 * @sinice 2021-06-10 15:09
 */
class PrivacyLog {
    companion object Log {
        private const val TAG = "PrivacyOfficer"

        fun e(msg: String) {
            if (PrivacySentry.Privacy.isDebug()) {
                android.util.Log.e(TAG, msg)
            }
        }

        fun w(msg: String) {
            if (PrivacySentry.Privacy.isDebug()) {
                android.util.Log.w(TAG, msg)
            }
        }

        fun i(msg: String) {
            if (PrivacySentry.Privacy.isDebug()) {
                android.util.Log.i(TAG, msg)
            }
        }
    }
}