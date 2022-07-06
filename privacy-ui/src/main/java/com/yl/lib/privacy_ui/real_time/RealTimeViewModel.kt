package com.yl.lib.privacy_ui.real_time

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.yl.lib.sentry.hook.printer.PrivacyFunBean
import com.yl.lib.sentry.hook.watcher.PrivacyDataManager

/**
 * @author yulun
 * @since 2022-07-04 15:55
 */
@Keep
class RealTimeViewModel : ViewModel() {
    var replaceData: MutableLiveData<ArrayList<PrivacyFunBean>>? = null
    var originData: ArrayList<PrivacyFunBean>? = null

    fun observer(): LiveData<ArrayList<PrivacyFunBean>> {
        if (replaceData == null) {
            replaceData = MutableLiveData<ArrayList<PrivacyFunBean>>()
        }
        return replaceData!!
    }

    fun buildData(lifecycleOwner: LifecycleOwner) {
        try {
            PrivacyDataManager.Manager.getFunBeanList().let {
                originData = arrayListOf<PrivacyFunBean>()
                originData?.addAll(it)
                replaceData?.value = originData
            }

            PrivacyDataManager.Manager.observerItem(
                lifecycleOwner,
                observer = Observer<PrivacyFunBean> { t ->
                    if (originData?.contains(t) == true) {
                        return@Observer
                    }
                    originData?.add(t!!)
                    replaceData?.value = originData
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun search(searchText: String?) {
        if (searchText == null || searchText.isEmpty()) {
            replaceData?.postValue(originData)
        } else {
            originData?.filter {
                it.funAlias?.toLowerCase()?.contains(searchText) ?: false
            }?.let {
                replaceData?.postValue(it as ArrayList<PrivacyFunBean> /* = java.util.ArrayList<com.yl.lib.privacy_ui.replace.ReplaceItemList> */)
            }
        }
    }
}