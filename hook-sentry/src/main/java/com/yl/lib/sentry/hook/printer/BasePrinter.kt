package com.yl.lib.sentry.hook.printer

/**
 * @author yulun
 * @sinice 2021-09-24 15:43
 */
abstract class BasePrinter {

    constructor()

    abstract fun logPrint(msg:String)

    abstract fun filePrint(funName:String, funAlias:String, msg:String)
}