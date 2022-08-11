package com.yl.lib.plugin.sentry.task.assets

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @since 2022-06-29 16:06
 * 把扫描生成的静态文件 写到apk assets目录下
 */
open class MoveAssetsTask : DefaultTask() {

    var fileName: String? = null

    var assetsPathList: ArrayList<String>? = ArrayList()

    @TaskAction
    fun doMoveFile() {
        var originFile = File(project.buildDir.absolutePath + File.separator + fileName)
        if (!originFile.exists()) {
            project.logger.info("MoveAssetsTask originFile don't exist,path is ${project.buildDir.absolutePath + File.separator + fileName}")
            return
        }
        assetsPathList?.forEach { assetsPath ->
            var assetsFile = File(assetsPath)
            project.logger.info("MoveAssetsTask assetsPath  is ${assetsPath}")
            assetsFile?.let {
                GFileUtils.deleteFileQuietly(assetsFile)
            }
            project.logger.info("MoveAssetsTask originFile is ${originFile.absolutePath} assetsPath is $assetsPath")
            GFileUtils.copyFile(originFile, assetsFile)
        }

    }

}