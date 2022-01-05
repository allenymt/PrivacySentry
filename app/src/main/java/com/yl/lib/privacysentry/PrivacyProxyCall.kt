//package com.yl.lib.privacysentry
//
//import android.app.ActivityManager
//import androidx.annotation.Keep
//import com.yl.lib.privacy_annotation.MethodInvokeOpcode
//import com.yl.lib.privacy_annotation.PrivacyClassProxy
//import com.yl.lib.privacy_annotation.PrivacyMethodProxy
//
///**
// * @author yulun
// * @sinice 2021-12-22 14:23
// */
//@Keep
//open class PrivacyProxyCall {
//
//    // kotlin里实际解析的是这个PrivacyProxyCall$Proxy 内部类
//    @PrivacyClassProxy
//    @Keep
//    object Proxy {
//
//        @PrivacyMethodProxy(
//            originalClass = ActivityManager::class,
//            originalMethod = "getRunningTasks",
//            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
//            documentDesc = "获取当前运行中的任务"
//        )
//        @JvmStatic
//        fun getRunningTasks(
//            manager: ActivityManager,
//            maxNum: Int
//        ): List<ActivityManager.RunningTaskInfo?>? {
////            doFilePrinter("getRunningTasks")
//            return manager.getRunningTasks(maxNum)
//        }
//
////        @PrivacyMethodProxy(
////            originalClass = Settings.Secure::class,
////            originalMethod = "getString",
////            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
////
////        )
////        fun getString(contentResolver: ContentResolver?, type: String?): String? {
////            var result = ""
////            try {
////                doFilePrinter("getString", args = type)
////                result = Settings.Secure.getString(
////                    contentResolver,
////                    type
////                )
////            } catch (e: Exception) {
////                e.printStackTrace()
////            }
////            return result
////        }
//
////        @PrivacyMethodProxy(
////            originalClass = ActivityManager::class,
////            originalMethod = "getRunningAppProcesses",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取当前运行中的进程"
////        )
////        fun getRunningAppProcesses(manager: ActivityManager): List<ActivityManager.RunningAppProcessInfo> {
////            doFilePrinter("getRunningAppProcesses")
////            return manager.getRunningAppProcesses()
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = PackageManager::class,
////            originalMethod = "getInstalledPackages",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取安装包-getInstalledPackages"
////        )
////        fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo> {
////            doFilePrinter("getInstalledPackages")
////            return manager.getInstalledPackages(flags)
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = PackageManager::class,
////            originalMethod = "getInstalledPackages",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "读安装列表-queryIntentActivities"
////        )
////        fun queryIntentActivities(
////            manager: PackageManager,
////            intent: Intent,
////            flags: Int
////        ): List<ResolveInfo> {
////            doFilePrinter("queryIntentActivities")
////            return manager.queryIntentActivities(intent, flags)
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = PackageManager::class,
////            originalMethod = "getInstalledPackages",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "读安装列表-queryIntentActivityOptions"
////        )
////        fun queryIntentActivityOptions(
////            manager: PackageManager,
////            caller: ComponentName?,
////            specifics: Array<Intent?>?,
////            intent: Intent,
////            flags: Int
////        ): List<ResolveInfo> {
////            doFilePrinter("queryIntentActivityOptions")
////            return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getMeid",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "移动设备标识符-getMeid()"
////        )
////        fun getMeid(manager: TelephonyManager): String? {
////            doFilePrinter("getMeid", "()Ljava/lang/String;")
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                manager.getMeid()
////            } else {
////                ""
////            }
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getMeid",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "移动设备标识符-getMeid(I)"
////        )
////        fun getMeid(manager: TelephonyManager, index: Int): String? {
////            doFilePrinter("getMeid", "(I)Ljava/lang/String;")
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                manager.getMeid(index)
////            } else {
////                ""
////            }
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getDeviceId",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getDeviceId()"
////        )
////        fun getDeviceId(manager: TelephonyManager): String? {
////            doFilePrinter("getDeviceId", "()Ljava/lang/String;")
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                manager.getDeviceId()
////            } else {
////                ""
////            }
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getDeviceId",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getDeviceId(I)"
////        )
////        fun getDeviceId(manager: TelephonyManager, index: Int): String? {
////            doFilePrinter("getDeviceId", "(I)Ljava/lang/String;")
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                manager.getDeviceId(index)
////            } else {
////                ""
////            }
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getDeviceId",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getSubscriberId(I)"
////        )
////        fun getSubscriberId(manager: TelephonyManager): String? {
////            doFilePrinter("getSubscriberId")
////            return manager.subscriberId
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "subscriberId",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getSubscriberId()"
////        )
////        fun getSubscriberId(manager: TelephonyManager, index: Int): String? {
////            doFilePrinter("getSubscriberId")
////            return manager.subscriberId
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getImei",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getImei()"
////        )
////        fun getImei(manager: TelephonyManager): String? {
////            doFilePrinter("getImei", "()Ljava/lang/String;")
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                manager.getImei()
////            } else {
////                ""
////            }
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getImei",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getImei()"
////        )
////        fun getImei(manager: TelephonyManager, index: Int): String? {
////            doFilePrinter("getImei", "(I)Ljava/lang/String;")
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                manager.getImei(index)
////            } else {
////                ""
////            }
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getSimSerialNumber",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getSimSerialNumber()"
////        )
////        fun getSimSerialNumber(manager: TelephonyManager): String? {
////            doFilePrinter("getSimSerialNumber")
////            return manager.getSimSerialNumber()
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = TelephonyManager::class,
////            originalMethod = "getSimSerialNumber",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取设备id-getSimSerialNumber()"
////        )
////        fun getSimSerialNumber(manager: TelephonyManager, index: Int): String? {
////            doFilePrinter("getSimSerialNumber")
////            return manager.getSimSerialNumber()
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = ClipboardManager::class,
////            originalMethod = "getPrimaryClip",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取剪贴板内容-getPrimaryClip"
////        )
////        fun getPrimaryClip(manager: ClipboardManager): ClipData? {
////            doFilePrinter("getPrimaryClip")
////            return manager.primaryClip
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = ClipboardManager::class,
////            originalMethod = "getPrimaryClipDescription",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取剪贴板内容-getPrimaryClipDescription"
////        )
////        fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription? {
////            doFilePrinter("getPrimaryClipDescription")
////            return manager.primaryClipDescription
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = ClipboardManager::class,
////            originalMethod = "getText",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取剪贴板内容-getText"
////        )
////        fun getText(manager: ClipboardManager): CharSequence? {
////            doFilePrinter("getText")
////            return manager.text
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = ClipboardManager::class,
////            originalMethod = "setPrimaryClip",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "设置剪贴板内容-setPrimaryClip"
////        )
////        fun setPrimaryClip(manager: ClipboardManager, clip: ClipData) {
////            doFilePrinter("setPrimaryClip")
////            manager.setPrimaryClip(clip)
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = ClipboardManager::class,
////            originalMethod = "setText",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "设置剪贴板内容-setText"
////        )
////        fun setText(manager: ClipboardManager, clip: CharSequence) {
////            doFilePrinter("setText")
////            manager.text = clip
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = WifiInfo::class,
////            originalMethod = "getMacAddress",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取mac地址-getMacAddress"
////        )
////        fun getMacAddress(manager: WifiInfo): String? {
////            doFilePrinter("getMacAddress")
////            return manager.getMacAddress();
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = NetworkInterface::class,
////            originalMethod = "getHardwareAddress",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取mac地址-getHardwareAddress"
////        )
////        fun getHardwareAddress(manager: NetworkInterface): ByteArray? {
////            doFilePrinter("getHardwareAddress")
////            return manager.hardwareAddress
////        }
////
////        @PrivacyMethodProxy(
////            originalClass = BluetoothAdapter::class,
////            originalMethod = "getAddress",
////            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
////            documentDesc = "获取蓝牙地址-getAddress"
////        )
////        fun getAddress(manager: BluetoothAdapter): String? {
////            doFilePrinter("getAddress")
////            return manager.address ?: ""
////        }
//
////        private fun doFilePrinter(
////            funName: String,
////            methodReturnDesc: String = "",
////            args: String? = ""
////        ) {
////            var hookMethodItem: HookMethodItem? = HookMethodManager.MANAGER.findHookItemByName(
////                funName,
////                methodReturnDesc = methodReturnDesc
////            )
////            hookMethodItem?.let {
////                PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
////                    it.filePrint(
////                        hookMethodItem?.originMethodName!!,
////                        hookMethodItem?.documentMethodDesc + if (args?.isNotEmpty() == true) "--参数: $args" else "",
////                        PrivacyUtil.Util.getStackTrace()
////                    )
////                }
////            }
////        }
//    }
//}