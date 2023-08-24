package com.yl.lib.plugin.sentry.transform.booster.task

import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.project
import com.yl.lib.plugin.sentry.transform.loadPrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyClassTestProxy
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.*

/**
 * @author yulun
 * @since 2023-08-24 10:57
 */
open class PrivacyCollectTask : DefaultTask() {

    lateinit var variant: BaseVariant

    @TaskAction
    fun doCollect() {
        var list = loadPrivacyClassProxy(variant.project)
        val privacyClassProxyList =
            ServiceLoader.load(PrivacyClassTestProxy::class.java, Thread.currentThread().contextClassLoader).toList()

        val privacyClassProxyList1 =
            ServiceLoader.load(PrivacyClassTestProxy::class.java, variant.project.buildscript.classLoader).toList()
        println("PrivacyCollectTask doCollect")
    }
}