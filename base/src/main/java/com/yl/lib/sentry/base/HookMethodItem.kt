package com.yl.lib.sentry.base

/**
 * @author yulun
 * @sinice 2021-12-14 11:44
 */
class HookMethodItem {
    var className:String
    var methodName:String
    var methodReturnDesc:String
    var methodDesc:String

    constructor(
        className: String,
        methodName: String,
        methodReturnDesc: String,
        methodDesc: String
    ) {
        this.className = className
        this.methodName = methodName
        this.methodReturnDesc = methodReturnDesc
        this.methodDesc = methodDesc
    }
}