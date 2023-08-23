package com.yl.lib.plugin.sentry.transform

import com.android.build.api.transform.*
import com.didiglobal.booster.gradle.*
import com.didiglobal.booster.kotlinx.NCPU
import com.didiglobal.booster.transform.*
import com.didiglobal.booster.transform.util.transform
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.transform.booster.asmtransform.PrivacyBaseTransform
import com.yl.lib.plugin.sentry.util.PrivacyExecutorsUtil
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import com.yl.lib.plugin.sentry.util.privacyTransform
import org.gradle.api.Project
import java.io.File
import java.net.URI

/**
 * @author yulun
 * @since 2023-08-09 14:44
 */
internal class PrivacyTransformInvocation(
    private val delegateInvocation: TransformInvocation,
    val transform: PrivacyBaseTransform
) : TransformInvocation by delegateInvocation, PrivacyTransformContext, TransformContext {

    private val project = transform.project

    override fun project(): Project {
        return project
    }

    override fun privacyExtension(): PrivacyExtension {
        return project.extensions.getByType(PrivacyExtension::class.java)
    }

    override val applicationId: String
        get() = delegateInvocation.applicationId
    override val artifacts: ArtifactManager
        get() = TODO("Not yet implemented")
    override val bootClasspath: Collection<File>
        get() = delegateInvocation.bootClasspath
    override val buildDir: File
        get() = project.buildDir
    override val compileClasspath: Collection<File>
        get() = delegateInvocation.compileClasspath
    override val dependencies: Collection<String> by lazy {
        compileClasspath.map { it.canonicalPath }
    }
    override val isDataBindingEnabled: Boolean
        get() = delegateInvocation.isDataBindingEnabled
    override val isDebuggable: Boolean
        get() = variant.buildType.isDebuggable
    override val klassPool: KlassPool =
        object : AbstractKlassPool(compileClasspath, transform.bootKlassPool) {}
    override val name: String
        get() = delegateInvocation.context.variantName
    override val originalApplicationId: String
        get() = delegateInvocation.originalApplicationId
    override val projectDir: File
        get() = project.projectDir
    override val reportsDir: File
        get() = File(buildDir, "reports").also { it.mkdirs() }
    override val runtimeClasspath: Collection<File>
        get() = delegateInvocation.runtimeClasspath
    override val temporaryDir: File
        get() = delegateInvocation.context.temporaryDir

    override fun hasProperty(name: String): Boolean {
        return project.hasProperty(name)
    }

    override fun <R> registerCollector(collector: Collector<R>) {
    }

    override fun <R> unregisterCollector(collector: Collector<R>) {
    }


    internal fun doFullTransform() = doTransform(this::fullTransform)

    internal fun doIncrementalTransform() = doTransform(this::incrementalTransform)

    private fun fullTransform(): Iterable<Runnable> {
        return this.inputs.map { it.jarInputs + it.directoryInputs }.flatten().map { input ->
            Runnable {
                var format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
                outputProvider?.let { provider ->
                    project.logger.info("Privacy Transforming ${input.file}")
                    input.transform(
                        provider.getContentLocation(
                            input.name,
                            input.contentTypes,
                            input.scopes,
                            format
                        )
                    )
                }
            }
        }
    }

    private fun incrementalTransform(): Iterable<Runnable> {
        return this.inputs.map { input ->
            input.jarInputs.filter { it.status != Status.NOTCHANGED }.map { jarInput ->
                Runnable {
                    doIncrementalTransform(jarInput)
                }
            } + input.directoryInputs.filter { it.changedFiles.isNotEmpty() }.map { dirInput ->
                val base = dirInput.file.toURI()
                Runnable {
                    doIncrementalTransform(dirInput, base)
                }
            }
        }.flatten()
    }

    private fun doTransform(func: () -> Iterable<Runnable>) {
        this.onPreTransform()
        PrivacyPluginUtil.privacyPluginUtil.i("doTransform NCPU is $NCPU")
        try {
            func().forEach {
                PrivacyExecutorsUtil.submit(it).get()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this.onPostTransform()
    }


    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(jarInput: JarInput) {
        when (jarInput.status) {
            Status.REMOVED -> jarInput.file.delete()
            Status.CHANGED, Status.ADDED -> {
                project.logger.info("Transforming ${jarInput.file}")
                outputProvider?.let { provider ->
                    jarInput.transform(
                        provider.getContentLocation(
                            jarInput.name,
                            jarInput.contentTypes,
                            jarInput.scopes,
                            Format.JAR
                        )
                    )
                }
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(dirInput: DirectoryInput, base: URI) {
        dirInput.changedFiles.forEach { (file, status) ->
            when (status) {
                Status.REMOVED -> {
                    project.logger.info("Deleting $file")
                    outputProvider?.let { provider ->
                        provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        ).parentFile.listFiles()?.asSequence()
                            ?.filter { it.isDirectory }
                            ?.map { File(it, dirInput.file.toURI().relativize(file.toURI()).path) }
                            ?.filter { it.exists() }
                            ?.forEach { it.delete() }
                    }
                    file.delete()
                }
                Status.ADDED, Status.CHANGED -> {
                    project.logger.info("Transforming $file")
                    outputProvider?.let { provider ->
                        val root = provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        )
                        val output = File(root, base.relativize(file.toURI()).path)
                        file.transform(output) { bytecode ->
                            bytecode.transform()
                        }
                    }
                }
            }
        }
    }


    private fun onPreTransform() {
        transform.transformers.forEach {
            it.onPreTransform(this)
        }
    }

    private fun onPostTransform() {
        transform.transformers.forEach {
            it.onPostTransform(this)
        }
    }

    private fun QualifiedContent.transform(output: File) {
        try {
            this.file.privacyTransform(output) { bytecode ->
                bytecode.transform()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun ByteArray.transform(): ByteArray {
        return transform.transformers.fold(this) { bytes, transformer ->
            transformer.transform(this@PrivacyTransformInvocation, bytes)
        }
    }

}