package com.yl.lib.plugin.sentry.transform

import com.didiglobal.booster.transform.*
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.Project
import java.io.File

/**
 * @author yulun
 * @since 2023-08-09 14:44
 */
abstract class PrivacyTransformInvocation(
    applicationId: String,
    name: String,
    bootClasspath: Collection<File>,
    compileClasspath: Collection<File>,
    runtimeClasspath: Collection<File>,
    private val project: Project,
    private val extension: PrivacyExtension,
) : AbstractTransformContext(
    applicationId,
    name,
    bootClasspath,
    compileClasspath,
    runtimeClasspath,
), PrivacyTransformContext {


    override fun project(): Project {
        return project
    }

    override fun privacyExtension(): PrivacyExtension {
        return extension
    }


}