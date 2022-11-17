package com.yl.lib.privacy_ui.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author yulun
 * @since 2022-07-04 15:55
 */
@Keep
class PermissionViewModel : ViewModel() {
    var data: MutableLiveData<ArrayList<PermissionItem>>? = null
    val originData: ArrayList<PermissionItem> by lazy {
        ArrayList<PermissionItem>()
    }

    fun observer(): LiveData<ArrayList<PermissionItem>> {
        if (data == null) {
            data = MutableLiveData()
        }
        return data!!
    }

    fun buildData(context: Context) {
        try {
            getManifestPermissions(context)?.let {
                originData?.addAll(transformData(it))
                data?.postValue(originData)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun transformData(permission: Array<String>): Array<PermissionItem> {
        val list = ArrayList<PermissionItem>()
        permission.forEach {
            list.add(PermissionItem(it, getPermissionDesc(it)))
        }
        return list.toTypedArray()
    }

    private fun getPermissionDesc(permissionName: String): String {
        when (permissionName) {
            Manifest.permission.READ_PHONE_STATE -> return "读取手机状态"
            Manifest.permission.READ_EXTERNAL_STORAGE -> return "读取外部存储"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> return "写入外部存储"

            Manifest.permission.ACCESS_FINE_LOCATION -> return "获取精确位置"
            Manifest.permission.ACCESS_COARSE_LOCATION -> return "获取粗略位置"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> return "后台定位"
            Manifest.permission.ACCESS_MEDIA_LOCATION -> return "媒体定位"

            Manifest.permission.ACCESS_WIFI_STATE -> return "获取WIFI状态"
            Manifest.permission.CHANGE_WIFI_STATE -> return "修改WIFI状态"
            Manifest.permission.ACCESS_NETWORK_STATE -> return "获取网络状态"
            Manifest.permission.CHANGE_NETWORK_STATE -> return "获取网络状态"

            Manifest.permission.CAMERA -> return "相机"
            Manifest.permission.RECORD_AUDIO -> return "录音"
            Manifest.permission.READ_CONTACTS -> return "读取联系人"
            Manifest.permission.WRITE_CONTACTS -> return "写入联系人"
            Manifest.permission.READ_CALENDAR -> return "读取日历"
            Manifest.permission.WRITE_CALENDAR -> return "写入日历"
            Manifest.permission.READ_SMS -> return "读取短信"
            Manifest.permission.SEND_SMS -> return "发送短信"
            Manifest.permission.RECEIVE_SMS -> return "接收短信"
            Manifest.permission.READ_CALL_LOG -> return "读取通话记录"
            Manifest.permission.WRITE_CALL_LOG -> return "写入通话记录"
            Manifest.permission.BLUETOOTH -> return "蓝牙"
            Manifest.permission.BLUETOOTH_ADMIN -> return "蓝牙管理"
            Manifest.permission.BODY_SENSORS -> return "传感器"
            Manifest.permission.READ_PHONE_NUMBERS -> return "读取电话号码"

            Manifest.permission.ACTIVITY_RECOGNITION -> return "活动识别"
            Manifest.permission.ADD_VOICEMAIL -> return "添加语音邮件"
            Manifest.permission.ANSWER_PHONE_CALLS -> return "接听电话"
            Manifest.permission.BROADCAST_STICKY -> return "粘性广播"
            Manifest.permission.CALL_PHONE -> return "拨打电话"
            Manifest.permission.CALL_PRIVILEGED -> return "拨打特殊电话"
            Manifest.permission.CAPTURE_AUDIO_OUTPUT -> return "录制音频输出"
            Manifest.permission.CONTROL_LOCATION_UPDATES -> return "控制位置更新"
            Manifest.permission.DELETE_CACHE_FILES -> return "删除缓存文件"
            Manifest.permission.DELETE_PACKAGES -> return "删除应用"
            Manifest.permission.DISABLE_KEYGUARD -> return "禁用键盘锁"
            Manifest.permission.DUMP -> return "转储"
            Manifest.permission.EXPAND_STATUS_BAR -> return "扩展状态栏"
            Manifest.permission.FOREGROUND_SERVICE -> return "前台服务"
            Manifest.permission.GET_ACCOUNTS -> return "获取账户"
            Manifest.permission.GET_ACCOUNTS_PRIVILEGED -> return "获取特殊账户"
            Manifest.permission.GET_PACKAGE_SIZE -> return "获取应用大小"
            Manifest.permission.INSTALL_SHORTCUT -> return "安装快捷方式"
            Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE -> return "即时应用前台服务"
            Manifest.permission.INTERNET -> return "网络"
            Manifest.permission.KILL_BACKGROUND_PROCESSES -> return "杀死后台进程"
            Manifest.permission.LOCATION_HARDWARE -> return "位置硬件"
            Manifest.permission.WAKE_LOCK -> return "唤醒锁"
            Manifest.permission.RECEIVE_BOOT_COMPLETED -> return "接收开机完成"
        }
        return ""
    }

    private fun getManifestPermissions(context: Context): Array<String>? {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo != null) {
            return packageInfo.requestedPermissions
        }
        return null
    }

    fun search(searchText: String?) {
        if (searchText == null || searchText.isEmpty()) {
            data?.postValue(originData)
        } else {
            originData?.filter { it.desc?.toLowerCase()?.contains(searchText) ?: false }
                ?.let {
                    data?.postValue(it as ArrayList<PermissionItem>)
                }
        }
    }
}