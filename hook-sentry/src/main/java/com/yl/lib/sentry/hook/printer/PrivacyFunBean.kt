package com.yl.lib.sentry.hook.printer

import com.yl.lib.sentry.hook.util.PrivacyUtil

/**
 * @author yulun
 * @sinice 2021-11-19 15:24
 * 写入文件记录的单条记录
 */
class PrivacyFunBean {

    // 记录生成的时间
    var appendTime: String? = null

    /**
     * 类似于我们正常理解中的别名，比如imsi在系统里的函数名并不是imsi
     */
    var funAlias: String? = null

    /**
     * 实际的函数名
     */
    var funName: String? = null

    /**
     * 调用的堆栈
     */
    var stackTraces: String? = null

    /**
     * 调用次数，同个堆栈可能调用多次，这个也存在合规风险
     */
    var count = 0


    constructor(alias: String?, funName: String?, stackTrace: String?, count: Int) {
        appendTime = PrivacyUtil.Util.formatTime(System.currentTimeMillis(), "MM-dd HH:mm:ss.SSS")
        this.funAlias = alias
        this.funName = funName
        this.stackTraces = trimTrace(stackTrace)
        this.count = count
    }

    fun addSelf() {
        count++
    }

    fun buildStackTrace(): String {
        if (stackTraces == null || stackTraces?.isEmpty() != false) {
            return ""
        }
        return stackTraces ?: ""
    }

    // 裁剪掉部分冗余重复的trace
    private fun trimTrace(stackStrace: String?): String? {
        if (stackStrace == null || "" == stackStrace)
            return ""
        var sArray = stackStrace?.split("\n")
        var delIndex = sArray?.indexOfFirst { it.contains("java.lang.reflect.Proxy.invoke") }
        if (delIndex != null && delIndex <= 0) {
            return stackStrace
        }
        return sArray?.subList(delIndex!! + 1, sArray.size)?.joinToString(separator = "\n")
    }

    override fun equals(other: Any?): Boolean {
        if (other is PrivacyFunBean) {
            return (
                    other.appendTime == appendTime &&
                            other.funAlias == funAlias &&
                            other.funName == funName && other.stackTraces == stackTraces)
        }

        return super.equals(other)
    }
}