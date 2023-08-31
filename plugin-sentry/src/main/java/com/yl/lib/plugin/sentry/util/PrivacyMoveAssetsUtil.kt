package com.yl.lib.plugin.sentry.util

import com.yl.lib.plugin.sentry.transform.manager.HookedDataManger
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @since 2022-06-29 16:06
 * 把扫描生成的静态文件 写到apk assets目录下
 * todo  demo里task可以执行，但是在项目里，task注入不进去，待排查
 */
open class PrivacyMoveAssetsUtil{

    object Asset{

        var fileName: String? = null

        var assetsPathList: ArrayList<String> = ArrayList()

        var buildDir:File?=null

        fun doFlushProxyData() {
            PrivacyPluginUtil.privacyPluginUtil.i("MoveAssetsTask-flushToFile")
            // 写入被代理所有的类和文件
            fileName?.let {
                HookedDataManger.MANAGER.flushToFile(it, buildDir!!)
            }

            var originFile = File(buildDir?.absolutePath + File.separator + fileName)
            if (!originFile.exists()) {
                PrivacyPluginUtil.privacyPluginUtil.i("MoveAssetsTask originFile don't exist,path is ${buildDir?.absolutePath + File.separator + fileName}")
                return
            }
            assetsPathList.forEach { assetsPath ->
                var assetsFile = File(assetsPath+ File.separator+ fileName)
                PrivacyPluginUtil.privacyPluginUtil.i("MoveAssetsTask assetsPath  is ${assetsPath}")
                assetsFile.let {
                    GFileUtils.deleteQuietly(assetsFile)
                }
                PrivacyPluginUtil.privacyPluginUtil.i("MoveAssetsTask originFile is ${originFile.absolutePath} assetsPath is $assetsPath")
                GFileUtils.copyFile(originFile, assetsFile)
            }

        }
    }




}