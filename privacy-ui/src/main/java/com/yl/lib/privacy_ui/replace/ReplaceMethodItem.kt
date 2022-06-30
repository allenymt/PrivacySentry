package com.yl.lib.privacy_ui.replace

class ReplaceMethodItem {
    // 原始类名，指的是调用敏感方法的 类和方法
    var originClassName: String? = ""

    // 原始方法名
    var originMethodName: String? = ""

    // 代理方法名，即敏感方法
    var proxyMethodName: String? = ""

    // 加个类名是为了保证key的唯一性，虽然系统方法重名的概率很低很低
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