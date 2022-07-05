package com.yl.lib.sentry.hook.watcher

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.yl.lib.sentry.hook.printer.PrivacyFunBean

/**
 * @author yulun
 * @since 2022-07-05 16:26
 * 实时hook数据 管理中心，负责分发
 */
class PrivacyDataManager {
    object Manager {
        private var privacyFunBeanList: ArrayList<PrivacyFunBean> = ArrayList()

        private var liveItemPrivacy: MutableLiveData<PrivacyFunBean> = MutableLiveData()

        fun addData(bean: PrivacyFunBean) {
            assert(bean != null)
            privacyFunBeanList.add(bean)
            liveItemPrivacy.value = (bean)
        }

        fun getFunBeanList(): ArrayList<PrivacyFunBean> {
            return privacyFunBeanList
        }

        fun isEmpty(): Boolean {
            return privacyFunBeanList.isEmpty()
        }


        fun observerItem(lifecycleOwner: LifecycleOwner, observer: Observer<PrivacyFunBean>) {
            liveItemPrivacy.observe(lifecycleOwner, observer)
        }
    }
}