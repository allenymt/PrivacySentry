package com.yl.lib.privacy_proxy

import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil

/**
 * @author yulun
 * @since 2022-01-13 17:58
 * 以下不可读或者只能读一次
IMEI：
读一次
android.telephony.TelephonyManager.getImei
读一次
android.telephony.TelephonyManager.getDeviceId

IMSI:
读一次
android.telephony.TelephonyManager.getSubscriberId

设备序列号：
读一次
android.os.Build.getSerial


软件列表
读一次，做个缓存
android.app.ApplicationPackageManager.getInstalledPackagesAsUser
android.app.ApplicationPackageManager.getInstalledPackages
根据参数过滤，不能读全量的
android.app.ApplicationPackageManager.queryIntentActivitiesAsUser

MAC
只能读取一次
java.net.NetworkInterface.getHardwareAddress

Android_id
做缓存，单进程内读取一次

剪切板读取问题
1. 单位时间内 做频率和缓存控制
2. 需要隐私协议里补充声明

遗留问题：
多进程读取字段算不算超频违规？ 避免次数过多，这个很烦
只能业务上控制了

以上只是做个粗略的划分，接下来对于所有字段，
1. 单进程内只能读取一次的固定字段，做静态缓存，避免重复读取
2. 部分字段设置时长有效性，超过时长后再生效
 */
class PrivacyProxyUtil {
    object Util {
        fun doFilePrinter(
            funName: String,
            methodDocumentDesc: String = "",
            args: String? = "",
            bVisitorModel: Boolean = false,
            bCache: Boolean = false
        ) {
            if (PrivacySentry.Privacy.getBuilder()?.isEnableFileResult() == false) {
                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc,EnableFileResult=false")
                return
            }
            if (bVisitorModel) {
                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc,isVisitorModel=true")
                return
            }
            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    funName,
                    (if (bCache) "命中缓存--" else "") + methodDocumentDesc + if (args?.isNotEmpty() == true) "--参数: $args" else "",
                    PrivacyUtil.Util.getStackTrace()
                )
            }
        }

        //部分字段只需要读取一次
        //IMEI：
        //        读一次
        //        android.telephony.TelephonyManager.getImei
        //        读一次
        //        android.telephony.TelephonyManager.getDeviceId
        //
        //        IMSI:
        //        读一次
        //        android.telephony.TelephonyManager.getSubscriberId
        //
        //        设备序列号：
        //        读一次
        //        android.os.Build.getSerial
        //
        //
        //        软件列表
        //        读一次，做个缓存
        //        android.app.ApplicationPackageManager.getInstalledPackagesAsUser
        //        android.app.ApplicationPackageManager.getInstalledPackages
        //        根据参数过滤，不能读全量的
        //        android.app.ApplicationPackageManager.queryIntentActivitiesAsUser
        //
        //        MAC
        //        只能读取一次
        //        java.net.NetworkInterface.getHardwareAddress
        //
        //        Android_id
        //        做缓存，单进程内读取一次
        //
        private var staticParamMap: HashMap<String, Any> = HashMap()

        /**
         * 获取该进程内已经缓存的静态字段
         * @param defaultValue T
         * @param key String
         * @return T
         */
        fun <T> getCacheStaticParam(defaultValue: T, key: String): T {
            var cacheValue = staticParamMap[key]
            return if (cacheValue == null) {
                staticParamMap[key] = defaultValue as Any
                defaultValue
            } else {
                cacheValue as T
            }
        }

        /**
         * 设置字段
         * @param value T
         * @param key String
         */
        fun <T> putCacheStaticParam(value: T, key: String) {
            value?.also {
                staticParamMap[key] = it as Any
            }
        }

        /**
         * 是否有读取过这个静态值
         * @param key String
         * @return Boolean
         */
        fun hasReadStaticParam(key: String): Boolean {
            var cacheValue = staticParamMap[key]
            return cacheValue != null
        }

        // 有效时长仅限于5s内？超过一定时长容易引起业务错误。。，这里引起业务错误就很麻烦了，非常难排查，这里感觉适合在业务上做
        // 部分字段短时间内可以设置有效时长，降低读取频率
        // 剪贴板？
        // 位置信息
        fun getTimeOutParam() {}

        // 检查queryIntentActivities参数是否合法，不能查询所有已安装的AC
        fun checkQueryIntentActivities() {}
    }

}