package com.yl.lib.sentry.hook.printer

import android.text.TextUtils
import java.io.File
import java.io.FileOutputStream

/**
 * @author yulun
 * @sinice 2021-09-24 15:47
 */
class DefaultFilePrint : BasePrinter {

    var fileName: String = ""

    var strData: StringBuilder = StringBuilder()

    constructor() : super() {

    }

    constructor(fileName: String) : super() {
        this.fileName = fileName
    }

    override fun print(name: String, msg: String) {
        strData.append(name).append("\n").append(msg).append("\n")
        if (strData.length >= 500) {
            flush(str = strData.toString())
        }
        strData.clear()
    }

    private fun flush(str: String) {
        if (TextUtils.isEmpty(fileName))
            return
        var file = File(fileName)
        if (!file.exists()) {
            file.exists()
        }
        saveFile(fileName!!, str)
    }

    private fun saveFile(outputFileName: String, strInput: String) {
        try {
            var file = File(outputFileName)
            var fos = FileOutputStream(file, true)
            fos.write(strInput.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}