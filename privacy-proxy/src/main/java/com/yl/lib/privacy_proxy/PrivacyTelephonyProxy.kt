package com.yl.lib.privacy_proxy

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.SIM_STATE_UNKNOWN
import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyClassTestProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.cache.CachePrivacyManager
import com.yl.lib.sentry.hook.cache.CacheUtils
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil

/**
 * @author yulun
 * @since 2022-06-17 17:56
 * 代理电话权限的部分敏感API
 */
@Keep
@AutoService(PrivacyClassTestProxy::class)
open class PrivacyTelephonyProxy {

    @Keep
    @PrivacyClassProxy
    @AutoService(PrivacyClassTestProxy::class)
    object TelephonyProxy {

        private var objectImeiLock = Object()
        private var objectImsiLock = Object()
        private var objectMeidLock = Object()
        private var objectSimOperatorLock = Object()
        private var objectNetworkOperatorLock = Object()


        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getMeid",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMeid(manager: TelephonyManager): String? {
            var key = "meid"
            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "移动设备标识符-getMeid()", bVisitorModel = true)
                return ""
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    "getMeid",
                    methodDocumentDesc = "移动设备标识符-getMeid()-无权限"
                )
                return ""
            }

            synchronized(objectMeidLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "移动设备标识符-getMeid()",
                    ""
                ) { manager.meid }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getMeid",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMeid(manager: TelephonyManager, index: Int): String? {
            var key = "meid"
            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "移动设备标识符-getMeid()", bVisitorModel = true)
                return ""
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return ""
            }
            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    "getMeid",
                    methodDocumentDesc = "移动设备标识符-getMeid()-无权限"
                )
                return ""
            }
            synchronized(objectMeidLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "移动设备标识符-getMeid(I)",
                    ""
                ) { manager.meid }
            }
        }

        var objectDeviceIdLock = Object()

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getDeviceId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getDeviceId(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getDeviceId"
            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "IMEI-getDeviceId()", bVisitorModel = true)
                return ""
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "IMEI-getDeviceId()-无权限")
                return ""
            }
            synchronized(objectDeviceIdLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "IMEI-getDeviceId()",
                    ""
                ) { manager.getDeviceId() }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getDeviceId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getDeviceId(manager: TelephonyManager, index: Int): String? {
            var key = "TelephonyManager-getDeviceId-$index"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "IMEI-getDeviceId(I)",
                    bVisitorModel = true
                )
                return ""
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "IMEI-getDeviceId()-无权限")
                return ""
            }
            synchronized(objectDeviceIdLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "IMEI-getDeviceId(I)",
                    ""
                ) { manager.getDeviceId(index) }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSubscriberId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSubscriberId(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getSubscriberId"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "IMSI-getSubscriberId(I)",
                    bVisitorModel = true
                )
                return ""
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "IMSI-getSubscriberId(I)-无权限")
                return ""
            }

            synchronized(objectImsiLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "IMSI-getSubscriberId()",
                    ""
                ) { manager.subscriberId }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSubscriberId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSubscriberId(manager: TelephonyManager, index: Int): String? {
            return getSubscriberId(manager)
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getImei",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getImei(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getImei"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "IMEI-getImei()", bVisitorModel = true)
                return ""
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "IMEI-getImei()-无权限")
                return ""
            }

            synchronized(objectImeiLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "IMEI-getImei()",
                    ""
                ) { manager.imei }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getImei",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getImei(manager: TelephonyManager, index: Int): String? {
            var key = "TelephonyManager-getImei-$index"
            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "设备id-getImei(I)", bVisitorModel = true)
                return ""
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "设备id-getImei(I)-无权限")
                return ""
            }

            synchronized(objectImeiLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "IMEI-getImei(I)",
                    ""
                ) { manager.getImei(index) }
            }
        }

        var objectSimLock = Object()

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimSerialNumber",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimSerialNumber(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getSimSerialNumber"
            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "SIM卡-getSimSerialNumber()",
                    bVisitorModel = true
                )
                return ""
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return ""
            }

            if (!PrivacyProxyUtil.Util.checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "SIM卡-getSimSerialNumber()-无权限")
                return ""
            }
            synchronized(objectSimLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "SIM卡-getSimSerialNumber()",
                    ""
                ) { manager.getSimSerialNumber() }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimSerialNumber",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimSerialNumber(manager: TelephonyManager, index: Int): String? {
            return getSimSerialNumber(manager)
        }

        var objectPhoneNumberLock = Object()

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getLine1Number",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @SuppressLint("MissingPermission")
        @JvmStatic
        fun getLine1Number(manager: TelephonyManager): String? {

            var key = "TelephonyManager-getLine1Number"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(key, "手机号-getLine1Number", bVisitorModel = true)
                return ""
            }
            synchronized(objectPhoneNumberLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "手机号-getLine1Number",
                    ""
                ) { manager.line1Number }
            }
        }


        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimOperator",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimOperator(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getSimOperator"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "运营商信息-getSimOperator()",
                    bVisitorModel = true
                )
                return ""
            }

            synchronized(objectSimOperatorLock) {
                return CachePrivacyManager.Manager.loadWithMemoryCache(
                    key,
                    "运营商信息-getSimOperator()",
                    ""
                ) { manager.simOperator }
            }
        }


        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getNetworkOperator",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getNetworkOperator(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getNetworkOperator"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "运营商信息-getNetworkOperator()",
                    bVisitorModel = true
                )
                return ""
            }

            synchronized(objectNetworkOperatorLock) {
                return CachePrivacyManager.Manager.loadWithMemoryCache(
                    key,
                    "运营商信息-getNetworkOperator()",
                    ""
                ) { manager.networkOperator }
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimState",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimState(manager: TelephonyManager): Int {
            var key = "TelephonyManager-getNetworkOperator"

            if (PrivacySentry.Privacy.inDangerousState()) {
                PrivacyProxyUtil.Util.doFilePrinter(
                    key,
                    "运营商信息-getNetworkOperator()",
                    bVisitorModel = true
                )
                return SIM_STATE_UNKNOWN
            }

            synchronized(objectNetworkOperatorLock) {
                return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
                    key,
                    "运营商信息-getNetworkOperator()",
                    SIM_STATE_UNKNOWN,
                    duration = CacheUtils.Utils.MINUTE * 5
                ) { manager.simState }
            }
        }
    }
}