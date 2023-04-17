package com.yl.lib.sentry.hook.watcher

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.yl.lib.sentry.hook.printer.PrivacyFunBean
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author yulun
 * @since 2022-07-05 16:26
 * 实时hook数据 管理中心，负责分发
 */
class PrivacyDataManager {
    object Manager {
        private var privacyFunBeanList: CopyOnWriteArrayList<PrivacyFunBean> =
            CopyOnWriteArrayList()

        private var liveItemPrivacy: MutableLiveData<PrivacyFunBean> = MutableLiveData()

        private var stickFunBeanList: CopyOnWriteArrayList<PrivacyFunBean> = CopyOnWriteArrayList()
        fun addData(bean: PrivacyFunBean) {
            assert(bean != null)
            privacyFunBeanList.add(bean)
            liveItemPrivacy.postValue(bean)
        }

        fun addStickData(bean: PrivacyFunBean) {
            assert(bean != null)
            stickFunBeanList.add(bean)
        }

        fun getFunBeanList(): CopyOnWriteArrayList<PrivacyFunBean> {
            if (stickFunBeanList.isNotEmpty()) {
                privacyFunBeanList.addAll(stickFunBeanList)
                stickFunBeanList.clear()
            }
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