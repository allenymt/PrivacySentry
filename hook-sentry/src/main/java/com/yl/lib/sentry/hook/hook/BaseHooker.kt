package com.yl.lib.sentry.hook.hook

/**
 * @author yulun
 * @sinice 2021-09-24 15:28
 */
open abstract class BaseHooker {
    var baseHookerHookBuilder: BaseHookBuilder? = null

    constructor(baseHookerHookBuilder: BaseHookBuilder?) {
        this.baseHookerHookBuilder = baseHookerHookBuilder
    }

    abstract fun hook()

}