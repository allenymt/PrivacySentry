package com.yl.lib.plugin.sentry.upload

import com.yl.lib.plugin.sentry.extension.PrivacyExtension
import groovy.lang.Closure
import org.apache.http.HttpEntity
import org.apache.http.HttpStatus
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.gradle.api.*
import org.gradle.api.logging.Logger
import org.gradle.api.logging.LoggingManager
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.*
import java.io.*
import java.time.Duration

/**
 * @author yulun
 * @since 2022-06-28 16:57
 */
open class HoutuFileUploadTask : DefaultTask() {

    override fun getName(): String {
        return "HoutuFileUploadTask"
    }

    override fun doFirst(action: Action<in Task>): Task {
        project.logger.info("HoutuFileUploadTask doUpload")
        return super.doFirst(action)
    }

    @TaskAction
    fun doUpload(){
        project.logger.info("HoutuFileUploadTask doUpload")
        var privacyExtension = project.extensions.findByType(
            PrivacyExtension::class.java
        ) as PrivacyExtension
        project.logger.info("doUpload privacyExtension is $privacyExtension")
        privacyExtension.replaceFileName?.let {
            uploadFile(privacyExtension.replaceFileName!!,project.buildDir.absolutePath,project)
        }
    }

    private fun uploadFile(fileName: String, filePath: String, project: Project): String? {
        project.logger.info("doUpload uploadFile is $fileName filePath is  $filePath")
        var result = ""
        val httpClient: CloseableHttpClient =
            HttpClients.createDefault()
        var response: CloseableHttpResponse? = null
        var content: InputStream? = null
        var br: BufferedReader? = null
        try {
            val httpPost: HttpPost =
                HttpPost("http://houtu.vdian.net/file/uploadWithoutLogin")
            //HttpMultipartMode.RFC6532参数的设定是为避免文件名为中文时乱码
            val builder: MultipartEntityBuilder =
                MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.RFC6532)
            httpPost.addHeader("scope", "privacy");//头部放文件上传的head可自定义
            val file: File = File(filePath + File.separator + fileName) //待上传文件
            builder.addBinaryBody(
                "file",
                file,
                ContentType.MULTIPART_FORM_DATA,
                fileName
            )
            val entity: HttpEntity = builder.build()
            httpPost.entity = entity
            response = httpClient.execute(httpPost) // 执行提交
            val responseEntity: HttpEntity =
                response.entity //接收调用外部接口返回的内容
            if (response.statusLine
                    .statusCode == HttpStatus.SC_OK
            ) {
                // 返回的内容都在content中
                content = responseEntity.content
                // 定义BufferedReader输入流来读取URL的响应
                br = BufferedReader(InputStreamReader(content))
                var line: String? = ""
                while ( br.readLine()?.also { line = it } != null) {
                    result += line
                }
                project.logger.info("houtu文件上传测试 上传文件replace.json返回参数==>$result")
            }
        } catch (e: Exception) {
            project.logger.info("houtu文件上传测试 上传文件失败：", e)
        } finally { //处理结束后关闭httpclient的链接
            try {
                httpClient.close()
                content?.close()
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }
}