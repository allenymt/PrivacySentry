package com.yl.lib.privacy_ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.yl.lib.sentry.hook.PrivacySentry
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

/**
 * 展示静态替换数据
 * @property searchBar SearchView?
 * @property recyclerView RecyclerView?
 * @property progressBar ProgressBar
 */
class ReplaceListActivity : AppCompatActivity() {

    var searchBar: SearchView? = null

    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replace_list)
        searchBar = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.content)
    }

    var progressBar:ProgressBar = ProgressBar(this)

    private fun buildData(){
        progressBar.showContextMenu()
        try{
            var fis = FileInputStream(PrivacySentry.Privacy.replaceFilePath)
//            fis.re
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun loadConfigFile(filePath: String): String? {
        try {
            var file = File(filePath + File.separator + CONFIG_JSON_NAME)
            if (file.exists()) {
                val data = EmoticonUtil.convertStreamToByte(FileInputStream(file))
                if (data != null) {
                    return String(data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}