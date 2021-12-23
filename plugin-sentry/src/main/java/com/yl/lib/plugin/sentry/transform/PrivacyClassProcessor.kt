package com.yl.lib.plugin.sentry.transform

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author yulun
 * @sinice 2021-12-13 19:22
 * 实际处理者
 */
class PrivacyClassProcessor {

    companion object {

        fun run(`is`: InputStream?, project: Project): ByteArray? {
            val classReader = org.objectweb.asm.ClassReader(`is`)

            // 入参有两个，ClassWriter.COMPUTE_MAXS 和 COMPUTE_FRAMES
            // 简单说 他们的区别是COMPUTE_MAXS的方式会帮助我们重新计算局部变量和操作数的size ， 慢10%
            // COMPUTE_FRAMES既会计算栈size,也会计算StackMapFrame 慢20%
            val classWriter =
                org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS)
            // 定义类访问者
            val classVisitor: ClassVisitor =
                SentryTraceClassAdapter(
                    Opcodes.ASM7, classWriter, project.extensions.findByType(
                        PrivacyExtension::class.java
                    )
                )
            /**
             * ClassReader.SKIP_DEBUG：表示不遍历调试内容，即跳过源文件，源码调试扩展，局部变量表，局部变量类型表和行号表属性，即以下方法既不会被解析也不会被访问（ClassVisitor.visitSource，MethodVisitor.visitLocalVariable，MethodVisitor.visitLineNumber）。使用此标识后，类文件调试信息会被去除，请警记。
             * ClassReader.SKIP_CODE：设置该标识，则代码属性将不会被转换和访问，例如方法体代码不会进行解析和访问。
             * ClassReader.SKIP_FRAMES：设置该标识，表示跳过栈图（StackMap）和栈图表（StackMapTable）属性，即MethodVisitor.visitFrame方法不会被转换和访问。当设置了ClassWriter.COMPUTE_FRAMES时，设置该标识会很有用，因为他避免了访问帧内容（这些内容会被忽略和重新计算，无需访问）。
             * ClassReader.EXPAND_FRAMES：该标识用于设置扩展栈帧图。默认栈图以它们原始格式（V1_6以下使用扩展格式，其他使用压缩格式）被访问。如果设置该标识，栈图则始终以扩展格式进行访问（此标识在ClassReader和ClassWriter中增加了解压/压缩步骤，会大幅度降低性能）。
             * 一般来说都选最全的，即使性能问题也是编译期
             */
            classReader.accept(classVisitor, org.objectweb.asm.ClassReader.EXPAND_FRAMES)
            return classWriter.toByteArray()
        }

        fun processJar(project: Project, file: File, extension: PrivacyExtension) {
            if (file == null || !file.exists() || !file.name.endsWith(".jar")) {
                return
            }
            var tmpFile = File(file.parent + File.separator + "${file.name}_classes_temp.jar")
            // 避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            // 建立输出流，注意是 JarOutput
            var jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))
            // 从当前的file里构建一个JarFile
            var jarFile = JarFile(file)
            // 以下是标准模板代码
            var enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                var jarEntry = enumeration.nextElement()
                // jar里每个元素的名称，对于java来说，就是类名(文件名)
                var entryName = jarEntry.getName()
                // 封装成zipEntry why？
                var zipEntry = ZipEntry(entryName)
                // 针对jarEntry构建输入流
                var inputStream = jarFile.getInputStream(jarEntry)
                if (shouldProcessClass(entryName, extension.blackList)) {
                    project.logger.info("deal with jar file is: $file.absolutePath entryName is $entryName")
                    jarOutputStream.putNextEntry(zipEntry)
                    // 使用 ASM 对 class 文件进行操控
                    jarOutputStream.write(run(inputStream, project))
                } else {
                    project.logger.info("undeal with jar file is: $file.absolutePath entryName is $entryName")
                    // 如果命中黑名单，不做处理，直接输入
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()

            if (file.exists()) {
                file.delete()
            }
            // 要把临时文件重命名成源文件的名称
            tmpFile.renameTo(file)
        }

        fun processDirectory(
            project: Project,
            inputDir: File,
            inputFile: File,
            extension: PrivacyExtension
        ) {
            if (shouldProcessClass(inputFile.name, extension.blackList)) {
                project.logger.info("deal with directory file is:" + inputFile.absolutePath)
                var codeBytes = run(FileInputStream(inputFile), project)
                // 构建输出流，这里是当前目录的原文件；也可以新建个临时文件，写完后再覆盖
                var fileOutputStream = FileOutputStream(
                    "${inputFile.parent}${File.separator}${inputFile.name}"
                )
                fileOutputStream.write(codeBytes)
                fileOutputStream.close()
            }
        }


        private fun shouldProcessClass(entryName: String, blackList: Set<String>?): Boolean {
            val replaceEntryName = entryName.replace("/",".")
            blackList?.forEach{
                if (replaceEntryName.contains(it))
                    return false
            }
            if (!entryName.endsWith(".class")
//                || entryName.contains("$") // kotlin object编译后都是内部类，因此这里要放开
                || entryName.endsWith("R.class")
                || entryName.endsWith("BuildConfig.class")
                || entryName.contains("android/support/")
                || entryName.contains("android/arch/")
                || entryName.contains("android/app/")
                || entryName.contains("android/material")
                || entryName.contains("androidx")
                // 过滤掉库本身
                || entryName.contains("com/yl/lib/sentry/hook")
                || entryName.contains("com/yl/lib/plugin_proxy")
                || entryName.contains("com/yl/lib/sentry/base")
            ) {
//            print("checkClassFile className is $entryName false")
                return false
            }
//        print("checkClassFile className is $entryName true")
            return true
        }
    }
}