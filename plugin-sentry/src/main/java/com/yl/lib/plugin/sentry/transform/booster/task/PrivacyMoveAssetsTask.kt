package com.yl.lib.plugin.sentry.transform.booster.task

import com.yl.lib.plugin.sentry.transform.manager.HookedDataManger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @since 2022-06-29 16:06
 * 把扫描生成的静态文件 写到apk assets目录下
 */
open class PrivacyMoveAssetsTask : DefaultTask() {

    var fileName: String? = null

    var assetsPathList: ArrayList<String> = ArrayList()

    @TaskAction
    fun doFlushProxyMethod() {
        project.logger?.info("MoveAssetsTask-flushToFile")
        // 写入被代理所有的类和文件
        fileName?.let {
            HookedDataManger.MANAGER.flushToFile(it, project)
        }

        var originFile = File(project.buildDir.absolutePath + File.separator + fileName)
        if (!originFile.exists()) {
            project.logger.info("MoveAssetsTask originFile don't exist,path is ${project.buildDir.absolutePath + File.separator + fileName}")
            return
        }
        assetsPathList.forEach { assetsPath ->
            var assetsFile = File(assetsPath+ File.separator+ fileName)
            project.logger.info("MoveAssetsTask assetsPath  is ${assetsPath}")
            assetsFile.let {
                GFileUtils.deleteFileQuietly(assetsFile)
            }
            project.logger.info("MoveAssetsTask originFile is ${originFile.absolutePath} assetsPath is $assetsPath")
            GFileUtils.copyFile(originFile, assetsFile)
        }

    }

}