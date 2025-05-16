package com.yl.lib.plugin.sentry.extension

/**
 * @author yulun
 * @sinice 2021-12-13 17:28
 */
open class PrivacyExtension {

    var enablePrivacy = true

    // 不修改的黑名单，首先是包括自己
    var blackList: Set<String>? = null

    // hook变量
    @Deprecated("后续准备放弃，几乎没有业务场景")
    var hookField : Boolean = false

    // 记录所有被代理的方法名+类名,将以单行的形式被写入到文件中
    // 空=不写入
    var replaceFileName :String? = "privacy_hook.json"

    // 开启hook反射方法，默认为false
    var hookReflex: Boolean = false
    var reflexMap: Map<String, List<String>>? = null

    // 开启hook构造函数，默认为false
    @Deprecated("放弃维护")
    var hookConstructor: Boolean = false

    @Deprecated("放弃维护")
    var enableProcessManifest : Boolean = false
    // hook Service的部分代码，修复在MIUI上的自启动问题
    // 部分Service把自己的Priority设置为1000，这里开启代理功能，可以代理成0
    @Deprecated("放弃维护")
    var enableReplacePriority = false
    @Deprecated("放弃维护")
    var replacePriority = 0

    // 支持关闭Service的Export功能，默认为false，注意部分厂商通道之类的push，不能关闭
    @Deprecated("放弃维护")
    var enableCloseServiceExport = false
    // Export白名单Service
    @Deprecated("放弃维护")
    var serviceExportPkgWhiteList: Set<String>? = null
    // hook Service的startCommand方法，修复在MIUI上的自启动问题
    @Deprecated("放弃维护")
    var enableHookServiceStartCommand = false
}
