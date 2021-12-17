package com.yl.lib.plugin.sentry.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @sinice 2021-12-13 17:10
 */
class PrivacySentryTransform : Transform {

    private val project: Project

    constructor(project: Project){
        this.project = project
    }


    override fun getName(): String {
        return "PrivacySentryPlugin"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)

        // 非增量，删掉所有
        if (transformInvocation?.isIncremental == false) {
            transformInvocation.outputProvider.deleteAll()
        }

        transformInvocation?.inputs?.forEach {
            handleJar(it, transformInvocation.outputProvider, transformInvocation.isIncremental)
            handleDirectory(
                it,
                transformInvocation.outputProvider,
                transformInvocation.isIncremental
            )
        }
    }

    // 处理jar
    fun handleJar(
        transformInput: TransformInput, outputProvider: TransformOutputProvider,
        incremental: Boolean
    ) {
        transformInput.jarInputs.forEach {
            var output =
                outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
            if (incremental) {
                when (it.status) {
                    Status.ADDED, Status.CHANGED -> {
                        project.logger.info("directory status is ${it.status}  file is:" + it.file.absolutePath)
                        // TODO process
                        GFileUtils.deleteFileQuietly(output)
                        GFileUtils.copyFile(it.file, output)
                    }
                    Status.REMOVED -> {
                        project.logger.info("jar REMOVED file is:" + it.file.absolutePath)
                        GFileUtils.deleteFileQuietly(output)
                    }
                }
            } else {
                project.logger.info("jar incremental false file is:" + it.file.absolutePath)
                // TODO process
                GFileUtils.deleteFileQuietly(output)
                GFileUtils.copyFile(it.file, output)
            }
        }
    }

    // 处理directory
    fun handleDirectory(
        transformInput: TransformInput,
        outputProvider: TransformOutputProvider,
        incremental: Boolean
    ) {
        transformInput.directoryInputs.forEach {
            var inputDir = it.file
            var outputDir = outputProvider.getContentLocation(
                it.name,
                it.contentTypes,
                it.scopes,
                Format.DIRECTORY
            )
            if (incremental) {
                for ((inputFile, status) in it.changedFiles) {
                    var outputFile = File(
                        outputDir,
                        FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir)
                    )

                    when (status) {
                        Status.REMOVED -> {
                            project.logger.info("directory REMOVED file is:" + inputFile.absolutePath)
                            GFileUtils.deleteFileQuietly(inputFile)
                        }
                        Status.ADDED, Status.CHANGED -> {
                            project.logger.info("directory status is $status $ file is:" + inputFile.absolutePath)
                            // TODO process
                            if (inputFile.exists()) {
                                GFileUtils.deleteFileQuietly(outputFile)
                                FileUtils.copyFile(inputFile, outputFile)
                            }
                        }
                    }
                }
            } else {
                project.logger.info("directory incremental false  file is:" + inputDir.absolutePath)
                // TODO process
                // 保险起见，删一次
                GFileUtils.deleteFileQuietly(outputDir)
                FileUtils.copyDirectory(inputDir, outputDir)
            }
        }
    }
}