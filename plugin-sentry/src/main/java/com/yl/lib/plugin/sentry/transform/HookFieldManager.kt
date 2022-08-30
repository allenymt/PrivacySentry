package com.yl.lib.plugin.sentry.transform

/**
 * @author yulun
 * @since 2022-08-30 11:10
 */
open class HookFieldManager {
    object MANAGER {
        private var hookFieldSet: HashSet<HookFieldItem> = HashSet()
        /**
         * 检测是否需要替换某个变量
         * @param fieldName String
         * @param classOwnerName String
         * @param methodReturnDesc String
         * @return Boolean
         */
        fun contains(
            fieldName: String? = "",
            classOwnerName: String? = "",
            fieldDesc: String? = ""
        ): Boolean {
            if (fieldName == "") {
                return false
            }
            return hookFieldSet.find {
                isHookFieldItem(it, fieldName ?:"", classOwnerName ?:"", fieldDesc ?:"")
            } != null
        }

        fun findHookItemByName(
            methodName: String
        ): HookFieldItem? {
            return findHookItemByName(methodName, "", "")
        }

        fun findHookItemByName(
            methodName: String ?= "", classOwnerName: String? = "",
            methodReturnDesc: String? = ""
        ): HookFieldItem? {
            if (methodName == "") {
                return null
            }

            return hookFieldSet.find {
                isHookFieldItem(it, methodName ?: "", classOwnerName ?: "", methodReturnDesc ?: "")
            }
        }

        private fun isHookFieldItem(
            hookItem: HookFieldItem, fieldName: String,
            classOwnerName: String = "",
            fieldDesc: String = ""
        ): Boolean {
            if (fieldName.isEmpty()) {
                return false
            }
            return if (classOwnerName.isEmpty() && fieldDesc.isNotEmpty()) {
                fieldName == hookItem.originFieldName && fieldDesc == hookItem.proxyFieldDesc
            } else if (classOwnerName.isNotEmpty() && fieldDesc.isEmpty()) {
                fieldName == hookItem.originFieldName && classOwnerName == hookItem.originClassName
            } else if (classOwnerName.isNotEmpty() && fieldDesc.isNotEmpty()) {
                fieldName == hookItem.originFieldName && classOwnerName == hookItem.originClassName && fieldDesc == hookItem.proxyFieldDesc
            } else {
                fieldName == hookItem.originFieldName
            }
        }

        /**
         * 追加hook方法
         * @param originClassName String 被代理方法的类名
         * @param originMethodName String 被代理的方法
         * @param proxyClassName String 代理方法的类名
         * @param proxyMethodName String 代理方法名
         * @param proxyMethodReturnDesc String 代理方法描述=被代理的方法描述
         * @param documentMethodDesc String 方法注释信息
         */
        fun appendHookField(
            originClassName: String,
            originFieldName: String,
            proxyClassName: String,
            proxyFieldName: String,
            proxyFieldDesc: String
        ) {
            hookFieldSet.add(
                HookFieldItem(
                    originClassName = originClassName,
                    originFieldName = originFieldName,
                    proxyClassName = proxyClassName,
                    proxyFieldName = proxyFieldName,
                    proxyFieldDesc = proxyFieldDesc
                )
            )
        }

        /**
         * 加入hook集合
         * 2022.06.15新增功能，支持业务方定义hook方法覆盖库内的方法
         * @param hookFieldItem HookMethodItem
         */
        fun appendHookField(
            hookFieldItem: HookFieldItem
        ) {
            if (hookFieldSet.contains(hookFieldItem)) {
                // 这里有两种情况
                // 1. 先扫描到privacy自身的配置方法，需要替换掉HashSet里的方法
                // 2. 先扫描到业务自身的配置，那过滤掉不处理
                // 3. 如果业务方重复定义，那就没办法了，最后被扫描到的会被加入
                var bPrivacyItem =
                    hookFieldItem.proxyClassName.contains("com.yl.lib.privacy_proxy")
                if (!bPrivacyItem){
                    hookFieldSet.removeIf { it == hookFieldItem }
                    hookFieldSet.add(hookFieldItem)
                }
                return
            }
            hookFieldSet.add(
                hookFieldItem
            )
        }
    }
}

class HookFieldItem {
    // 原始类名
    var originClassName: String? = ""

    // 原始变量名
    var originFieldName: String? = ""


    // 代理的类名
    var proxyClassName: String

    // 代理的变量名
    var proxyFieldName: String

    // 代理变量签名，默认和原始变量签名一致
    var proxyFieldDesc: String? = ""

    constructor(
        proxyClassName: String,
        proxyFieldName: String,
        proxyFieldDesc: String
    ) {
        this.proxyClassName = proxyClassName
        this.proxyFieldName = proxyFieldName
        this.proxyFieldDesc = proxyFieldDesc
    }

    constructor(
        originClassName: String,
        originFieldName: String,

        proxyClassName: String,
        proxyFieldName: String,
        proxyFieldDesc: String
    ) {
        this.originClassName = originClassName
        this.originFieldName = originFieldName
        this.proxyFieldDesc = proxyFieldDesc
        this.proxyClassName = proxyClassName
        this.proxyFieldName = proxyFieldName
    }

    override fun equals(other: Any?): Boolean {
        if (other is HookFieldItem) {
            return (other.originClassName == originClassName &&
                    other.originFieldName == originFieldName &&
                    other.proxyFieldDesc == proxyFieldDesc)
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return  originClassName.hashCode() + originFieldName.hashCode() + proxyFieldDesc.hashCode()
    }
}