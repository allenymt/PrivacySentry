package com.yl.lib.plugin.sentry.transform

/**
 * @author yulun
 * @sinice 2021-12-14 11:39
 * 汇总所有的hook方法配置
 */
open class HookMethodManager {
    object MANAGER {
        private var hookMethodList: HashSet<HookMethodItem> = HashSet()
        /**
         * 检测是否需要替换某个方法
         * @param methodName String
         * @param classOwnerName String
         * @param methodReturnDesc String
         * @return Boolean
         */
        fun contains(
            methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): Boolean {
            if (methodName == "") {
                return false
            }
            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc)
            } != null
        }

        fun findHookItemByName(
            methodName: String
        ): HookMethodItem? {
            return findHookItemByName(methodName, "", "")
        }

        fun findHookItemByName(
            methodName: String, classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): HookMethodItem? {
            if (methodName == "") {
                return null
            }

            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc)
            }
        }

        private fun isHookMethodItem(
            hookItem: HookMethodItem, methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = ""
        ): Boolean {
            if (methodName.isEmpty()) {
                return false
            }
            return if (classOwnerName.isEmpty() && methodReturnDesc.isNotEmpty()) {
                methodName == hookItem.originMethodName && methodReturnDesc == hookItem.originMethodDesc
            } else if (classOwnerName.isNotEmpty() && methodReturnDesc.isEmpty()) {
                methodName == hookItem.originMethodName && classOwnerName == hookItem.originClassName
            } else if (classOwnerName.isNotEmpty() && methodReturnDesc.isNotEmpty()) {
                methodName == hookItem.originMethodName && classOwnerName == hookItem.originClassName && methodReturnDesc == hookItem.originMethodDesc
            } else {
                methodName == hookItem.originMethodName
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
        fun appendHookMethod(
            originClassName: String,
            originMethodName: String,
            originMethodAccess: Int,
            originMethodReturnDesc: String,
            proxyClassName: String,
            proxyMethodName: String,
            proxyMethodReturnDesc: String,
            documentMethodDesc: String
        ) {
            hookMethodList.add(
                HookMethodItem(
                    originClassName = originClassName,
                    originMethodName = originMethodName,
                    originMethodDesc = originMethodReturnDesc,
                    originMethodAccess = originMethodAccess,
                    proxyClassName = proxyClassName,
                    proxyMethodName = proxyMethodName,
                    proxyMethodDesc = proxyMethodReturnDesc
                )
            )
        }

        /**
         * 加入hook集合
         * 2022.06.15新增功能，支持业务方定义hook方法覆盖库内的方法
         * @param hookMethodItem HookMethodItem
         */
        fun appendHookMethod(
            hookMethodItem: HookMethodItem
        ) {
            if (hookMethodList.contains(hookMethodItem)) {
                // 这里有两种情况
                // 1. 先扫描到privacy自身的配置方法，需要替换掉HashSet里的方法
                // 2. 先扫描到业务自身的配置，那过滤掉不处理
                // 3. 如果业务方重复定义，那就没办法了，最后被扫描到的会被加入
                var bPrivacyItem =
                    hookMethodItem.proxyClassName.contains("com.yl.lib.privacy_proxy")
                if (!bPrivacyItem){
                    hookMethodList.removeIf { it == hookMethodItem }
                    hookMethodList.add(hookMethodItem)
                }
                return
            }
            hookMethodList.add(
                hookMethodItem
            )
        }
    }
}

class HookMethodItem {
    // 原始类名
    var originClassName: String? = ""

    // 原始方法名
    var originMethodName: String? = ""

    // 原始方法签名
    var originMethodDesc: String? = ""

    var originMethodAccess: Int? = 0

    // 代理的类名
    var proxyClassName: String

    // 代理的方法名
    var proxyMethodName: String

    // 代理的方法签名
    var proxyMethodDesc: String

    constructor(
        proxyClassName: String,
        proxyMethodName: String,
        proxyMethodReturnDesc: String
    ) {
        this.proxyClassName = proxyClassName
        this.proxyMethodName = proxyMethodName
        this.proxyMethodDesc = proxyMethodReturnDesc
    }

    constructor(
        originClassName: String,
        originMethodName: String,
        originMethodDesc: String,
        originMethodAccess: Int,
        proxyClassName: String,
        proxyMethodName: String,
        proxyMethodDesc: String
    ) {
        this.originClassName = originClassName
        this.originMethodName = originMethodName
        this.originMethodDesc = originMethodDesc
        this.originMethodAccess = originMethodAccess
        this.proxyClassName = proxyClassName
        this.proxyMethodName = proxyMethodName
        this.proxyMethodDesc = proxyMethodDesc
    }

    override fun equals(other: Any?): Boolean {
        if (other is HookMethodItem) {
            return (other.originMethodAccess == originMethodAccess &&
                    other.originClassName == originClassName &&
                    other.originMethodName == originMethodName &&
                    other.originMethodDesc == originMethodDesc)
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return originMethodAccess.hashCode() + originClassName.hashCode() + originMethodName.hashCode() + originMethodDesc.hashCode()
    }
}