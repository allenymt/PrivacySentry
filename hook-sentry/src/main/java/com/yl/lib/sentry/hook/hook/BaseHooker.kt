package com.yl.lib.sentry.hook.hook

import android.app.Application
import android.os.Build
import com.yl.lib.sentry.hook.hook.BaseHookBuilder

/**
 * @author yulun
 * @sinice 2021-09-24 15:28
 */
open abstract class BaseHooker {
    var baseHookerHookBuilder: BaseHookBuilder? = null

    constructor(baseHookerHookBuilder: BaseHookBuilder?) {
        this.baseHookerHookBuilder = baseHookerHookBuilder
    }

    fun legalAndroidVersion(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        return true
    }

    /**
     * 开启hook
     */
    abstract fun hook(ctx: Application)

    /**
     * 日志监听结束后，还原反射hook的变量，避免anr等系统异常
     */
    abstract fun reduction(ctx: Application)

}