package com.yl.lib.privacy.processor

import com.google.auto.service.AutoService
import com.yl.lib.privacy_annotation.PrivacyClassBlack
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyClassReplace
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * @author yulun
 * @since 2023-08-23 15:46
 */
@AutoService(Processor::class)
open class PrivacyAptProcessor : AbstractProcessor() {


    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        System.out.println("PrivacyAptProcessor getSupportedAnnotationTypes")
        return mutableSetOf(
            PrivacyClassProxy::class.java.canonicalName,
            PrivacyClassBlack::class.java.canonicalName,
            PrivacyClassReplace::class.java.canonicalName
        )
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        var elementsClassProxy = roundEnv?.getElementsAnnotatedWith(PrivacyClassProxy::class.java)
        var elementsClassReplace =
            roundEnv?.getElementsAnnotatedWith(PrivacyClassReplace::class.java)
        var elementsClassBlack = roundEnv?.getElementsAnnotatedWith(PrivacyClassBlack::class.java)

        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }


}