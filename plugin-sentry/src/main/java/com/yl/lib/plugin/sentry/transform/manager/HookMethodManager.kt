package com.yl.lib.plugin.sentry.transform.manager

/**
 * @author yulun
 * @sinice 2021-12-14 11:39
 * 汇总所有的hook方法配置
 */
open class HookMethodManager {
    object MANAGER {
        private var hookMethodList: HashSet<HookMethodItem> = HashSet()

        /**
         * 检测是否需要代理某个方法
         * @param methodName String
         * @param classOwnerName String
         * @param methodReturnDesc String
         * @return Boolean
         */
        fun contains(
            methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = "",
            opcodeAndSource:Int
        ): Boolean {
            if (methodName == "") {
                return false
            }
            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc,opcodeAndSource)
            } != null
        }

        fun findHookItemByName(
            methodName: String
        ): HookMethodItem? {
            return findHookItemByName(methodName, "", "",-1)
        }

        /**
         * 找到代理方法
         * @param methodName String
         * @param classOwnerName String
         * @param methodReturnDesc String
         * @return HookMethodItem?
         */
        fun findHookItemByName(
            methodName: String, classOwnerName: String = "",
            methodReturnDesc: String = "",
            opcodeAndSource:Int
        ): HookMethodItem? {
            if (methodName == "") {
                return null
            }

            return hookMethodList.find {
                isHookMethodItem(it, methodName, classOwnerName, methodReturnDesc,opcodeAndSource)
            }
        }

        /**
         * 判断当前方法是否可以被代理
         * @param hookItem HookMethodItem
         * @param methodName String
         * @param classOwnerName String
         * @param methodReturnDesc String
         * @return Boolean
         */
        private fun isHookMethodItem(
            hookItem: HookMethodItem, methodName: String,
            classOwnerName: String = "",
            methodReturnDesc: String = "",
            opcodeAndSource:Int
        ): Boolean {
            if (methodName.isEmpty()) {
                return false
            }

            // 如果忽略类名，只要方法名和签名相同就可以
            val replaceClassOwnerName = if (hookItem.ignoreClass) {
                ""
            } else {
                classOwnerName
            }

            return if (replaceClassOwnerName.isEmpty() && methodReturnDesc.isNotEmpty()) {
                methodName == hookItem.originMethodName && methodReturnDesc == hookItem.originMethodDesc && opcodeAndSource == hookItem.originMethodAccess
            } else if (replaceClassOwnerName.isNotEmpty() && methodReturnDesc.isEmpty()) {
                methodName == hookItem.originMethodName && replaceClassOwnerName == hookItem.originClassName && opcodeAndSource == hookItem.originMethodAccess
            } else if (replaceClassOwnerName.isNotEmpty() && methodReturnDesc.isNotEmpty()) {
                methodName == hookItem.originMethodName && replaceClassOwnerName == hookItem.originClassName && methodReturnDesc == hookItem.originMethodDesc && opcodeAndSource == hookItem.originMethodAccess
            } else {
                methodName == hookItem.originMethodName
            }
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
                // 1. 先扫描到privacy自身的配置方法，需要代理掉HashSet里的方法
                // 2. 先扫描到业务自身的配置，那过滤掉不处理
                // 3. 如果业务方重复定义，那就没办法了，最后被扫描到的会被加入
                var bPrivacyItem =
                    hookMethodItem.proxyClassName.contains("com.yl.lib.privacy_proxy")
                if (!bPrivacyItem) {
                    hookMethodList.removeIf { it == hookMethodItem }
                    hookMethodList.add(hookMethodItem)
                }
                return
            }
            hookMethodList.add(
                hookMethodItem
            )
        }

        //判断加载的字符串常量
        fun findByClsOrMethod(
            name: String
        ): Boolean {
            return hookMethodList.find {
                name.contains(it.originClassName ?: "")  ||  name.contains(it.originMethodName ?: "")
            } !=null
        }

        /**
         * 兼容kotlin lambda表达式，lambda会生成新的类，导致库本身的屏蔽失效
         * @param className String
         * @return Boolean
         */
        fun isProxyClass(className: String): Boolean {
            return hookMethodList.find {
                it.proxyClassName == className || className.startsWith(it.proxyClassName)
            } != null
        }

        /**
         * 由于变量是静态的，防止gradle进程有缓存
         */
        fun clear() {
            hookMethodList.clear()
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

    var ignoreClass : Boolean = false

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