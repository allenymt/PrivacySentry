package com.yl.lib.plugin.sentry.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.Project
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @sinice 2021-12-31 11:36
 * 收集带有 com.yl.lib.privacy_annotation.PrivacyMethodProxy 的方法
 */
class PrivacyCollectTransform : Transform {
    private var project: Project

    constructor(project: Project) {
        this.project = project
    }

    override fun getName(): String {
        return "PrivacyCollectTransform"
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

        var privacyExtension = project.extensions.findByType(
            PrivacyExtension::class.java
        ) as PrivacyExtension

        transformInvocation?.inputs?.forEach {
            handleJar(
                it,
                transformInvocation.outputProvider,
                transformInvocation.isIncremental,
                privacyExtension
            )
            handleDirectory(
                it,
                transformInvocation.outputProvider,
                transformInvocation.isIncremental, privacyExtension
            )
        }
    }


    // 处理jar
    private fun handleJar(
        transformInput: TransformInput, outputProvider: TransformOutputProvider,
        incremental: Boolean,
        extension: PrivacyExtension
    ) {
        transformInput.jarInputs.forEach {
            var output =
                outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
            if (incremental) {
                when (it.status) {
                    Status.ADDED, Status.CHANGED -> {
                        project.logger.info("directory status is ${it.status}  file is:" + it.file.absolutePath)
                        PrivacyClassProcessor.processJar(
                            project,
                            it.file,
                            extension,
                            runAsm = { input, project ->
                                PrivacyClassProcessor.runCollect(
                                    input,
                                    project
                                )
                            })
                        GFileUtils.deleteQuietly(output)
                        GFileUtils.copyFile(it.file, output)
                    }
                    Status.REMOVED -> {
                        project.logger.info("jar REMOVED file is:" + it.file.absolutePath)
                        GFileUtils.deleteQuietly(output)
                    }
                }
            } else {
                project.logger.info("jar incremental false file is:" + it.file.absolutePath)
                PrivacyClassProcessor.processJar(
                    project,
                    it.file,
                    extension,
                    runAsm = { input, project ->
                        PrivacyClassProcessor.runCollect(
                            input,
                            project
                        )
                    })
                GFileUtils.deleteQuietly(output)
                GFileUtils.copyFile(it.file, output)
            }
        }
    }

    // 处理directory
    private fun handleDirectory(
        transformInput: TransformInput,
        outputProvider: TransformOutputProvider,
        incremental: Boolean,
        extension: PrivacyExtension
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
                            GFileUtils.deleteQuietly(inputFile)
                        }
                        Status.ADDED, Status.CHANGED -> {
                            project.logger.info("directory status is $status $ file is:" + inputFile.absolutePath)
                            PrivacyClassProcessor.processDirectory(
                                project,
                                inputDir,
                                inputFile,
                                extension,
                                runAsm = { input, project ->
                                    PrivacyClassProcessor.runCollect(
                                        input,
                                        project
                                    )
                                }
                            )
                            if (inputFile.exists()) {
                                GFileUtils.deleteQuietly(outputFile)
                                FileUtils.copyFile(inputFile, outputFile)
                            }
                        }
                    }
                }
            } else {
                project.logger.info("directory incremental false  file is:" + inputDir.absolutePath)
                inputDir.walk().forEach { file ->
                    if (!file.isDirectory) {
                        PrivacyClassProcessor.processDirectory(
                            project,
                            inputDir,
                            file,
                            extension,
                            runAsm = { input, project ->
                                PrivacyClassProcessor.runCollect(
                                    input,
                                    project
                                )
                            })
                    }
                }

                // 保险起见，删一次
                GFileUtils.deleteQuietly(outputDir)
                FileUtils.copyDirectory(inputDir, outputDir)
            }
        }
    }
}