# PrivacySentry
    android隐私合规检测，不仅仅是是检测，碰到第三方SDK不好解决的或者修复周期很长的，我们等不了那么长时间，可以通过这个库去动态拦截
    例如游客模式，这种通过xposed\epic只能做检测，毕竟xposed\epic不能带到线上，但是asm可以
    
## 更新日志
    2022-08-30(1.1.0)
        1. 变量hook支持通过注解配置
        2. 修复不引入privacy-proxy引起的问题
    2022-07-29(1.0.9)
        1. 删除多余的aar引用
    2022-07-26(1.0.8)
        1. 优化log输出，未初始化也能有log输出
        2. 优化初始化方式
    2022-06-24(1.0.7)
        1. 新增hook 传感器方法
        2. 新增静态扫描，支持产出敏感函数hook列表
    2022-06-16(1.0.5)
        1. 修复Settings.System获取Android_id,未拦截到的问题
        2. 支持业务方配置同类型的hook函数覆盖自带的hook函数
        3. 新增MIT开源协议
    2022-04-22(1.0.4)
        1. 对imei、imsi、mac、android_id、meid、serial等不可变字段，单进程内只读取一次
        2. 精简堆栈，删除重复部分
        3. 修复Android_id拦截问题
    2022-03-04(1.0.3)
        支持变量hook，主要是Build.SERIAL
    2022-1-18(1.0.2)
        1. 编译期注解+hook方案
        2. 支持业务方自定义配置拦截，支持游客模式
    2021-12-26(1.0.0)
        1. Asm修改字节码，hook敏感函数
    2021-12-02(0.0.7)
        1. 支持多进程
        2. 日志加上时间戳，方便阅读
        3. 优化文件分时段写入
        4. pms增加部分hook方法
    



## TODO
1. 有其他问题欢迎提issue
2. 项目里如果有引入高德地图or openInstall，先加黑 blackList = ["com.loc","com.amap.api","io.openinstall.sdk"], asm的版本有冲突
3. 动态加载加载的代码无法拦截(热修复，插件化)

## 如何使用

```
    1. 在根目录的build.gralde下添加
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	buildscript {
	     dependencies {
	         // 添加插件依赖
	         classpath 'com.github.allenymt.PrivacySentry:plugin-sentry:1.0.9'
	     }
	}
```



```
    2. 在项目中的build.gralde下添加
        // 在主项目里添加插件依赖
        apply plugin: 'privacy-sentry-plugin'
        
        dependencies {
            // aar依赖
            def privacyVersion = "1.0.9"
            implementation "com.github.allenymt.PrivacySentry:hook-sentry:$privacyVersion"
            implementation "com.github.allenymt.PrivacySentry:privacy-annotation:$privacyVersion"
	    //如果不想使用库中本身的代理方法，可以不引入这个aar，自己实现
            implementation "com.github.allenymt.PrivacySentry:privacy-proxy:$privacyVersion"
        }
        
        // 黑名单配置，可以设置这部分包名不会被修改字节码
        // 项目里如果有引入高德地图，先加黑 blackList = ["com.loc","com.amap.api"], asm的版本有冲突
        // 如果需要生成静态扫描文件， 默认名是replace.json
        privacy {
            blackList = []
            replaceFileName = "replace.json"
        }

```

```
    初始化方法最好在attachBaseContext中第一个调用！！！
```

```
    完成功能的初始化
    PrivacySentryBuilder builder = new PrivacySentryBuilder()
                        // 自定义文件结果的输出名
                        .configResultFileName("buyer_privacy")
                        // 配置游客模式，true打开游客模式，false关闭游客模式
                        .configVisitorModel(false)
                        // 配置写入文件日志 , 线上包这个开关不要打开！！！！，true打开文件输入，false关闭文件输入
                        .enableFileResult(true)
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
-     支持hook调用堆栈至文件，默认的时间为1分钟，支持自定义设置时间。
-     排查结果可参考目录下的demo_result.xls，排查结果支持两个维度查看，第一是结合隐私协议的展示时机和敏感方法的调用时机，第二是统计所有敏感函数的调用次数
-     排查结果可观察日志，结果文件会在 /storage/emulated/0/Android/data/yourPackgeName/cache/xx.xls，需要手动执行下adb pull

## 基本原理
-     编译期注解+hook方案，第一个transform收集需要拦截的敏感函数，第二个transform替换敏感函数，运行期收集日志，同时支持游客模式
-     为什么不用xposed等框架？ 因为想做本地自动化定期排查，第三方hook框架外部依赖性太大
-     为什么不搞基于lint的排查方式？ 工信部对于运行期 敏感函数的调用时机和次数都有限制，代码扫描解决不了这些问题


## 支持的hook函数列表

支持hook以下功能函数：

- 当前运行进程和任务

- 系统剪贴板服务

- 读取设备应用列表

- 读取 Android SN(Serial,包括方法和变量)

- 读写联系人、日历、本机号码

- 获取定位、基站信息、wifi信息

- Mac 地址、IP 地址

- 读取 IMEI(DeviceId)、MEID、IMSI、ADID(AndroidID)

- 手机可用传感器,传感器注册






## 结语
    整体代码很简单，有问题可以直接提~ (兄弟们，走过路过请给个star~~~)
