package com.yl.lib.sentry.hook.hook

import android.content.Context

/**
 * @author yulun
 * @sinice 2021-09-24 15:28
 */
open abstract class BaseHooker {
    var baseHookerHookBuilder: BaseHookBuilder? = null

    constructor(baseHookerHookBuilder: BaseHookBuilder?) {
        this.baseHookerHookBuilder = baseHookerHookBuilder
    }

    abstract fun hook(ctx: Context)

}