package com.yl.lib.sentry.hook.printer

/**
 * @author yulun
 * @sinice 2021-09-24 15:43
 */
abstract  class BasePrinter {
    constructor()

    abstract fun print(name:String,msg:String)
}