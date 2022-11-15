package com.yl.lib.plugin.sentry.transform

import com.android.build.api.transform.*
import com.android.build.api.variant.VariantInfo
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.yl.lib.plugin.sentry.Utils
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.logging.Logger
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @sinice 2021-12-13 17:10
 */
class PrivacySentryTransform : Transform {

    private var logger: Logger
    private var extension: PrivacyExtension
    private var resultFilePath: String

    constructor(logger: Logger, extension: PrivacyExtension, path: String) {
        this.logger = logger
        this.extension = extension
        this.resultFilePath = path
    }

    override fun applyToVariant(variant: VariantInfo?): Boolean {
        return Utils.isApply(variant,extension)
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
            handleJar(
                it,
                transformInvocation.outputProvider,
                transformInvocation.isIncremental,
                extension
            )
            handleDirectory(
                it,
                transformInvocation.outputProvider,
                transformInvocation.isIncremental, extension
            )
        }
        // 写入被替换所有的类和文件
        extension.replaceFileName?.let {
            ReplaceMethodManger.MANAGER.flushToFile(resultFilePath,extension.replaceFileName!!,logger)
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
                        logger.info("directory status is ${it.status}  file is:" + it.file.absolutePath)
                        PrivacyClassProcessor.processJar(
                            logger,
                            it.file,
                            extension,
                            runAsm = { input, project ->
                                PrivacyClassProcessor.runHook(
                                    input,
                                    extension,
                                    logger
                                )
                            })
                        GFileUtils.deleteQuietly(output)
                        GFileUtils.copyFile(it.file, output)
                    }
                    Status.REMOVED -> {
                        logger.info("jar REMOVED file is:" + it.file.absolutePath)
                        GFileUtils.deleteQuietly(output)
                    }
                }
            } else {
                logger.info("jar incremental false file is:" + it.file.absolutePath)
                PrivacyClassProcessor.processJar(
                    logger,
                    it.file,
                    extension,
                    runAsm = { input, project ->
                        PrivacyClassProcessor.runHook(
                            input,
                            extension,
                            logger
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
                    var outputFile  = File(outputDir, inputFile.toRelativeString(inputDir))
                    when (status) {
                        Status.REMOVED -> {
                            logger.info("directory REMOVED file is:" + inputFile.absolutePath)
                            GFileUtils.deleteQuietly(inputFile)
                        }
                        Status.ADDED, Status.CHANGED -> {
                            logger.info("directory status is $status $ file is:" + inputFile.absolutePath)
                            PrivacyClassProcessor.processDirectory(
                                logger,
                                inputDir,
                                inputFile,
                                extension,
                                runAsm = { input, project ->
                                    PrivacyClassProcessor.runHook(
                                        input,
                                        extension,
                                        logger
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
                logger.info("directory incremental false  file is:" + inputDir.absolutePath)
                inputDir.walk().forEach { file ->
                    if (!file.isDirectory) {
                        PrivacyClassProcessor.processDirectory(
                            logger,
                            inputDir,
                            file,
                            extension,
                            runAsm = { input, project ->
                                PrivacyClassProcessor.runHook(
                                    input,
                                    extension,
                                    logger
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