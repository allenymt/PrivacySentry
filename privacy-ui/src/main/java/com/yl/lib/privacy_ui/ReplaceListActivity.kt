package com.yl.lib.privacy_ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.yl.lib.sentry.hook.PrivacySentry
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
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
            var file = File(filePath + File.separator + "CONFIG_JSON_NAME")
            if (file.exists()) {
                val data = convertStreamToByte(FileInputStream(file))
                if (data != null) {
                    return String(data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertStreamToByte(inputStream: InputStream?): ByteArray? {
        if (inputStream == null) {
            return null
        }
        var bos: ByteArrayOutputStream? = null
        try {
            bos = ByteArrayOutputStream()
            val buffer = ByteArray(2 * 1024)
            var read = -1
            while (inputStream.read(buffer)?.also { read = it } != -1) {
                bos.write(buffer, 0, read)
            }
            return bos.toByteArray()
        } catch (e: java.lang.Exception) {
            Log.e("error:", e.toString())
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e2: java.lang.Exception) {
                }
            }
        }
        return null
    }

    private fun convertStreamToString(inputStream: InputStream?): String? {
        var result: String? = ""
        val data: ByteArray? = convertStreamToByte(inputStream)
        if (data != null) {
            result = String(data)
        }
        return result
    }
}