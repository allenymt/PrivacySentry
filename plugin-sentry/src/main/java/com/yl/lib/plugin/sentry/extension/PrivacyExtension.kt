package com.yl.lib.plugin.sentry.extension

/**
 * @author yulun
 * @sinice 2021-12-13 17:28
 */
open class PrivacyExtension {
    // 不修改的黑名单，首先是包括自己
    var blackList: Set<String>? = null

    var hookClassPath: String? = "com.yl.lib.plugin_proxy.PrivacyProxy".replace(".", "/")

    // 接口就固定了，不再提供修改
    val hookInterfacePath: String = "com.yl.lib.plugin_proxy.IPrivacyProxy".replace(".", "/")
}