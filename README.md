# PrivacySentry
    android隐私合规检测

## 更新日志
    2021-12-26
        1. Asm修改字节码，hook敏感函数


## TODO
1. 反射调用敏感函数的情况是否需要考虑？
2. 有其他问题欢迎提issue

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
	         classpath 'com.github.allenymt.PrivacySentry:plugin-sentry:1.0.0'
	     }
	}
```



```
    2. 在项目中的build.gralde下添加
        // 在主项目里添加插件依赖
        apply plugin: 'privacy-sentry-plugin'
        
        dependencies {
            // aar依赖
            def privacyVersion = "1.0.0"
            implementation "com.github.allenymt.PrivacySentry:hook-sentry:$privacyVersion"
            implementation "com.github.allenymt.PrivacySentry:base:$privacyVersion"
            implementation "com.github.allenymt.PrivacySentry:plugin-proxy:$privacyVersion"
        }
        
        // 黑名单配置，可以设置这部分包名不会被修改字节码
        privacy {
            blackList = []
        }

```

```
    初始化方法最好在attachBaseContext中第一个调用！！！
```

```
    简易版初始化
    在代码中调用，越早越好，建议在application中调用
    // runtime-hook初始化
    kotlin:PrivacySentry.Privacy.init(this)
    java:PrivacySentry.Privacy.INSTANCE.init(this);
    
    // transform初始化，需要搭配privacy-sentry-plugin使用
    kotlin:PrivacySentry.Privacy.initTransform(this)
    java:PrivacySentry.Privacy.INSTANCE.initTransform(this);
```


```
    完成功能的初始化
     // 完整版配置
        var builder = PrivacySentryBuilder()
            // 自定义文件结果的输出名
            .configResultFileName("demo_test")
            .configPrivacyType(PrivacySentryBuilder.PrivacyType.TRANSFORM)
            // TRANSFORM or RUNTIME
            //.configPrivacyType(PrivacySentryBuilder.PrivacyType.RUNTIME)
            //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
            .configWatchTime(5 * 60 * 1000)
            // 文件输出后的回调
            .configResultCallBack(object : PrivacyResultCallBack {
                override fun onResultCallBack(filePath: String) {
                    PrivacyLog.i("result file patch is $filePath")
                }
            })
        PrivacySentry.Privacy.init(this, PrivacySentry.Privacy.defaultConfigHookBuilder(builder))
        
        
        java
         // 完整版配置
        PrivacySentryBuilder builder = new PrivacySentryBuilder()
                // 自定义文件结果的输出名
                .configResultFileName("buyer_privacy")
                 .configPrivacyType(PrivacySentryBuilder.PrivacyType.TRANSFORM)
                // TRANSFORM or RUNTIME
                //.configPrivacyType(PrivacySentryBuilder.PrivacyType.RUNTIME)
                //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
                .configWatchTime(30 * 1000)
                // 文件输出后的回调
                .configResultCallBack(new PrivacyResultCallBack() {

                    @Override
                    public void onResultCallBack(@NonNull String s) {

                    }
                });
        PrivacySentry.Privacy.INSTANCE.init(this, PrivacySentry.Privacy.INSTANCE.defaultConfigHookBuilder(builder));
```


```
    在隐私协议确认的时候调用，这一步非常重要！，一定要加
    kotlin:PrivacySentry.Privacy.updatePrivacyShow()
    java:PrivacySentry.Privacy.INSTANCE.updatePrivacyShow();
```


```
    支持多进程，多进程产出的文件名前缀默认增加进程名
```



## 隐私方法调用结果产出
-     默认拦截隐私方法时间为1分钟，支持自定义设置时间。
-     排查结果可参考目录下的demo_result.xls，排查结果支持两个维度查看，第一是结合隐私协议的展示时机和敏感方法的调用时机，第二是统计所有敏感函数的调用次数
-     排查结果可观察日志，结果文件会在 /storage/emulated/0/Android/data/yourPackgeName/cache/xx.xls，需要手动执行下adb pull

## 基本原理
-     二期通过修改字节码，理论上可以hook所有敏感方法
-     一期是运行期基于动态代理hook系统关键函数实现，二期计划是编译期代码插桩实现
-     为什么不用xposed等框架？ 因为想做本地自动化定期排查，第三方hook框架外部依赖性太大
-     为什么不搞基于lint的排查方式？ 工信部对于运行期 敏感函数的调用时机和次数都有限制，代码扫描解决不了这些问题


## 支持的hook函数列表

敏感函数 | 函数说明 | 归属的系统服务
---|---|---
getRunningTasks getRunningAppProcesses | 获取运行中的进程，多进程的APP中一般都有调用 | ActivityManagerService(AMS)
getInstalledPackages queryIntentActivities getLeanbackLaunchIntentForPackage getInstalledPackagesAsUser queryIntentActivitiesAsUser queryIntentActivityOptions | 获取手机上已安装的APP  | PackageManager(PMS)
getSimSerialNumber getDeviceId getSubscriberId getDeviceId | 设备和硬件标识  | TelephonyManager(TMS)
getPrimaryClip getPrimaryClipDescription getText setPrimaryClip setText | 剪贴板内容，Android12开始，读取剪贴板会通知用户，因此这里也加一个 | ClipboardManager(CMS)
getMacAddress getHardwareAddress | mac地址 | WifiInfo NetworkInterface
BluetoothAdapter.getAddress  | 蓝牙地址 | BluetoothAdapter
Settings$Secure.getString   | 系统设置，比如Android_id等 | Settings$Secure





## 结语
    整体代码很简单，有问题可以直接提~
