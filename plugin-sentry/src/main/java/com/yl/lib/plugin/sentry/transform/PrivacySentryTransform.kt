package com.yl.lib.plugin.sentry.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

/**
 * @author yulun
 * @sinice 2021-12-13 17:10
 */
class PrivacySentryTransform : Transform {

    private val project: Project by lazy { project }

    constructor(project: Project)

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
    }
}