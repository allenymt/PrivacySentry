package com.yl.lib.plugin.sentry.extension

/**
 * @author yulun
 * @sinice 2021-12-13 17:28
 */
open class PrivacyExtension {
    // 不修改的黑名单，首先是包括自己
    var blackList: Set<String>? = null

    var hookField : Boolean = false

    // 记录所有被替换的方法名+类名,将以单行的形式被写入到文件中
    // 空=不写入
    var replaceFileName :String? = "replace.json"

    // 开启hook反射方法，默认为false
    var hookReflex: Boolean = false

    // 开启hook构造函数，默认为true
    var hookConstructor: Boolean = true
}