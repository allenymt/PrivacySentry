package com.yl.lib.sentry.hook.printer

/**
 * @author yulun
 * @sinice 2021-09-24 15:43
 */
open class BasePrinter {

    constructor()

    open fun logPrint(msg:String){}

    open fun filePrint(funName:String, funAlias:String, msg:String){}
}