package com.yl.lib.sentry.base

/**
 * @author yulun
 * @sinice 2021-12-14 11:39
 * 汇总所有的hook方法配置
 */
open class HookMethodManager {
    object MANAGER {
        private var hookMethodList: ArrayList<HookMethodItem> = ArrayList()
        private var hookClassPath: String? = ""

        /**
         * 检测是否需要替换某个方法
         * @param methodName String
         * @param classOwnerName String
         * @param methodReturnDesc String
         * @return Boolean
         */
        fun contains(
            methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): Boolean {
            if (methodName == "") {
                return false
            }
            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc)
            } != null
        }

        fun findHookItemByName(
            methodName: String
        ): HookMethodItem? {
            return findHookItemByName(methodName, "", "")
        }

        fun findHookItemByName(
            methodName: String, classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): HookMethodItem? {
            if (methodName == "") {
                return null
            }

            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc)
            }
        }

        private fun isHookMethodItem(
            hookItem: HookMethodItem, methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): Boolean {
            if (methodName.isEmpty()) {
                return false
            }
            return if (classOwnerName.isEmpty() && methodReturnDesc.isNotEmpty()) {
                methodName == hookItem.originMethodName && methodReturnDesc == hookItem.originMethodDesc
            } else if (classOwnerName.isNotEmpty() && methodReturnDesc.isEmpty()) {
                methodName == hookItem.originMethodName && classOwnerName == hookItem.originClassName
            } else if (classOwnerName.isNotEmpty() && methodReturnDesc.isNotEmpty()) {
                methodName == hookItem.originMethodName && classOwnerName == hookItem.originClassName && methodReturnDesc == hookItem.originMethodDesc
            } else {
                methodName == hookItem.originMethodName
            }
        }

        /**
         * 追加hook方法
         * @param originClassName String 被代理方法的类名
         * @param originMethodName String 被代理的方法
         * @param proxyClassName String 代理方法的类名
         * @param proxyMethodName String 代理方法名
         * @param proxyMethodReturnDesc String 代理方法描述=被代理的方法描述
         * @param documentMethodDesc String 方法注释信息
         */
        fun appendHookMethod(
            originClassName: String,
            originMethodName: String,
            originMethodAccess: Int,
            originMethodReturnDesc: String,
            proxyClassName: String,
            proxyMethodName: String,
            proxyMethodReturnDesc: String,
            documentMethodDesc: String
        ) {
            hookMethodList.add(
                HookMethodItem(
                    originClassName = originClassName,
                    originMethodName = originMethodName,
                    originMethodDesc = originMethodReturnDesc,
                    originMethodAccess = originMethodAccess,
                    proxyClassName = proxyClassName,
                    proxyMethodName = proxyMethodName,
                    proxyMethodDesc = proxyMethodReturnDesc,
                    documentMethodDesc = documentMethodDesc
                )
            )
        }

        fun appendHookMethod(
            hookMethodItem: HookMethodItem
        ) {
            if (hookMethodList.contains(hookMethodItem))
                return
            hookMethodList.add(
                hookMethodItem
            )
        }

        /**
         * 设置hook类，只能设置一个，以最后那个设置的为准
         */
        fun setHookClassPath(hookClassPath: String) {
            this.hookClassPath = hookClassPath
        }

        fun getHookClassPath(): String? {
            return hookClassPath
        }


