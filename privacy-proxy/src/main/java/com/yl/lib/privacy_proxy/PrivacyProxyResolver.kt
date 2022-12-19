package com.yl.lib.privacy_proxy

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil.Util.doFilePrinter
import com.yl.lib.sentry.hook.PrivacySentry

/**
 * @author yulun
 * @since 2022-01-13 17:57
 * 代理ContentResolver 查增删改 ,主要是针对联系人，通讯录，日历等等
 */
@Keep
open class PrivacyProxyResolver {
    // kotlin里实际解析的是这个PrivacyProxyCall$Proxy 内部类
    @PrivacyClassProxy
    @Keep
    object Proxy {

        // 查询
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "query",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun query(
            contentResolver: ContentResolver?,
            uri: Uri,
            projection: Array<String?>?, selection: String?,
            selectionArgs: Array<String?>?, sortOrder: String?
        ): Cursor? {
            doFilePrinter("query", "查询服务: ${uriToLog(uri)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return null
            }
            return contentResolver?.query(uri, projection, selection, selectionArgs, sortOrder)
        }

        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "query",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun query(
            contentResolver: ContentResolver?,
            uri: Uri,
            projection: Array<String?>?, selection: String?,
            selectionArgs: Array<String?>?, sortOrder: String?,
            cancellationSignal: CancellationSignal?
        ): Cursor? {
            doFilePrinter("query", "查询服务: ${uriToLog(uri)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return null
            }
            return contentResolver?.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
                cancellationSignal
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "query",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun query(
            contentResolver: ContentResolver?,
            uri: Uri,
            projection: Array<String?>?, queryArgs: Bundle?,
            cancellationSignal: CancellationSignal?
        ): Cursor? {
            doFilePrinter("query", "查询服务: ${uriToLog(uri)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return null
            }
            return contentResolver?.query(uri, projection, queryArgs, cancellationSignal)
        }

        //增加
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "insert",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun insert(
            contentResolver: ContentResolver?,
            url: Uri,
            values: ContentValues?
        ): Uri? {
            doFilePrinter("insert", "增加服务: ${uriToLog(url)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return null
            }
            return contentResolver?.insert(url, values)
        }

        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "insert",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun insert(
            contentResolver: ContentResolver?,
            url: Uri,
            values: ContentValues?, extras: Bundle?
        ): Uri? {
            doFilePrinter("insert", "增加服务: ${uriToLog(url)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return null
            }
            return contentResolver?.insert(url, values, extras)
        }

        // update
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "update",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun update(
            contentResolver: ContentResolver?, uri: Uri,
            values: ContentValues?, where: String?,
            selectionArgs: Array<String?>?
        ): Int? {
            doFilePrinter("update", "更新服务: ${uriToLog(uri)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return -1
            }
            return contentResolver?.update(uri, values, where, selectionArgs)
        }


        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "update",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun update(
            contentResolver: ContentResolver?, uri: Uri,
            values: ContentValues?, extras: Bundle?
        ): Int? {
            doFilePrinter("update", "更新服务: ${uriToLog(uri)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return -1
            }
            return contentResolver?.update(uri, values, extras)
        }

        //删
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "delete",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun delete(
            contentResolver: ContentResolver?, url: Uri, where: String?,
            selectionArgs: Array<String?>?
        ): Int? {
            doFilePrinter("delete", "删除服务: ${uriToLog(url)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return -1
            }
            return contentResolver?.delete(url, where, selectionArgs)
        }


        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,
            originalMethod = "delete",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun delete(
            contentResolver: ContentResolver?, url: Uri, extras: Bundle?
        ): Int? {
            doFilePrinter("delete", "删除服务: ${uriToLog(url)}")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return -1
            }
            return contentResolver?.delete(url, extras)
        }

        /**
         * 对常见的ContentResolver相关的uri做一层转换，方便日志理解
         * @param url Uri
         * @return String
         */
        private fun uriToLog(url: Uri): String {
            if (url == null) {
                return ""
            }

            if (url.toString().contains("contact")) {
                return "联系人"
            }

            if (url.toString().contains("calendar")) {
                return "日历"
            }

            if (url.toString().contains("calls")) {
                return "通话"
            }

            if (url.toString().contains("sms")) {
                return "短信"
            }

            return url.toString()
        }
    }


}