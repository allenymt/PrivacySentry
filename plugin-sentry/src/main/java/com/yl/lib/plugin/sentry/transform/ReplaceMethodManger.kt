package com.yl.lib.plugin.sentry.transform

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature
import org.gradle.api.Project
import org.gradle.util.GFileUtils
import java.io.File

/**
 * @author yulun
 * @since 2022-06-16 20:06
 * 记录所有被替换的方法和类列表，方便做数据比对
 */
class ReplaceMethodManger {
    object MANAGER {
        private var replaceMethodMap: HashMap<String, ReplaceMethodData> = HashMap()

        fun addReplaceMethodItem(methodItem: ReplaceMethodItem) {
            var key = buildKey(methodItem)
            var replaceMethodData = replaceMethodMap[key]
            if (replaceMethodData == null) {
                replaceMethodData = ReplaceMethodData()
            }
            replaceMethodData.addReplaceMethodItem(replaceMethodItem = methodItem)
            replaceMethodMap[key] = replaceMethodData
        }

        fun flushToFile(fileName: String, project: Project) {
            if (fileName == null || replaceMethodMap.isEmpty()) {
                return
            }
            project.logger.debug("flushToFile")
            var resultFile = File(project.buildDir.absolutePath + File.separator + fileName)
            if (resultFile?.parentFile != null && !resultFile.parentFile.exists()) {
                GFileUtils.mkdirs(resultFile)
            }

            resultFile?.let {
                GFileUtils.deleteQuietly(resultFile)
            }
            GFileUtils.writeFile(
                objectToJsonString(
                    replaceMethodMap.toList().sortedByDescending { it.second.count }.toMap()
                ), resultFile
            )
        }

        private fun buildKey(methodItem: ReplaceMethodItem): String {
            return "${methodItem.proxyMethodClass}.${methodItem.proxyMethodName}"
        }

        private fun objectToJsonString(o: Any?): String? {
            return if (null != o) {
                try {
                    JSON.toJSONString(
                        o, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat
                    );
                } catch (var2: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }
}

class ReplaceMethodData {
    var count: Int = 0
    var originMethodList: ArrayList<ReplaceMethodItem>? = null

    constructor() {
        originMethodList = ArrayList()
    }

    fun addReplaceMethodItem(replaceMethodItem: ReplaceMethodItem) {
        if (originMethodList?.contains(replaceMethodItem) == true) {
            return
        }
        count++
        originMethodList?.add(replaceMethodItem)
    }
}

class ReplaceMethodItem {
    // 原始类名，指的是调用敏感方法的 类和方法
    var originClassName: String? = ""

    // 原始方法名
    var originMethodName: String? = ""

    // 代理方法名，即敏感方法
    @JSONField(serialize = false)
    var proxyMethodName: String? = ""

    // 加个类名是为了保证key的唯一性，虽然系统方法重名的概率很低很低
    @JSONField(serialize = false)
    var proxyMethodClass: String? = ""

    constructor(
        originClassName: String,
        originMethodName: String,
        proxyMethodClass: String,
        proxyMethodName: String
    ) {
        this.originClassName = originClassName
        this.originMethodName = originMethodName
        this.proxyMethodClass = proxyMethodClass
        this.proxyMethodName = proxyMethodName

    }

    override fun equals(other: Any?): Boolean {
        if (other is ReplaceMethodItem) {
            return (other.proxyMethodName == proxyMethodName &&
                    other.originClassName == originClassName &&
                    other.originMethodName == originMethodName &&
                    other.proxyMethodClass == proxyMethodClass)
        }

        return super.equals(other)
    }

    override fun toString(): String {
        return "原始类名: $originClassName, 原始方法名: $originMethodName\n"
    }


    override fun hashCode(): Int {
        return proxyMethodName.hashCode() + originClassName.hashCode() + originMethodName.hashCode() + proxyMethodClass.hashCode()
    }
}