//        private const val asmClsName = "android/app/ActivityManager"
//        private fun buildAmsMethod(): ArrayList<HookMethodItem> {
//            return arrayListOf(
//                HookMethodItem(
//                    asmClsName,
//                    "getRunningTasks",
//                    "(I)Ljava/util/List;",
//                    "获取当前运行任务-getRunningTasks"
//                ),
//                HookMethodItem(
//                    asmClsName,
//                    "getRunningAppProcesses",
//                    "()Ljava/util/List;",
//                    "获取当前运行进程-getRunningAppProcesses"
//                )
//            )
//        }
//
//        private val pmsClsName = "android.content.pm.PackageManager".replace(".", "/")
//        private fun buildPmsMethod(): ArrayList<HookMethodItem> {
//            return arrayListOf(
//                HookMethodItem(
//                    pmsClsName,
//                    "getInstalledPackages",
//                    "(I)Ljava/util/List;",
//                    "获取安装包-getInstalledPackages"
//                ),
//                HookMethodItem(
//                    pmsClsName,
//                    "queryIntentActivities",
//                    "(I)Ljava/util/List;",
//                    "读安装列表-queryIntentActivities"
//                ),
//                HookMethodItem(
//                    pmsClsName,
//                    "queryIntentActivityOptions",
//                    "(Landroid/content/ComponentName;[Landroid/content/Intent;Landroid/content/Intent;I)Ljava/util/List;",
//                    "读安装列表-queryIntentActivityOptions"
//                ),
//
//                // 给runtime-hook用的，所以不需要方法返回信息
//                HookMethodItem(
//                    pmsClsName,
//                    "queryIntentActivitiesAsUser",
//                    "",
//                    "读安装列表-queryIntentActivitiesAsUser"
//                ),
//                HookMethodItem(
//                    pmsClsName,
//                    "getLeanbackLaunchIntentForPackage",
//                    "",
//                    "读安装列表-getLeanbackLaunchIntentForPackage"
//                ),
//                HookMethodItem(
//                    pmsClsName,
//                    "getInstalledPackagesAsUser",
//                    "",
//                    "读安装列表-getInstalledPackagesAsUser"
//                )
//            )
//        }
//
//        private val tmsClsName = "android.telephony.TelephonyManager".replace(".", "/")
//        private fun buildTmsMethod(): ArrayList<HookMethodItem> {
//            return arrayListOf(
//                HookMethodItem(
//                    tmsClsName,
//                    "getMeid",
//                    "(I)Ljava/lang/String;",
//                    "移动设备标识符-getMeid(I)"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getMeid",
//                    "()Ljava/lang/String;",
//                    "移动设备标识符-getMeid"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getDeviceId",
//                    "(I)Ljava/lang/String;",
//                    "获取设备id-getDeviceId(I)"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getDeviceId",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getDeviceId"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getSubscriberId",
//                    "(I)Ljava/lang/String;",
//                    "获取设备id-getSubscriberId(I)"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getSubscriberId",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getSubscriberId"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getImei",
//                    "(I)Ljava/lang/String;",
//                    "获取设备id-getImei(I)"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getImei",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getImei"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getSimSerialNumber",
//                    "(I)Ljava/lang/String;",
//                    "获取设备id-getSimSerialNumber(I)"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getSimSerialNumber",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getSimSerialNumber"
//                ),
//
//                // for runtime hook
//                HookMethodItem(
//                    tmsClsName,
//                    "getDeviceIdWithFeature",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getDeviceIdWithFeature"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getImeiForSlot",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getImeiForSlot"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getSubscriberIdForSubscriber",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getSubscriberIdForSubscriber"
//                ),
//                HookMethodItem(
//                    tmsClsName,
//                    "getIccSerialNumberForSubscriber",
//                    "()Ljava/lang/String;",
//                    "获取设备id-getIccSerialNumberForSubscriber"
//                )
//            )
//        }
//
//        private val cmsClsName = "android.content.ClipboardManager".replace(".", "/")
//        private fun buildCmsMethod(): ArrayList<HookMethodItem> {
//            return arrayListOf(
//                HookMethodItem(
//                    cmsClsName,
//                    "getPrimaryClip",
//                    "()Landroid/content/ClipData;",
//                    "获取剪贴板内容-getPrimaryClip"
//                ),
//                HookMethodItem(
//                    cmsClsName,
//                    "getPrimaryClipDescription",
//                    "()Landroid/content/ClipDescription;",
//                    "获取剪贴板内容-getPrimaryClipDescription"
//                ),
//                HookMethodItem(
//                    cmsClsName,
//                    "getText",
//                    "()Ljava/lang/CharSequence;",
//                    "获取剪贴板内容-getText"
//                ),
//                HookMethodItem(
//                    cmsClsName,
//                    "setPrimaryClip",
//                    "(Landroid/content/ClipData;)V",
//                    "设置剪贴板内容-setPrimaryClip"
//                ),
//                HookMethodItem(
//                    cmsClsName,
//                    "setText",
//                    "(Ljava/lang/CharSequence;)V",
//                    "设置剪贴板内容-setText"
//                )
//            )
//
//        }
//
//        private fun buildOtherMethod(): ArrayList<HookMethodItem> {
//            val resultList = ArrayList<HookMethodItem>()
//
//            //mac
//            resultList.add(
//                HookMethodItem(
//                    "android/net/wifi/WifiInfo",
//                    "getMacAddress",
//                    "()Ljava/lang/String;",
//                    "获取mac地址-getMacAddress"
//                )
//            )
//
//            // 硬件地址
//            resultList.add(
//                HookMethodItem(
//                    "java/net/NetworkInterface",
//                    "getHardwareAddress",
//                    "()[B",
//                    "获取硬件地址-getHardwareAddress"
//                )
//            )
//
//            // 蓝牙
//            resultList.add(
//                HookMethodItem(
//                    "android/bluetooth/BluetoothAdapter",
//                    "getAddress",
//                    "()Ljava/lang/String;",
//                    "获取蓝牙-getAddress"
//                )
//            )
//
//            //系统设置Settings$Secure
//            resultList.add(
//                HookMethodItem(
//                    "android/provider/Settings\$Secure",
//                    "getString",
//                    "(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;",
//                    "系统设置-Settings\$Secure"
//                )
//            )
//            return resultList
//        }
    }
}

class HookMethodItem {
    // 原始类名
    var originClassName: String? = ""

    // 原始方法名
    var originMethodName: String? = ""

    // 原始方法签名
    var originMethodDesc: String? = ""

    var originMethodAccess: Int? = 0

    // 代理的类名
    var proxyClassName: String

    // 代理的方法名
    var proxyMethodName: String

    // 代理的方法签名
    var proxyMethodDesc: String

    // 被代理方法的描述信息
    var documentMethodDesc: String? = ""

    constructor(
        proxyClassName: String,
        proxyMethodName: String,
        proxyMethodReturnDesc: String
    ) {
        this.proxyClassName = proxyClassName
        this.proxyMethodName = proxyMethodName
        this.proxyMethodDesc = proxyMethodReturnDesc
    }

    constructor(
        originClassName: String,
        originMethodName: String,
        originMethodDesc: String,
        originMethodAccess: Int,
        proxyClassName: String,
        proxyMethodName: String,
        proxyMethodDesc: String,
        documentMethodDesc: String
    ) {
        this.originClassName = originClassName
        this.originMethodName = originMethodName
        this.originMethodDesc = originMethodDesc
        this.originMethodAccess = originMethodAccess
        this.proxyClassName = proxyClassName
        this.proxyMethodName = proxyMethodName
        this.proxyMethodDesc = proxyMethodDesc
        this.documentMethodDesc = documentMethodDesc
    }

    override fun equals(other: Any?): Boolean {
        if (other is HookMethodItem) {
            return (other.originMethodAccess == originMethodAccess &&
                    other.originClassName == originClassName &&
                    other.originMethodName == originMethodName &&
                    other.originMethodDesc == originMethodDesc)
        }

        return super.equals(other)
    }
}