package com.yl.lib.plugin.sentry.transform.manager

/**
 * @author yulun
 * @since 2022-11-18 15:09
 * 类代理，暂时主要用于构造函数代理
 */
class ReplaceClassManager {
    object MANAGER {
        private var replaceClassSet: HashSet<ReplaceClassItem> = HashSet()

        /**
         *
         * @param originClassName String?
         * @param proxyClassName String?
         * @return Boolean
         */
        fun contains(
            originClassName: String? = "",
            proxyClassName: String? = ""
        ): Boolean {
            return replaceClassSet.find {
                isHookItem(it, originClassName ?: "", proxyClassName ?: "")
            } != null
        }


        fun findItemByName(
            originClassName: String? = "", proxyClassName: String? = ""
        ): ReplaceClassItem? {
            return replaceClassSet.find {
                isHookItem(it, originClassName ?: "", proxyClassName ?: "")
            }
        }

        private fun isHookItem(
            hookItem: ReplaceClassItem, originClassName: String,
            proxyClassName: String = ""
        ): Boolean {
            return if (originClassName.isEmpty() && proxyClassName.isEmpty()) {
                false
            } else if (originClassName.isNotEmpty() && proxyClassName.isEmpty()) {
                hookItem.originClassName == originClassName
            } else if (originClassName.isEmpty() && proxyClassName.isNotEmpty()) {
                hookItem.proxyClassName == proxyClassName
            } else {
                originClassName == hookItem.originClassName && proxyClassName == hookItem.proxyClassName
            }
        }

        /**
         * 追加待代理的类
         * @param originClassName String
         * @param proxyClassName String
         */
        fun appendHookItem(
            originClassName: String,
            proxyClassName: String
        ) {
            replaceClassSet.add(
                ReplaceClassItem(
                    originClassName = originClassName,
                    proxyClassName = proxyClassName
                )
            )
        }

        /**
         * 加入hook集合
         * @param replaceClassItem ReplaceClassItem
         */
        fun appendHookItem(
            replaceClassItem: ReplaceClassItem
        ) {
            if (replaceClassSet.contains(replaceClassItem)) {
                // 这里有两种情况
                // 1. 先扫描到privacy自身的配置方法，需要代理掉HashSet里的方法
                // 2. 先扫描到业务自身的配置，那过滤掉不处理
                // 3. 如果业务方重复定义，那就没办法了，最后被扫描到的会被加入
                var bPrivacyItem =
                    replaceClassItem.proxyClassName.contains("com.yl.lib.privacy_replace")
                if (!bPrivacyItem) {
                    replaceClassSet.removeIf { it == replaceClassItem }
                    replaceClassSet.add(replaceClassItem)
                }
                return
            }
            replaceClassSet.add(
                replaceClassItem
            )
        }

        /**
         * 兼容kotlin lambda表达式，lambda会生成新的类，导致库本身的屏蔽失效
         * @param className String
         * @return Boolean
         */
        fun isProxyClass(className: String): Boolean {
            return replaceClassSet.find {
                it.proxyClassName == className || className.startsWith(it.proxyClassName)
            } != null
        }

        /**
         * 由于变量是静态的，防止gradle进程有缓存
         */
        fun clear() {
            replaceClassSet.clear()
        }
    }
}

class ReplaceClassItem {
    // 原始类名
    var originClassName: String

    // 代理的类名
    var proxyClassName: String

    constructor(originClassName: String, proxyClassName: String) {
        this.originClassName = originClassName
        this.proxyClassName = proxyClassName
    }


    override fun equals(other: Any?): Boolean {
        if (other is HookFieldItem) {
            return (other.originClassName == originClassName &&
                    other.originClassName == originClassName)
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return originClassName.hashCode() + originClassName.hashCode()
    }

    override fun toString(): String {
        return "originClassName is $originClassName , proxyClassName is $proxyClassName"
    }
}