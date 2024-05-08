# PrivacySentry
    android隐私合规检测工具，可规避应用市场上架合规检测的大部分问题

## 群二维码

加作者个人微信，备注来意PrivacySentry, 进社区群
<img width="290" alt="image" src="https://github.com/allenymt/PrivacySentry/assets/8003195/76f2124e-f58d-4420-ac2d-8d33b1093907">


## 更新日志
    2024-05-08(1.3.5)
        1. 修复读取小米系统oaid，反射的方法代理失败的问题(参考demo里reflexMap的配置)
        2. 修复SHA-256 digest error问题，例如引用bcprov jar

    2023-09-18(1.3.4.2)
        1. 修复Service自启动的尝试
        2. 修复异常情况下，产物替换文件缺失的问题

    2023-09-18(1.3.4)
        1. 修复内存缓存数据转换问题
        2. 增加部分demo
        3. 修复getSimState闪退：https://github.com/allenymt/PrivacySentry/issues/116

    2023-08-22(1.3.3-灰度版本)
        1. 重构plugin部分，引入Boost, 适配Agp和Gradle高版本，支持AGP7.0
        2. 尝试解决小米照明弹自启动的问题

    2023-07-12(1.3.2)
        1. 对于hook的方法，内部不再try catch

    2023-04-18(1.3.1)
        1. 新增wifiinfo.getIPAddress代理
        2. 支持粘性数据，即使sdk初始化时机较晚，api的调用记录也可以写入文件
        3. android系统库不再hook

     2023-04-14(1.2.9)
        1. ip地址只做代理，不再拦截
        2. contentResolver的方法，只做代理，不再拦截
        3. 兼容剪贴板写入空异常
        4. 修复同意隐私协议之前 ，代理的数据没有写入到文件的bug issues/103

    2023-02-21(1.2.8)
        1. 放开package信息读取
        2. 放开bssid ,ssdid的缓存，这个会导致腾讯定位出问题
        3. 修复地理位置缓存问题
        4. 放开wifiEnable缓存，不再走缓存判断

    2023-01-06(1.2.7)
        1. 修复拦截ip地址时，主线程异常问题
        2. 默认关闭debug模式
        3. 优化部分逻辑
        4. 注意尽可能在attachBaseContext里第一个调用，因为attachBaseContext之后才能反射拿到ActivityThread的application,所以如果是在attachBaseContext中，
        

    2022-12-06(1.2.6.1)
        重构缓存模块，修复部分问题

    2022-12-05(1.2.6)
        修复值转换的问题

    2022-12-05(1.2.4)
        1. 修复ClipboardManager.hasPrimaryClip 和 WifiManager.isWifiEnabled拦截失败的问题
        2. 增加注解PrivacyClassBlack，用于标记类不需要拦截

    2022-11-15(1.2.3)
        1. 升级asm至9.1版本
        2. 支持类替换，主要是为了拦截构造函数的入参，比如对File的访问，这个功能还是试验期，增加了开关hookConstructor
           详细的配置方法请参考 privacy-replace这个lib

    2022-11-15(1.2.2)
        1. 放开support androidx目录下的类hook
        2. 支持权限请求hook(requestPermissions) https://github.com/allenymt/PrivacySentry/issues/75
        3. 修复部分多线程引起的数据同步问题
        4. 支持关闭插件的hook功能(感谢runforprogram)
        
    2022-11-02(1.2.1)
        更新的东西有点多，尽量测试和自测
        1. androidId等不能只做内存缓存，还要磁盘缓存 
        2. 传感器信息加入到进程级别缓存
        3. 增加三种缓存，分别是内存缓存，时间单位的磁盘缓存，永久的磁盘缓存
        4. 设备名加入到不可变字段缓存，类似于Android—id一样
        5. 扩展存储api，比如位置信息等，wifi参数等，增加拦截sim卡状态，sim卡操作码
        6. 增加剪切板读取开关，对应到合规库加一个全局开关
        7. 修复SHA-256 digest error问题， https://github.com/allenymt/PrivacySentry/issues/29
        8. 修复问题多线程写入问题：https://github.com/allenymt/PrivacySentry/issues/84


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
        2. 支持业务方自定义配置拦截
    2021-12-26(1.0.0)
        1. Asm修改字节码，hook敏感函数
    2021-12-02(0.0.7)
        1. 支持多进程
        2. 日志加上时间戳，方便阅读
        3. 优化文件分时段写入
        4. pms增加部分hook方法
    


