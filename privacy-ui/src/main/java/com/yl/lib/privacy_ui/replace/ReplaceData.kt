package com.yl.lib.privacy_ui.replace


class ReplaceItemList(val count: Int, val originMethodList: ArrayList<ReplaceItem>) {
    var proxyMethodName:String? = null
    constructor() : this(0, arrayListOf())
}

/**
 *
 * @property originClassName String 原始类名，指的是调用敏感方法的 类和方法

 * @property originMethodName String 原始方法名

 * @constructor
 */
class ReplaceItem(val originClassName: String, val originMethodName: String) {
    constructor() : this("", "")

    override fun equals(other: Any?): Boolean {
        if (other is ReplaceItem) {
            return (
                    other.originClassName == originClassName &&
                            other.originMethodName == originMethodName)
        }

        return super.equals(other)
    }

    override fun toString(): String {
        return "原始类名: $originClassName, 原始方法名: $originMethodName\n"
    }

}