package com.yl.lib.privacy_ui.replace

import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil
import kotlinx.coroutines.*

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

    fun buildData(context: Context) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                var data = loadReplaceFile(context, "privacy/replace.json")
                data.let {
                    var result = fromJson(it!!,
                        object : TypeReference<HashMap<String, ReplaceItemList>>() {})
                    result?.let {
                        withContext(Dispatchers.Main) {
                            originData = transformData(result!!)
                            replaceData?.postValue(originData)
                        }
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
            originData?.filter { it.proxyMethodName?.toLowerCase()?.contains(searchText) ?: false }
                ?.let {
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

    private fun loadReplaceFile(context: Context, fileName: String): String? {
        try {
            return PrivacyUtil.Util.convertStreamToString(context.assets.open(fileName))
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