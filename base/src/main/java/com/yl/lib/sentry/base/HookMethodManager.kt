package com.yl.lib.sentry.base

/**
 * @author yulun
 * @sinice 2021-12-14 11:39
 * 汇总所有的hook方法配置
 */
class HookMethodManager {
    object MANAGER {
        private var hookMethodList: ArrayList<HookMethodItem> = ArrayList()
        fun contains(
            methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): Boolean {
            if (hookMethodList.isEmpty()) {
                init()
            }
            if (methodName == null || methodName == "") {
                return false
            }
            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc)
            } != null
        }

        fun findHookItemByName(
            methodName: String
        ): HookMethodItem? {
            return findHookItemByName(methodName,"","")
        }

        fun findHookItemByName(
            methodName: String, classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): HookMethodItem? {
            if (hookMethodList.isEmpty()) {
                init()
            }
            if (methodName == null || methodName == "") {
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
            if (hookItem == null || methodName.isEmpty()) {
                return false
            }

            return if (classOwnerName.isEmpty() && methodReturnDesc.isEmpty()) {
                methodName == hookItem.methodName
            } else {
                methodName == hookItem.methodName && classOwnerName == hookItem.className && methodReturnDesc == hookItem.methodReturnDesc
            }
        }

        private fun init() {
            hookMethodList.addAll(buildAmsMethod())
            hookMethodList.addAll(buildPmsMethod())
            hookMethodList.addAll(buildTmsMethod())
            hookMethodList.addAll(buildCmsMethod())
            hookMethodList.addAll(buildOtherMethod())
        }

        private const val asmClsName = "android/app/ActivityManager"
        private fun buildAmsMethod(): ArrayList<HookMethodItem> {
            return arrayListOf(
                HookMethodItem(
                    asmClsName,
                    "getRunningTasks",
                    "(I)Ljava/util/List;",
                    "获取当前运行任务-getRunningTasks"
                ),
                HookMethodItem(
                    asmClsName,
                    "getRunningAppProcesses",
                    "(I)Ljava/util/List;",
                    "获取当前运行进程-getRunningAppProcesses"
                )
            )
        }

        private val pmsClsName = "android.content.pm.PackageManager".replace(".", "/")
        private fun buildPmsMethod(): ArrayList<HookMethodItem> {
            return arrayListOf(
                HookMethodItem(
                    pmsClsName,
                    "getInstalledPackages",
                    "(I)Ljava/util/List;",
                    "获取安装包-getInstalledPackages"
                ),
                HookMethodItem(
                    asmClsName,
                    "queryIntentActivities",
                    "(I)Ljava/util/List;",
                    "读安装列表-queryIntentActivities"
                ),
                HookMethodItem(
                    asmClsName,
                    "getLeanbackLaunchIntentForPackage",
                    "",
                    "读安装列表-getLeanbackLaunchIntentForPackage"
                ),
                HookMethodItem(
                    asmClsName,
                    "getInstalledPackagesAsUser",
                    "(I)Ljava/util/List;",
                    "读安装列表-getInstalledPackagesAsUser"
                ),
                HookMethodItem(
                    asmClsName,
                    "queryIntentActivitiesAsUser",
                    "",
                    "读安装列表-queryIntentActivitiesAsUser"
                ),
                HookMethodItem(
                    asmClsName,
                    "queryIntentActivityOptions",
                    "(I)Ljava/util/List;",
                    "读安装列表-queryIntentActivityOptions"
                )
            )
        }

        private val tmsClsName = "android.telephony.TelephonyManager".replace(".", "/")
        private fun buildTmsMethod(): ArrayList<HookMethodItem> {
            return arrayListOf(
                HookMethodItem(
                    tmsClsName,
                    "getMeid",
                    "(I)Ljava/lang/String;",
                    "移动设备标识符-getMeid(I)"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getMeid",
                    "()Ljava/lang/String;",
                    "移动设备标识符-getMeid"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getDeviceId",
                    "(I)Ljava/lang/String;",
                    "获取设备id-getDeviceId(I)"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getDeviceId",
                    "()Ljava/lang/String;",
                    "获取设备id-getDeviceId"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getSubscriberId",
                    "(I)Ljava/lang/String;",
                    "获取设备id-getSubscriberId(I)"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getSubscriberId",
                    "()Ljava/lang/String;",
                    "获取设备id-getSubscriberId"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getImei",
                    "(I)Ljava/lang/String;",
                    "获取设备id-getImei(I)"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getImei",
                    "()Ljava/lang/String;",
                    "获取设备id-getImei"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getSimSerialNumber",
                    "(I)Ljava/lang/String;",
                    "获取设备id-getSimSerialNumber(I)"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getSimSerialNumber",
                    "()Ljava/lang/String;",
                    "获取设备id-getSimSerialNumber"
                )

                // for runtime hook
                ,
                HookMethodItem(
                    tmsClsName,
                    "getDeviceIdWithFeature",
                    "()Ljava/lang/String;",
                    "获取设备id-getDeviceIdWithFeature"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getImeiForSlot",
                    "()Ljava/lang/String;",
                    "获取设备id-getImeiForSlot"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getSubscriberIdForSubscriber",
                    "()Ljava/lang/String;",
                    "获取设备id-getSubscriberIdForSubscriber"
                ),
                HookMethodItem(
                    tmsClsName,
                    "getIccSerialNumberForSubscriber",
                    "()Ljava/lang/String;",
                    "获取设备id-getIccSerialNumberForSubscriber"
                )
            )
        }

        private val cmsClsName = "android.content.ClipboardManager".replace(".", "/")
        private fun buildCmsMethod(): ArrayList<HookMethodItem> {
            return arrayListOf(
                HookMethodItem(
                    cmsClsName,
                    "getPrimaryClip",
                    "()Landroid/content/ClipData;",
                    "获取剪贴板内容-getPrimaryClip"
                ),
                HookMethodItem(
                    cmsClsName,
                    "getPrimaryClipDescription",
                    "()Landroid/content/ClipDescription;",
                    "获取剪贴板内容-getPrimaryClipDescription"
                ),
                HookMethodItem(
                    cmsClsName,
                    "getText",
                    "()Ljava/lang/CharSequence;",
                    "获取剪贴板内容-getText"
                ),
                HookMethodItem(
                    cmsClsName,
                    "setPrimaryClip",
                    "(Landroid/content/ClipData;)V",
                    "设置剪贴板内容-setPrimaryClip"
                ),
                HookMethodItem(
                    cmsClsName,
                    "setText",
                    "(Ljava/lang/CharSequence;)V",
                    "设置剪贴板内容-setText"
                )
            )

        }

        private fun buildOtherMethod(): ArrayList<HookMethodItem> {
            val resultList = ArrayList<HookMethodItem>()

            //mac
            resultList.add(
                HookMethodItem(
                    "android/net/wifi/WifiInfo",
                    "getMacAddress",
                    "()Ljava/lang/String;",
                    "获取mac地址-getMacAddress"
                )
            )

            // 硬件地址
            resultList.add(
                HookMethodItem(
                    "java/net/NetworkInterface",
                    "getHardwareAddress",
                    "()[B",
                    "获取硬件地址-getHardwareAddress"
                )
            )

            // 蓝牙
            resultList.add(
                HookMethodItem(
                    "android/bluetooth/BluetoothAdapter",
                    "getAddress",
                    "()Ljava/lang/String;",
                    "获取蓝牙-getAddress"
                )
            )

            //系统设置Settings$Secure
            resultList.add(
                HookMethodItem(
                    "android/provider/Settings\$Secure",
                    "getString",
                    "(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;",
                    "系统设置-Settings\$Secure"
                )
            )

            return resultList
        }
    }
}