package com.yl.lib.plugin.sentry.transform.booster.task

import com.android.SdkConstants
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationVariant
import com.android.utils.XmlUtils.parseUtfXmlFile
import com.didiglobal.booster.gradle.mergedManifests
import com.didiglobal.booster.kotlinx.search
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import com.yl.lib.plugin.sentry.util.PrivacyPluginUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource

/**
 * @author yulun
 * @since 2022-11-17 14:04
 * 处理manifest文件，目前主要针对Service
 *  1. 关闭Service的Export
 *  2. 部分Service的priority设置为1000，可以强行替换
 */
open class PrivacyManifestTask : DefaultTask() {
    @get:Internal
    lateinit var variant: ApplicationVariant

    @get:Internal
    lateinit var privacyExtension: PrivacyExtension

    @TaskAction
    fun process() {
        try {
            variant.artifacts.get(SingleArtifact.MERGED_MANIFEST)
                .get().asFile.let { manifestOutputFile ->
                // 修改文件路径，将 merged_manifest 替换为 merged_manifests
                val correctedManifestFile = File(
                    manifestOutputFile.absolutePath.replace(
                        "/merged_manifest/",
                        "/merged_manifests/"
                    )
                )
                var document = parseUtfXmlFile(correctedManifestFile, true)
                var root = document.documentElement
                if (root != null) {
                    var children = root.childNodes
                    for (i in 0 until children.length) {
                        var application = children.item(i)

                        if (application.nodeType == org.w3c.dom.Node.ELEMENT_NODE &&
                            application.nodeName.equals("application")
                        ) {
                            //遍历获取 Service
                            var comps = application.childNodes
                            for (j in 0 until comps.length) {
                                var item = comps.item(j)
                                if (item.nodeType == org.w3c.dom.Node.ELEMENT_NODE &&
                                    item.nodeName.equals("service")
                                ) {
                                    // 处理Service
                                    PrivacyPluginUtil.privacyPluginUtil.i("setExportedProperties-${item.attributes}")
                                    var element = item as org.w3c.dom.Element
                                    // 处理Service 的 priority
                                    if (privacyExtension.enableReplacePriority && element.hasAttribute(
                                            "android:priority"
                                        )
                                    ) {
                                        var priority = element.getAttribute("android:priority")
                                        var bReplacePriority = false
                                        try {
                                            var p = Integer.parseInt(priority)
                                            if (p >= privacyExtension.replacePriority) {
                                                bReplacePriority = true
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        if (bReplacePriority) {
                                            element.setAttribute(
                                                "android:priority",
                                                privacyExtension.replacePriority.toString()
                                            )
                                        }
                                    }

                                    // 强行把Service的exported属性设置为false
                                    if (privacyExtension.enableCloseServiceExport && !isServiceExportWhite(
                                            element.getAttribute("android:name"),
                                            privacyExtension.serviceExportPkgWhiteList
                                                ?: emptySet()
                                        )
                                    ) {
                                        if (element.hasAttribute("android:exported")) {
                                            if (element.getAttribute("android:exported")
                                                    .equals("true")
                                            ) {
                                                element.setAttribute(
                                                    "android:exported",
                                                    "false"
                                                )
                                            }
                                        } else {
                                            element.setAttribute("android:exported", "false")
                                        }
                                    }
                                }
                            }
                            break
                        }
                    }
                }
                var transFactory = TransformerFactory.newInstance()
                var transFormer = transFactory.newTransformer()
                transFormer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes")
                var domSource = DOMSource(
                    document
                )
                var realFile = File(correctedManifestFile.parentFile, "realFile.xml")
                if (!realFile.exists()) {
                    realFile.createNewFile()
                }
                var xmlResult = javax.xml.transform.stream.StreamResult(
                    realFile
                )
                transFormer.transform(domSource, xmlResult)
                correctedManifestFile.delete()
                realFile.renameTo(correctedManifestFile)
            }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

    private fun isServiceExportWhite(
        serviceName: String,
        servicePkgWhiteList: Set<String>
    ): Boolean {
        servicePkgWhiteList.forEach {
            if (serviceName.toUpperCase().contains(it.toUpperCase())) {
                return true
            }
        }
        return false
    }

}