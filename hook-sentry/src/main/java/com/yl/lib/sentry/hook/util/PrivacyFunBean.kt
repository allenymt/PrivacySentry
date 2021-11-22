package com.yl.lib.sentry.hook.util

import android.text.TextUtils
import androidx.collection.ArraySet

/**
 * @author yulun
 * @sinice 2021-11-19 15:24
 */
class PrivacyFunBean {
    /**
     * 类似于我们正常理解中的别名，比如imsi在系统里的函数名并不是imsi
     */
    var funAlias: String? = null
    var funName: String? = null
    var stackTrace: ArraySet<String>? = null
    var count = 0


    constructor(alias: String?, funName: String?, stackTrace: Set<String>?, count: Int) {
        this.funAlias = alias
        this.funName = funName
        this.stackTrace = ArraySet()
        this.count = count
    }

    constructor(alias: String?, funName: String?) {
        this.funAlias = alias
        this.funName = funName
        this.stackTrace = ArraySet()
        this.count = 0
    }

    fun buildStackTrace(): String {
        if (stackTrace == null || stackTrace?.isEmpty() != false) {
            return ""
        }

        return stackTrace?.joinToString("\n\n\n") ?: ""
    }

    fun addStackTrace(strStackTrace: String) {
        if (TextUtils.isEmpty(strStackTrace))
            return
        if (stackTrace == null) {
            stackTrace = ArraySet()
        }
        if (strStackTrace.contains(strStackTrace)) {
            return
        }
        stackTrace?.add(strStackTrace)
    }
}