## TODO
1. 有其他问题欢迎提issue
2. 项目里如果有引入高德地图or openInstall，先加黑 blackList = ["com.loc","com.amap.api","io.openinstall.sdk"]
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
	         classpath 'com.github.allenymt.PrivacySentry:plugin-sentry:1.3.4.2'
	     }
	}
	
	allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
```



```
    2. 在项目中的build.gralde下添加
        // 在主项目里添加插件依赖
        apply plugin: 'privacy-sentry-plugin'
        
        dependencies {
            // aar依赖
            def privacyVersion = "1.3.4.2"
            implementation "com.github.allenymt.PrivacySentry:hook-sentry:$privacyVersion"
            implementation "com.github.allenymt.PrivacySentry:privacy-annotation:$privacyVersion"

             // 代理类的库，如果自己没有代理类，那么必须引用这个aar！！
             // 如果不想使用库中本身的代理方法，可以不引入这个aar，但是自己必须实现代理类！！
             // 引入privacy-proxy，也可以自定义类代理方法，优先以业务方定义的为准
            implementation "com.github.allenymt.PrivacySentry:privacy-proxy:$privacyVersion"
            // 1.2.3 新增类替换，主要是为了hook构造函数的参数，按业务方需求自己决定
            implementation "com.github.allenymt.PrivacySentry:privacy-replace:$privacyVersion"
        }
        
        // 黑名单配置，可以设置这部分包名不会被修改字节码
        // 项目里如果有引入高德地图，先加黑 blackList = ["com.loc","com.amap.api"], asm的版本有冲突
        // 如果需要生成静态扫描文件， 默认名是replace.json
       privacy {
            // 设置免hook的名单
            blackList = []
            // 开关PrivacySentry插件功能
            enablePrivacy = true
            // 开启hook反射的方法
            hookReflex = true
            // 开启hook 替换类，目前支持file
            hookConstructor = true
            // 是否开启hook变量，默认为false，建议弃用
            hookField = true
        
        
            // 以下是为了解决小米照明弹自启动问题的尝试, 如果没有自启动的需求，这里关闭即可
            // hook Service的部分代码，修复在MIUI上的自启动问题
            // 部分Service把自己的Priority设置为1000，这里开启代理功能，可以代理成0
            enableReplacePriority = true
            replacePriority = 1
        
            // 支持关闭Service的Export功能，默认为false，注意部分厂商通道之类的push(xiaomi、vivo、huawei等厂商的pushService)，不能关闭
            enableCloseServiceExport = true
            // Export白名单Service, 这里根据厂商的名称设置了白名单
            serviceExportPkgWhiteList = ["xiaomi","vivo","honor","meizu","oppo","Oppo","Hms","huawei","stp","Honor"]
            // 修改Service的onStartCommand 返回值修改为START_NOT_STICKY
            enableHookServiceStartCommand = true
        }

```

```
    初始化方法最好在attachBaseContext中第一个调用！！！(1.3.1开始不需要了，可以晚点初始化，不影响检测结果)
