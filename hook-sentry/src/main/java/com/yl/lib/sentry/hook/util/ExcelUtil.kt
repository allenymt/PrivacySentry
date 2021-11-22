package com.yl.lib.sentry.hook.util

import android.content.Context
import android.widget.Toast

import jxl.Workbook

import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour

import jxl.write.WritableWorkbook

import jxl.write.WritableCell

import jxl.write.WriteException

import jxl.write.WritableCellFormat

import jxl.write.WritableFont
import java.lang.Exception

import jxl.write.Label;
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList


/**
 * @author yulun
 * @sinice 2021-11-19 15:14
 */
class ExcelUtil {
    object instance{
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
        fun initExcel(filePath: String, sheetName: String, colName: Array<String>) {
            format()
            var workbook: WritableWorkbook? = null
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    file.createNewFile()
                } else {
                    return
                }
                workbook = Workbook.createWorkbook(file)
                //设置表格的名字
                val sheet = workbook.createSheet(sheetName, 0)
                //创建标题栏
                sheet.addCell(Label(0, 0, filePath, arial14format) as WritableCell)
                for (col in colName.indices) {
                    sheet.addCell(Label(col, 0, colName[col], arial10format))
                }
                //设置行高
                sheet.setRowView(0, 340)
                workbook.write()
            } catch (e: Exception) {
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
         * 将制定类型的List写入Excel中
         *
         * @param objList  待写入的list
         * @param fileName
         * @param c
         * @param <T>
        </T> */
        fun <T> writeObjListToExcel(objList: List<T>?, fileName: String?, c: Context?) {
            if (objList != null && objList.size > 0) {
                var writebook: WritableWorkbook? = null
                var `in`: InputStream? = null
                try {
                    val setEncode = WorkbookSettings()
                    setEncode.encoding = UTF8_ENCODING
                    `in` = FileInputStream(File(fileName))
                    val workbook = Workbook.getWorkbook(`in`)
                    writebook = Workbook.createWorkbook(File(fileName), workbook)
                    val sheet = writebook.getSheet(0)
                    for (j in objList.indices) {
                        val privacyFunBean = objList[j] as PrivacyFunBean
                        val list: MutableList<String?> = ArrayList()
                        list.add(privacyFunBean.funAlias)
                        list.add(privacyFunBean.funName)
                        list.add(privacyFunBean.buildStackTrace())
                        list.add(privacyFunBean.count.toString())
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
                    Toast.makeText(c, "导出Excel成功", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
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