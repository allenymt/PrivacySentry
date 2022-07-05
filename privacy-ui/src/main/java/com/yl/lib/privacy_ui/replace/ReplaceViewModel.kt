package com.yl.lib.privacy_ui.replace

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.vdian.android.lib.executor.VExecutorManager
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.io.File
import java.io.FileInputStream

/**
 * @author yulun
 * @since 2022-07-04 15:55
 */
@Keep
class ReplaceViewModel : ViewModel() {
    var replaceData: MutableLiveData<ArrayList<ReplaceItemList>>? = null
    var originData: ArrayList<ReplaceItemList>? = null

    fun observer(): LiveData<ArrayList<ReplaceItemList>> {
        if (replaceData == null) {
            replaceData = MutableLiveData<ArrayList<ReplaceItemList>>()
        }
        return replaceData!!
    }

    fun buildData() {
        try {
            //  替换成协程的实现
            VExecutorManager.INSTANCE.io().submit {
                var data = loadReplaceFile(PrivacySentry.Privacy.buildrReplaceFilePath())
                data.let {
                    var result = fromJson(it!!,
                        object : TypeReference<HashMap<String, ReplaceItemList>>() {})
                    result?.let {
                        VExecutorManager.INSTANCE.main().execute(Runnable {
                            originData = transformData(result!!)
                            replaceData?.postValue(originData)
                        })
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun search(searchText: String?) {
        if (searchText == null || searchText.isEmpty()) {
            replaceData?.postValue(originData)
        } else {
            replaceData?.value?.filter { it.proxyMethodName?.toLowerCase()?.contains(searchText) ?: false }?.let {
                replaceData?.postValue(it as ArrayList<ReplaceItemList> /* = java.util.ArrayList<com.yl.lib.privacy_ui.replace.ReplaceItemList> */)
            }
        }
    }

    private fun transformData(src: HashMap<String, ReplaceItemList>): ArrayList<ReplaceItemList> {
        return src.map {
            it.value.proxyMethodName = it.key
            it.value
        } as ArrayList<ReplaceItemList>
    }

    private fun loadReplaceFile(fileName: String): String? {
        try {
            var file = File(fileName)
            if (file.exists()) {
                return PrivacyUtil.Util.convertStreamToString(FileInputStream(file))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun <T> fromJson(json: String, typeReference: TypeReference<T>): T? {
        return try {
            return JSON.parseObject<T>(
                json,
                typeReference
            ) as T
        } catch (var3: Exception) {
            var3.printStackTrace()
            null
        }
    }
}