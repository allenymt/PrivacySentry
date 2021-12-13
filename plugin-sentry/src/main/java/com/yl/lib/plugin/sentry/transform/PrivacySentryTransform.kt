package com.yl.lib.plugin.sentry.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import org.gradle.api.Project

/**
 * @author yulun
 * @sinice 2021-12-13 17:10
 */
class PrivacySentryTransform : Transform {

    private val project: Project by lazy { project }

    constructor(project: Project)

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        TODO("Not yet implemented")
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        TODO("Not yet implemented")
    }

    override fun isIncremental(): Boolean {
        TODO("Not yet implemented")
    }
}