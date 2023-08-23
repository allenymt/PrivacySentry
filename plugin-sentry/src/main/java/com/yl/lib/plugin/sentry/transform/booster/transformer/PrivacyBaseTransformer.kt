package com.yl.lib.plugin.sentry.transform.booster.transformer

import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.Transformer
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import com.yl.lib.plugin.sentry.util.privacyPrintln
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean
import java.util.*

/**
 * @author yulun
 * @since 2023-08-10 11:14
 */
class PrivacyBaseTransformer : Transformer {
    private val threadMxBean = ManagementFactory.getThreadMXBean()

    private val durations = mutableMapOf<ClassTransformer, Long>()

    private val classLoader: ClassLoader

    internal val transformers: Iterable<ClassTransformer>


    constructor() : this(Thread.currentThread().contextClassLoader)

    constructor(classLoader: ClassLoader = Thread.currentThread().contextClassLoader) : this(
        ServiceLoader.load(ClassTransformer::class.java, classLoader).sortedBy {
            it.javaClass.getAnnotation(Priority::class.java)?.value ?: 0
        }, classLoader
    )

    constructor(
        transformers: Iterable<ClassTransformer>,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader
    ) {
        this.classLoader = classLoader
        this.transformers = transformers
    }

    override fun onPostTransform(context: TransformContext) {
        transformers.forEach { it ->
            threadMxBean.privacySumTime(
                it
            ) { it.onPostTransform(context) }
        }

        val w1 = this.durations.keys.map {
            it.javaClass.name.length
        }.max() ?: 20
        this.durations.forEach { (transformer, ns) ->
            PrivacyPluginUtil.privacyPluginUtil.i("transform time :${transformer.javaClass.name.padEnd(w1 + 1)}: ${ns / 1000000} ms")
            "transform time :${transformer.javaClass.name.padEnd(w1 + 1)}: ${ns / 1000000} ms".privacyPrintln()
        }
    }

    override fun onPreTransform(context: TransformContext) {
        transformers.forEach { it ->
            threadMxBean.privacySumTime(
                it
            ) { it.onPreTransform(context) }
        }
    }

    override fun transform(context: TransformContext, bytecode: ByteArray): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            this.transformers.fold(ClassNode().also { klass ->
                try{
                    ClassReader(bytecode).accept(klass, 0)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }) { klass, transformer ->
                this.threadMxBean.privacySumTime(transformer) {
                    transformer.transform(context, klass)
                }
            }.accept(writer)
        }.toByteArray()
    }

    private fun <R> ThreadMXBean.privacySumTime(transformer: ClassTransformer, action: () -> R): R {
        val start = this.currentThreadCpuTime
        val result = action()
        val end = this.currentThreadCpuTime
        durations[transformer] = durations.getOrDefault(transformer, 0) + end - start
        return result
    }


}