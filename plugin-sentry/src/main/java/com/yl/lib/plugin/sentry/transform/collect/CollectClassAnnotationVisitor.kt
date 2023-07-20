package com.yl.lib.plugin.sentry.transform.collect

import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassItem
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import org.gradle.api.logging.Logger
import org.objectweb.asm.AnnotationVisitor

/**
 * @author yulun
 * @since 2023-07-20 17:16
 * @see com.yl.lib.privacy_annotation.PrivacyClassReplace
 * 收集待代理的类
 */
class CollectClassAnnotationVisitor : AnnotationVisitor {
    private var logger: Logger
    private var className: String

    constructor(
        api: Int,
        annotationVisitor: AnnotationVisitor?,
        className: String,
        logger: Logger
    ) : super(api, annotationVisitor) {
        this.logger = logger
        this.className = className
    }

    var item: ReplaceClassItem? = null
    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        if (name.equals("originClass")) {
            var classSourceName = value.toString()
            item = ReplaceClassItem(
                originClassName = classSourceName.substring(1, classSourceName.length - 1),
                proxyClassName = className
            )
            logger.info("CollectClassAnnotationVisitor-ReplaceClassItem - ${item.toString()}")
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        item?.let {
            ReplaceClassManager.MANAGER.appendHookItem(item!!)
        }
    }
}