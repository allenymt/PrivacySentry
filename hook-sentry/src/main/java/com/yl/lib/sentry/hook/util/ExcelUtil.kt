package com.yl.lib.sentry.hook.util

import com.yl.lib.sentry.hook.excel.ExcelBuildDataListener
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


/**
 * @author yulun
 * @sinice 2021-11-19 15:14
 */
class ExcelUtil {
    object instance {
        private var arial14font: WritableFont? = null

        private var arial14format: WritableCellFormat? = null
        private var arial10font: WritableFont? = null
        private var arial10format: WritableCellFormat? = null
        private var arial12font: WritableFont? = null
        private var arial12format: WritableCellFormat? = null
        private const val UTF8_ENCODING = "UTF-8"


        /**
         * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
         */
        private fun format() {
            try {
                arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
                arial14font!!.colour = Colour.LIGHT_BLUE
                arial14format = WritableCellFormat(arial14font)
                arial14format!!.alignment = Alignment.CENTRE
                arial14format!!.setBorder(Border.ALL, BorderLineStyle.THIN)
                arial14format!!.setBackground(Colour.VERY_LIGHT_YELLOW)
                arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
                arial10format = WritableCellFormat(arial10font)
                arial10format!!.alignment = Alignment.CENTRE
                arial10format!!.setBorder(Border.ALL, BorderLineStyle.THIN)
                arial10format!!.setBackground(Colour.GRAY_25)
                arial12font = WritableFont(WritableFont.ARIAL, 10)
                arial12format = WritableCellFormat(arial12font)
                //对齐格式
                arial10format!!.alignment = Alignment.CENTRE
                //设置边框
                arial12format!!.setBorder(Border.ALL, BorderLineStyle.THIN)
            } catch (e: WriteException) {
                e.printStackTrace()
            }
        }


        /**
         * 初始化Excel表格
         *
         * @param filePath  存放excel文件的路径（path/demo.xls）
         * @param sheetName Excel表格的表名
         * @param colName   excel中包含的列名（可以有多个）
         */
        fun initExcel(
            filePath: String,
            sheetName: List<String>,
            colName: List<Array<String>>,
            sheetIndex: List<Int>
        ) {
            format()
            var workbook: WritableWorkbook? = null
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    file.createNewFile()
                }
                workbook = Workbook.createWorkbook(file)
                for (index in sheetName.indices) {
                    //设置表格的名字
                    val sheet = workbook.createSheet(sheetName[index], sheetIndex[index])
                    //创建标题栏
                    sheet.addCell(Label(0, 0, filePath, arial14format) as WritableCell)
                    var currentColName = colName[index]
                    for (col in currentColName.indices) {
                        sheet.addCell(Label(col, 0, currentColName[col], arial10format))
                    }
                    //设置行高
                    sheet.setRowView(0, 340)
                }
                workbook.write()
                PrivacyLog.i("initExcel success")
            } catch (e: Exception) {
                PrivacyLog.i("initExcel fail")
                e.printStackTrace()
            } finally {
                if (workbook != null) {
                    try {
                        workbook.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         *
         */
        fun writeObjListToExcel(
            objList: List<PrivacyFunBean>?,
            fileName: String?,
            sheetIndex: Int,
            buildDataListener: ExcelBuildDataListener
        ) {
            if (objList != null && objList.isNotEmpty()) {
                var writebook: WritableWorkbook? = null
                var `in`: InputStream? = null
                try {
                    val setEncode = WorkbookSettings()
                    setEncode.encoding = UTF8_ENCODING
                    `in` = FileInputStream(File(fileName))
                    val workbook = Workbook.getWorkbook(`in`)
                    writebook = Workbook.createWorkbook(File(fileName), workbook)
                    val sheet = writebook.getSheet(sheetIndex)
                    for (j in objList.indices) {
                        val privacyFunBean = objList[j] as PrivacyFunBean
                        val list = buildDataListener.buildData(sheetIndex, privacyFunBean)
                        for (i in list.indices) {
                            sheet.addCell(Label(i, j + 1, list[i], arial12format))
                            if (list[i]!!.length <= 4) {
                                //设置列宽
                                sheet.setColumnView(i, list[i]!!.length + 8)
                            } else {
                                //设置列宽
                                sheet.setColumnView(i, list[i]!!.length + 5)
                            }
                        }
                        //设置行高
                        sheet.setRowView(j + 1, 350)
                    }
                    writebook.write()
                    workbook.close()
                    PrivacyLog.i("导出Excel success file : $fileName")
                    PrivacyLog.i("可执行  adb pull $fileName")
                } catch (e: Exception) {
                    PrivacyLog.i("导出Excel fail")
                    e.printStackTrace()
                } finally {
                    if (writebook != null) {
                        try {
                            writebook.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (`in` != null) {
                        try {
                            `in`.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}