package com.yl.lib.plugin.sentry.task

import com.android.utils.XmlUtils.parseUtfXmlFile
import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.gradle.api.logging.Logger
import java.io.File
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource

/**
 * @author yulun
 * @since 2022-11-17 14:04
 */
class ManifestProcessor {
    object Processor {
        //  暂时空载，后面想搞了再说
        fun process(manifestPath: String, privacyExtension: PrivacyExtension, logger: Logger) {
            try {
                var manifestOutputFile = File(manifestPath)
                var document = parseUtfXmlFile(manifestOutputFile, true)
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
                                    logger.info("setExportedProperties-${item.attributes}")
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
                                            privacyExtension.serviceExportPkgWhiteList ?: emptySet()
                                        )
                                    ) {
                                        if (element.hasAttribute("android:exported")) {
                                            if (element.getAttribute("android:exported")
                                                    .equals("true")
                                            ) {
                                                element.setAttribute("android:exported", "false")
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
                var realFile = File(manifestOutputFile.getParentFile(), "realFile.xml")
                if (!realFile.exists()) {
                    realFile.createNewFile()
                }
                var xmlResult = javax.xml.transform.stream.StreamResult(
                    realFile
                )
                transFormer.transform(domSource, xmlResult)
                manifestOutputFile.delete()
                realFile.renameTo(manifestOutputFile)
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

}