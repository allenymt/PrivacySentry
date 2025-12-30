# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

PrivacySentry 是一个 Android 隐私合规检测工具,通过 ASM 字节码插桩技术在编译期和运行期拦截敏感 API 调用,帮助开发者规避应用市场上架时的隐私合规检测问题。

## 核心架构

### 三层架构设计

1. **plugin-sentry (Gradle 插件层)**
   - 基于 AGP 8.0+ 和 Booster 框架的 Gradle 插件
   - 负责在编译期进行字节码转换 (Transform)
   - 两阶段 Transform:
     - 第一阶段: 收集需要拦截的敏感函数 (MethodProxyCollectTransform, ClassProxyCollectTransform)
     - 第二阶段: 替换敏感函数调用 (MethodHookTransform, FieldProxyTransform, ClassProxyTransform)
   - 关键类: `PrivacySentryPlugin.kt`, `PrivacyTransformTask.kt`

2. **privacy-annotation (注解层)**
   - 定义编译期注解用于声明需要拦截的方法和字段
   - 核心注解:
     - `@PrivacyMethodProxy`: 标记需要代理的方法
     - `@PrivacyFieldProxy`: 标记需要代理的字段
     - `@PrivacyClassProxy`: 标记包含代理方法的类
     - `@PrivacyClassBlack`: 标记不需要拦截的黑名单类

3. **hook-sentry (运行时 Hook 层)**
   - 提供运行时的 SDK 初始化和日志收集功能
   - 支持三种缓存策略: 内存缓存 (MemoryCache)、时效性磁盘缓存 (TimeLessDiskCache)、永久磁盘缓存 (DiskCache)
   - 日志输出到文件 (.xls 格式)
   - 关键类: `PrivacySentry.kt`, `PrivacySentryBuilder.kt`

4. **privacy-proxy (预置代理实现)**
   - 提供常用敏感 API 的拦截实现
   - 通过注解声明拦截规则,由插件在编译期自动收集
   - 关键类: `PrivacyProxyCall.kt`, `PrivacyTelephonyProxy.kt`, `PrivacySensorProxy.kt`

### 工作原理

1. **编译期**: 插件扫描所有 `@PrivacyMethodProxy` 注解,收集需要拦截的方法列表
2. **字节码转换**: 将目标方法调用替换为代理方法调用
3. **运行期**: 代理方法执行实际逻辑,并记录调用堆栈和时机到日志文件

## 常用命令

### 构建项目
```bash
./gradlew clean build
```

### 编译调试 (本地依赖模式)
修改 `config.gradle`:
```gradle
build = [
    local_debug: true,
    local_debug_dir : "${rootProject.projectDir}/local"
]
```

### 发布到本地 Maven
```bash
./gradlew publishToMavenLocal
```

### 运行测试应用
```bash
./gradlew :app:installDebug
```

## 项目模块说明

- **app**: 示例应用,演示如何集成和使用 PrivacySentry
- **plugin-sentry**: Gradle 插件,负责字节码转换
- **hook-sentry**: 运行时 SDK 核心库
- **privacy-annotation**: 注解定义模块
- **privacy-proxy**: 预置的敏感 API 拦截实现
- **privacy-replace**: 类替换功能 (已废弃,不再维护)
- **privacy-test**: 测试模块

## 版本兼容性

- **当前版本 (1.3.7_v820_beta4)**: 支持 AGP 8.0+
- **旧版本 (1.3.6)**: 支持 AGP 8.0 以下版本
- **最低 Android SDK**: minSdkVersion 19
- **编译 SDK**: compileSdkVersion 34
- **Kotlin**: 1.8.10

## 关键配置

### privacy 插件配置 (在 app/build.gradle 中)
```gradle
privacy {
    // 黑名单包名,不会被字节码修改
    blackList = []

    // 插件功能总开关
    enablePrivacy = true

    // 是否 hook 反射方法
    hookReflex = false

    // 反射拦截配置 (如小米 OAID)
    reflexMap = ["com.android.id.impl.IdProviderImpl":["getOAID","getAAID","getVAID"]]

    // 已废弃的功能开关
    hookConstructor = false
    hookField = false
}
```

### SDK 初始化示例
```kotlin
val builder = PrivacySentryBuilder()
    .configResultFileName("privacy_result")
    .syncDebug(true)  // 开启 logcat 输出
    .enableFileResult(true)  // 开启文件输出
    .configWatchTime(30 * 60 * 1000)  // 持续监控 30 分钟

PrivacySentry.Privacy.init(application, builder)

// 用户同意隐私协议后必须调用
PrivacySentry.Privacy.updatePrivacyShow()
```

## 添加自定义拦截

1. 创建包含 `@PrivacyClassProxy` 注解的类
2. 在静态方法上添加 `@PrivacyMethodProxy` 注解:
```kotlin
@PrivacyClassProxy
object CustomProxy {
    @PrivacyMethodProxy(
        originalClass = TargetClass::class,
        originalMethod = "methodName",
        originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun proxyMethod(instance: TargetClass, param: String): ReturnType {
        doFilePrinter("methodName", "方法说明")
        // 自定义逻辑
        return instance.methodName(param)
    }
}
```

## 注意事项

1. **黑名单配置**: 如引入高德地图或 OpenInstall,需添加到黑名单避免 ASM 版本冲突:
   ```gradle
   blackList = ["com.loc", "com.amap.api", "io.openinstall.sdk"]
   ```

2. **线上环境**: 务必关闭 `enableFileResult`,避免隐私数据泄露

3. **初始化时机**: 尽可能在 `Application.attachBaseContext()` 中第一个调用

4. **动态加载**: 热修复、插件化加载的代码无法被拦截

5. **支持的敏感 API**: 包括 IMEI、MAC、Android ID、位置信息、剪贴板、传感器、应用列表等,详见 README.md

## 调试技巧

- 查看生成的 hook 配置: `privacy_hook.json`
- 日志 TAG: `PrivacyOfficer`
- 输出文件路径: `/storage/emulated/0/Android/data/{packageName}/files/privacy/`
- 拉取文件: `adb pull /storage/emulated/0/Android/data/{packageName}/files/privacy/`