```

```
    完成功能的初始化
    PrivacySentryBuilder builder = new PrivacySentryBuilder()
                        // 自定义文件结果的输出名
                        .configResultFileName("buyer_privacy")
	`		//  debug打开，可以看到logcat的堆栈日志
			.syncDebug(true)
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
    如果在日志中发现check!!! 还未展示隐私协议，Illegal print，说明此时还未同意隐私协议，调用了敏感或者违规的api
    所以在隐私协议确认的时候调用，这一步非常重要！，一定要加，这一步是告知SDK，APP已经同意隐私协议了
    kotlin:PrivacySentry.Privacy.updatePrivacyShow()
    java:PrivacySentry.Privacy.INSTANCE.updatePrivacyShow();
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


```
    如何配置替换一个类
    可以参考源码中PrivacyFile的配置，使用PrivacyClassReplace注解，originClass代表你要替换的类，注意要继承originClass的所有构造函数
    可以配置 hookConstructor = false关闭这个功能
/**
 * @author yulun
 * @since 2022-11-18 15:01
 * 代理File的构造方法，如果是自定义的file类，需要业务方单独配置自行处理
 */
@PrivacyClassReplace(originClass = File.class)
public class PrivacyFile extends File {

    public PrivacyFile(@NonNull String pathname) {
        super(pathname);
        record(pathname);
    }

    public PrivacyFile(@Nullable String parent, @NonNull String child) {
        super(parent, child);
        record(parent + child);
    }

    public PrivacyFile(@Nullable File parent, @NonNull String child) {
        super(parent, child);
        record(parent.getPath() + child);
    }

    public PrivacyFile(@NonNull URI uri) {
        super(uri);
        record(uri.toString());
    }

    private void record(String path) {
        PrivacyProxyUtil.Util.INSTANCE.doFilePrinter("PrivacyFile", "访问文件", "path is " + path, PrivacySentry.Privacy.INSTANCE.getBuilder().isVisitorModel(), false);
    }
}


```


## 隐私方法调用结果产出
-     支持hook调用堆栈至文件，默认的时间为1分钟，支持自定义设置时间。
-     排查结果可参考目录下的demo_result.xls，排查结果支持两个维度查看，第一是结合隐私协议的展示时机和敏感方法的调用时机，第二是统计所有敏感函数的调用次数
-     排查结果可观察日志，结果文件会在 /storage/emulated/0/Android/data/yourPackgeName/files/xx.xls，需要手动执行下adb pull
-     logcat日志查看：TAG名为PrivacyOfficer
  
## 基本原理
-     编译期注解+hook方案，第一个transform收集需要拦截的敏感函数，第二个transform替换敏感函数，运行期收集日志
-     为什么不用xposed等框架？ 因为想做本地自动化定期排查，第三方hook框架外部依赖性太大
-     为什么不搞基于lint的排查方式？ 工信部对于运行期 敏感函数的调用时机和次数都有限制，代码扫描解决不了这些问题


## 支持的hook函数列表

支持hook以下功能函数：

- 支持敏感字段缓存(磁盘缓存、带有时间限制的磁盘缓存、内存缓存)

- hook替换类 (构造函数)

- 当前运行进程和任务

- 系统剪贴板服务

- 读取设备应用列表

- 读取 Android SN(Serial,包括方法和变量)，系统设备号

- 读写联系人、日历、本机号码

- 获取定位、基站信息、wifi信息

- Mac 地址、IP 地址

- 读取 IMEI(DeviceId)、MEID、IMSI、ADID(AndroidID)

- 手机可用传感器,传感器注册，传感器列表

- 权限请求

## 常见的合规字段整理
IMEI、MAC地址、MEID、IMSI、SN、ICCID等设备唯一标识符，Android ID、WiFi（WiFi名称、WiFi MAC地址以及设备扫描到的所有WiFi信息），SIM卡信息（IMSI、SIM卡序列号ICCID、手机号、运营商信息），应用安装列表（设备所有已安装应用的包名和应用名），传感器（传感器列表、加速度传感器、温度传感器等），蓝牙信息（设备蓝牙地址和设备扫描到的蓝牙设备信息），基站定位、GPS（用户地理位置信息），账户（各类应用注册的不同账号信息）、剪切板、IP地址、硬件序列号、SDCard信息（公有目录）


## 结语
    整体代码很简单，有问题可以直接提~ (兄弟们，走过路过请给个star~~~)
