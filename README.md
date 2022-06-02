# PrivacySentry
    android隐私合规检测

## TODO
    2022-04-22(1.0.4)
        1. 对imei、imsi、mac、android_id、meid、serial等不可变字段，单进程内只读取一次
        2. 精简堆栈，删除重复部分
        3. 修复Android_id拦截问题
        4. 支持变量hook，主要是Build.SERIAL

## 优势
- 全面高效:  对于业务开发无感知，只需要配置一次即可生效
- 全局监控：包括应用自身和第三方SDK，支持监控和修改敏感函数
- 开发简单:   后续新增监控简单，只需配置一个方法注解即可，不仅仅是敏感函数，支持任意函数的监控
- 可扩展性强：完全自定义和研发，支持输出调用堆栈记录，支持游客模式

## 技术文档
[传送门](http://docs.vdian.net/pages/viewpage.action?pageId=129578422)

## 如何使用

```
   添加插件依赖
    classpath "com.wdian.android.lib:privacy-plugin:0.0.5"
```

```
   在主项目的build.gradle下依赖插件和配置
   apply plugin: 'privacy-sentry-plugin'
   
   privacy {
    // 设置免hook的名单
    blackList = []
   }


   def privacyVersion = "0.0.5"
   implementation ("com.wdian.android.lib:privacy-hook:${privacyVersion}"){
            exclude group: 'androidx.appcompat'
            exclude group: 'androidx.core'
            exclude group: 'com.google.android.material'
        }
    implementation ("com.wdian.android.lib:privacy-annotation:${privacyVersion}"){
            exclude group: 'androidx.appcompat'
            exclude group: 'androidx.core'
            exclude group: 'com.google.android.material'
        }
    implementation ("com.wdian.android.lib:privacy-proxy:${privacyVersion}"){
            exclude group: 'androidx.appcompat'
            exclude group: 'androidx.core'
            exclude group: 'com.google.android.material'
        }
```

```
    初始化方法最好在attachBaseContext中第一个调用！！！
```

```
    简易版初始化
    在代码中调用，越早越好，建议在application中调用
    kotlin:PrivacySentry.Privacy.init(this)
    java:PrivacySentry.Privacy.INSTANCE.init(this);
```


```
    完成功能的初始化
      // 完整版配置
                PrivacySentryBuilder builder = new PrivacySentryBuilder()
                        // 自定义文件结果的输出名
                        .configResultFileName("buyer_privacy")
                        // 配置游客模式
                        .configVisitorModel(BeforeApplicationInitHelper.getInstance(application.getApplicationContext()).isNewUser())
                        // 配置写入文件日志
                        .enableFileResult("true".equals(BuildConfig.enablePrivacyPrintFile))
                        // 持续写入文件30分钟
                        .configWatchTime(30 * 60 * 1000)
                        // 文件输出后的回调
                        .configResultCallBack(new PrivacyResultCallBack() {

                            @Override
                            public void onResultCallBack(@NonNull String s) {

                            }
                        });
                // 添加默认结果输出，包含log输出和文件输出
                PrivacySentry.Privacy.INSTANCE.init(application, builder);
```


```
    在隐私协议确认的时候调用，这一步非常重要！，一定要加
    kotlin:PrivacySentry.Privacy.updatePrivacyShow()
    java:PrivacySentry.Privacy.INSTANCE.updatePrivacyShow();
```


```
    关闭游客模式
    PrivacySentry.Privacy.INSTANCE.closeVisitorModel();
```


```
    支持自定义配置hook函数
    /**
 * @author yulun
 * @since 2022-01-13 17:57
 * 主要是两个注解PrivacyClassProxy和PrivacyMethodProxy，PrivacyClassProxy代表要解析的类，PrivacyMethodProxy代表要hook的方法配置
 */
@Keep
open class PrivacyProxyResolver {
     
    // kotlin里实际解析的是这个PrivacyProxyCall$Proxy 内部类
    @PrivacyClassProxy
    @Keep
    object Proxy {
 
        // 查询
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = ContentResolver::class,   // hook的方法所在的类名
            originalMethod = "query",   // hook的方法名
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL //hook的方法调用，一般是静态调用和实例调用
        )
        @JvmStatic
        fun query(
            contentResolver: ContentResolver?, //实例调用的方法需要把声明调用对象，我们默认把对象参数放在第一位
            uri: Uri,
            projection: Array<String?>?, selection: String?,
            selectionArgs: Array<String?>?, sortOrder: String?
        ): Cursor? {
            doFilePrinter("query", "查询服务: ${uriToLog(uri)}") // 输入日志到文件
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) { //游客模式开关
                return null
            }
            return contentResolver?.query(uri, projection, selection, selectionArgs, sortOrder)
        }
  
        @RequiresApi(Build.VERSION_CODES.O)
        @PrivacyMethodProxy(
            originalClass = android.os.Build::class,
            originalMethod = "getSerial",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC //静态调用
        )
        @JvmStatic
        fun getSerial(): String? {
            var result = ""
            try {
                doFilePrinter("getSerial", "读取Serial")
                if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
                }
            result = Build.getSerial()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        return result
        }
    }
}

```

```
    支持多进程，多进程产出的文件名前缀默认增加进程名
```



## 隐私方法调用结果产出
-     默认拦截隐私方法时间为1分钟，支持自定义设置时间。
-     排查结果可参考目录下的demo_result.xls，排查结果支持两个维度查看，第一是结合隐私协议的展示时机和敏感方法的调用时机，第二是统计所有敏感函数的调用次数
-     排查结果可观察日志，结果文件会在 /storage/emulated/0/Android/data/yourPackgeName/cache/xx.xls，需要手动执行下adb pull

## 基本原理
-     编译期注解+hook方案，第一个transform收集需要拦截的敏感函数，第二个transform替换敏感函数，运行期收集日志，同时支持游客模式
-     为什么不用xposed等框架？ 因为想做本地自动化定期排查，第三方hook框架外部依赖性太大
-     为什么不搞基于lint的排查方式？ 工信部对于运行期 敏感函数的调用时机和次数都有限制，代码扫描解决不了这些问题


## 结语
    整体代码很简单，有问题可以直接提~
