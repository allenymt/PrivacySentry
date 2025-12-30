# PrivacySentry

[![](https://jitpack.io/v/allenymt/PrivacySentry.svg)](https://jitpack.io/#allenymt/PrivacySentry)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)

> Android 隐私合规检测工具，可规避应用市场上架合规检测的大部分问题

## ✨ 核心特性

- ✅ **编译期字节码插桩**：基于 ASM + Booster 框架，零运行时性能损耗
- 🎯 **完整的拦截方案**：支持 60+ 敏感 API 拦截，覆盖工信部合规要求
- 📊 **自动化检测报告**：生成 Excel 格式的详细调用记录和统计分析
- 🔄 **智能缓存机制**：内存/磁盘多级缓存，优化性能
- 🌐 **多进程支持**：自动处理多进程场景，独立输出日志
- 🔧 **灵活可扩展**：支持自定义拦截规则，黑名单配置

## 📚 文档导航

- **[快速开始](#快速开始)** - 5 分钟集成使用
- **[插件配置详解](#插件配置详解)** - 完整配置说明
- **[SDK 初始化](#sdk-初始化)** - 运行时配置
- **[自定义拦截](#自定义拦截)** - 扩展拦截规则
- **[架构文档](./docs/architecture.md)** - 完整技术架构解析
- **[开发指南](./CLAUDE.md)** - Claude Code 开发指南

## 📱 社区支持

加作者个人微信，备注来意 PrivacySentry，进社区群

<img width="290" alt="image" src="https://github.com/allenymt/PrivacySentry/assets/8003195/76f2124e-f58d-4420-ac2d-8d33b1093907">

## 🚀 快速开始

### 版本要求

| 组件 | 版本要求 |
|------|---------|
| **AGP** | 8.0+ (推荐 8.2.0) |
| **Gradle** | 8.0+ |
| **Kotlin** | 1.8.10+ |
| **minSdk** | 19+ |
| **compileSdk** | 34+ |

> **注意**：AGP 8.0 以下版本请使用 `1.3.6` 版本

### Step 1: 添加插件依赖

在项目根目录的 `build.gradle` 中添加：

```gradle
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
        mavenCentral()
        google()
    }

    dependencies {
        classpath 'com.github.allenymt.PrivacySentry:plugin-sentry:1.3.7_v820_beta4'
    }
}

allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        mavenCentral()
        google()
    }
}
```

### Step 2: 应用插件和依赖

在 app 模块的 `build.gradle` 中：

```gradle
// 应用插件
apply plugin: 'privacy-sentry-plugin'

dependencies {
    def privacyVersion = "1.3.7_v820_beta4"

    // 核心库（必须）
    implementation "com.github.allenymt.PrivacySentry:hook-sentry:$privacyVersion"
    implementation "com.github.allenymt.PrivacySentry:privacy-annotation:$privacyVersion"

    // 预置拦截实现（强烈推荐）
    implementation "com.github.allenymt.PrivacySentry:privacy-proxy:$privacyVersion"

    // 类替换功能（已废弃，不推荐使用）
    // implementation "com.github.allenymt.PrivacySentry:privacy-replace:$privacyVersion"
}
```

### Step 3: 插件配置

在 app 模块的 `build.gradle` 中添加 `privacy` 配置块：

```gradle
privacy {
    // ========== 核心配置 ==========

    /**
     * 插件功能总开关
     * 类型：Boolean
     * 默认值：true
     * 说明：控制 PrivacySentry 插件是否生效
     *      - true: 启用插件，执行字节码转换
     *      - false: 禁用插件，相当于未集成
     */
    enablePrivacy = true

    /**
     * 黑名单配置
     * 类型：Set<String>
     * 默认值：null (空列表)
     * 说明：指定不进行字节码修改的包名列表
     *      - 适用场景：
     *        1. 使用了其他 ASM 字节码修改工具的三方库（如高德地图）
     *        2. 已知会导致冲突的 SDK
     *        3. 不需要监控的系统库或三方库
     *      - 注意：blackList 中的包不会被插件修改，也无法拦截其中的敏感 API
     */
    blackList = []

    // 常见黑名单配置示例：
    // blackList = [
    //     "com.loc",              // 高德地图（ASM 版本冲突）
    //     "com.amap.api",         // 高德地图 API
    //     "io.openinstall.sdk",   // OpenInstall SDK
    //     "com.google.android",   // Google 服务（可选）
    //     "androidx"              // AndroidX 库（可选）
    // ]

    /**
     * 静态扫描结果文件名
     * 类型：String
     * 默认值："privacy_hook.json"
     * 说明：记录所有被代理的方法名和类名的文件名
     *      - 文件位置：项目根目录
     *      - 文件格式：JSON
     *      - 内容包含：
     *        1. hookServiceList: 被 hook 的 Service 列表
     *        2. replaceMethodMap: 被替换的方法映射表
     *      - 设置为 null 或空字符串则不生成文件
     */
    replaceFileName = "privacy_hook.json"

    // ========== 反射 Hook 配置（可选）==========

    /**
     * 反射方法 Hook 开关
     * 类型：Boolean
     * 默认值：false
     * 说明：是否拦截通过反射调用的敏感方法
     *      - true: 拦截反射调用（需配合 reflexMap 使用）
     *      - false: 不拦截反射调用
     *      - 适用场景：
     *        1. 三方 SDK 通过反射获取设备信息（如小米 OAID）
     *        2. 极光推送、个推、穿山甲等 SDK 的设备标识获取
     *      - 性能影响：轻微（仅影响 LDC 指令的匹配）
     */
    hookReflex = false

    /**
     * 反射拦截配置映射
     * 类型：Map<String, List<String>>
     * 默认值：null
     * 说明：配置需要拦截的反射调用
     *      - Key: 类的全限定名
     *      - Value: 该类中需要拦截的方法名列表
     *      - 只有 hookReflex = true 时才生效
     *      - 匹配原理：检测字节码中的 LDC 指令加载的字符串常量
     */
    reflexMap = [:]

    // 反射拦截配置示例：
    // reflexMap = [
    //     // 小米设备标识服务
    //     "com.android.id.impl.IdProviderImpl": [
    //         "getOAID",   // 开放匿名设备标识符
    //         "getAAID",   // 应用匿名设备标识符
    //         "getVAID"    // 开发者匿名设备标识符
    //     ],
    //     // 自定义类的反射方法
    //     "com.example.utils.DeviceUtils": [
    //         "getDeviceId",
    //         "getIMEI"
    //     ]
    // ]

    // ========== 已废弃功能（不推荐使用，仅供参考）==========

    /**
     * 字段 Hook 开关（已废弃）
     * 类型：Boolean
     * 默认值：false
     * 废弃原因：几乎没有业务场景，功能不稳定
     * 说明：是否 hook 字段访问（如 Build.SERIAL）
     *      - 不推荐使用，请使用方法 hook 代替
     */
    // hookField = false

    /**
     * 构造函数 Hook 开关（已废弃）
     * 类型：Boolean
     * 默认值：false
     * 废弃原因：实现复杂，稳定性差，已停止维护
     * 说明：是否 hook 构造函数（主要用于拦截 File 构造函数参数）
     *      - 相关模块：privacy-replace
     *      - 不推荐使用
     */
    // hookConstructor = false

    /**
     * Manifest 处理开关（已废弃）
     * 类型：Boolean
     * 默认值：false
     * 废弃原因：功能边界不清晰，已停止维护
     * 说明：是否处理 AndroidManifest.xml 文件
     *      - 主要用于处理 Service 的 Priority 和 Export
     *      - 不推荐使用
     */
    // enableProcessManifest = false

    /**
     * Service Priority 替换开关（已废弃）
     * 类型：Boolean
     * 默认值：false
     * 废弃原因：针对 MIUI 自启动问题的特殊方案，已停止维护
     * 说明：是否替换 Service 的 Priority 值
     *      - 部分 Service 设置 Priority = 1000 导致自启动
     *      - 开启后会将 Priority 替换为 replacePriority 的值
     *      - 不推荐使用
     */
    // enableReplacePriority = false

    /**
     * Service Priority 替换值（已废弃）
     * 类型：Int
     * 默认值：0
     * 说明：替换后的 Priority 值
     *      - 配合 enableReplacePriority 使用
     *      - 不推荐使用
     */
    // replacePriority = 0

    /**
     * Service Export 关闭开关（已废弃）
     * 类型：Boolean
     * 默认值：false
     * 废弃原因：可能影响厂商推送功能，已停止维护
     * 说明：是否关闭 Service 的 Export 功能
     *      - 注意：部分厂商推送（小米、VIVO、华为）的 PushService 不能关闭
     *      - 配合 serviceExportPkgWhiteList 使用
     *      - 不推荐使用
     */
    // enableCloseServiceExport = false

    /**
     * Service Export 白名单（已废弃）
     * 类型：Set<String>
     * 默认值：null
     * 说明：允许保持 Export 的 Service 包名列表
     *      - 配合 enableCloseServiceExport 使用
     *      - 不推荐使用
     */
    // serviceExportPkgWhiteList = []

    /**
     * Service StartCommand Hook 开关（已废弃）
     * 类型：Boolean
     * 默认值：false
     * 废弃原因：针对 MIUI 自启动问题的特殊方案，已停止维护
     * 说明：是否 hook Service 的 startCommand 方法
     *      - 不推荐使用
     */
    // enableHookServiceStartCommand = false
}
```

**配置优先级说明**：
1. **必须配置**：`enablePrivacy`（总开关）
2. **强烈推荐**：`blackList`（避免冲突）
3. **按需配置**：`hookReflex` + `reflexMap`（反射拦截）
4. **可选配置**：`replaceFileName`（静态扫描文件）
5. **不推荐使用**：所有标记为 `@Deprecated` 的配置项

### Step 4: SDK 初始化

在 `Application` 中初始化 SDK：

**Kotlin 示例**:

```kotlin
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // ⚠️ 重要：尽可能在 attachBaseContext 中第一个调用
        // 这样可以确保捕获所有敏感 API 调用
        initPrivacySentry()
    }

    override fun onCreate() {
        super.onCreate()

        // 展示隐私协议弹窗
        showPrivacyDialog {
            // 用户同意后，必须调用此方法
            PrivacySentry.Privacy.updatePrivacyShow()
        }
    }

    private fun initPrivacySentry() {
        val builder = PrivacySentryBuilder()
            // 自定义输出文件名
            .configResultFileName("privacy_result")

            // 开启 debug 模式（可在 logcat 查看日志）
            .syncDebug(BuildConfig.DEBUG)

            // 开启文件输出（⚠️ 线上版本请关闭）
            .enableFileResult(BuildConfig.DEBUG)

            // 监控时长（30 分钟）
            .configWatchTime(30 * 60 * 1000)

            // 文件输出完成回调
            .configResultCallBack(object : PrivacyResultCallBack {
                override fun onResultCallBack(filePath: String) {
                    Log.i("PrivacySentry", "结果文件：$filePath")
                }
            })

        PrivacySentry.Privacy.init(this, builder)
    }
}
```

**Java 示例**:

```java
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // ⚠️ 重要：尽可能在 attachBaseContext 中第一个调用
        initPrivacySentry();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 展示隐私协议弹窗
        showPrivacyDialog(() -> {
            // 用户同意后，必须调用此方法
            PrivacySentry.Privacy.INSTANCE.updatePrivacyShow();
        });
    }

    private void initPrivacySentry() {
        PrivacySentryBuilder builder = new PrivacySentryBuilder()
                .configResultFileName("privacy_result")
                .syncDebug(BuildConfig.DEBUG)
                .enableFileResult(BuildConfig.DEBUG)
                .configWatchTime(30 * 60 * 1000)
                .configResultCallBack(new PrivacyResultCallBack() {
                    @Override
                    public void onResultCallBack(@NonNull String filePath) {
                        Log.i("PrivacySentry", "结果文件：" + filePath);
                    }
                });

        PrivacySentry.Privacy.INSTANCE.init(this, builder);
    }
}
```

### Step 5: 查看检测结果

#### 方式 1：Logcat 日志

```bash
# 实时查看日志
adb logcat | grep "PrivacyOfficer"
```

#### 方式 2：Excel 文件

```bash
# 拉取结果文件
adb pull /storage/emulated/0/Android/data/{your.package.name}/files/privacy/

# 文件名格式
# 主进程：privacy_result.xls
# 子进程：{进程名}_privacy_result.xls
```

**Excel 文件包含两个 Sheet**:

1. **Sheet 1 - 隐私合规明细**：按时间倒序记录所有敏感 API 调用
   - 调用时间
   - 方法别名
   - 函数名
   - 完整调用堆栈

2. **Sheet 2 - 调用次数统计**：按堆栈聚合统计调用次数
   - 方法别名
   - 函数名
   - 调用堆栈
   - 调用次数

## ⚙️ 插件配置详解

### 核心配置项完整说明

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enablePrivacy` | Boolean | `true` | **插件功能总开关**<br>- `true`: 启用插件，执行字节码转换<br>- `false`: 禁用插件 |
| `blackList` | Set\<String\> | `null` | **黑名单配置**<br>指定不进行字节码修改的包名列表<br>- 适用场景：ASM 冲突的三方库<br>- 注意：黑名单中的包无法拦截敏感 API |
| `replaceFileName` | String | `"privacy_hook.json"` | **静态扫描结果文件名**<br>记录所有被代理的方法和类<br>- 文件位置：项目根目录<br>- 文件格式：JSON<br>- 设置为 `null` 则不生成 |
| `hookReflex` | Boolean | `false` | **反射方法 Hook 开关**<br>是否拦截通过反射调用的敏感方法<br>- 需配合 `reflexMap` 使用<br>- 适用：三方 SDK 反射获取设备信息 |
| `reflexMap` | Map\<String, List\<String\>\> | `null` | **反射拦截配置**<br>- Key: 类的全限定名<br>- Value: 需要拦截的方法名列表<br>- 只有 `hookReflex=true` 时生效 |

### 已废弃配置项

| 配置项 | 类型 | 默认值 | 废弃原因 |
|--------|------|--------|---------|
| `hookField` | Boolean | `false` | 几乎没有业务场景，功能不稳定 |
| `hookConstructor` | Boolean | `false` | 实现复杂，稳定性差，已停止维护 |
| `enableProcessManifest` | Boolean | `false` | 功能边界不清晰，已停止维护 |
| `enableReplacePriority` | Boolean | `false` | 针对 MIUI 特殊问题，已停止维护 |
| `replacePriority` | Int | `0` | 配合 `enableReplacePriority` 使用 |
| `enableCloseServiceExport` | Boolean | `false` | 可能影响厂商推送，已停止维护 |
| `serviceExportPkgWhiteList` | Set\<String\> | `null` | 配合 `enableCloseServiceExport` 使用 |
| `enableHookServiceStartCommand` | Boolean | `false` | 针对 MIUI 特殊问题，已停止维护 |

> ⚠️ **重要提示**：所有标记为"已废弃"的配置项均不推荐使用，可能在未来版本中移除。

### 黑名单配置说明

#### 什么情况需要配置黑名单？

1. **ASM 版本冲突**
   - 使用高德地图 SDK（ASM 9.1 vs 其他版本）
   - 使用其他字节码修改工具的三方库

2. **已知冲突的 SDK**
   - OpenInstall SDK
   - 部分混淆工具

3. **不需要监控的库**
   - 系统库（可选）
   - 不涉及隐私的三方库

#### 黑名单工作原理

- 插件在 Transform 阶段会跳过黑名单中的包
- 黑名单采用**前缀匹配**规则
- 例如：`"com.loc"` 会匹配 `com.loc.*` 下的所有类

#### 配置示例

```gradle
privacy {
    blackList = [
        // 高德地图（ASM 版本冲突）
        "com.loc",
        "com.amap.api",

        // OpenInstall SDK
        "io.openinstall.sdk",

        // Google 服务（可选）
        "com.google.android",

        // AndroidX 库（可选）
        "androidx",

        // 自定义不需要监控的包
        "com.example.thirdparty"
    ]
}
```

#### 黑名单注意事项

- ✅ **推荐**：只添加确实冲突的包
- ❌ **不推荐**：盲目添加大量包到黑名单
- ⚠️ **影响**：黑名单中的包无法拦截敏感 API 调用

### 反射 Hook 配置详解

#### 使用场景

反射 Hook 用于拦截通过**反射方式**调用的敏感方法，常见场景：

1. **设备标识获取**
   - 小米设备 OAID/AAID/VAID
   - 华为设备标识
   - OPPO/VIVO 设备标识

2. **三方 SDK**
   - 极光推送（JPush）
   - 个推（GeTui）
   - 穿山甲广告 SDK
   - 友盟统计

3. **自定义反射调用**
   - 项目中通过反射获取的敏感信息

#### 工作原理

```kotlin
// 原始代码（反射调用）
Class.forName("com.android.id.impl.IdProviderImpl")
    .getMethod("getOAID")
    .invoke(obj)

// 字节码层面
LDC "com.android.id.impl.IdProviderImpl"  // ← hookReflex 检测这里
LDC "getOAID"                              // ← reflexMap 匹配方法名
INVOKEVIRTUAL Method.invoke()              // ← 替换为代理方法
```

#### 配置示例

**场景 1：小米设备标识**

```gradle
privacy {
    hookReflex = true

    reflexMap = [
        "com.android.id.impl.IdProviderImpl": [
            "getOAID",   // 开放匿名设备标识符
            "getAAID",   // 应用匿名设备标识符
            "getVAID"    // 开发者匿名设备标识符
        ]
    ]
}
```

**场景 2：多个 SDK 配置**

```gradle
privacy {
    hookReflex = true

    reflexMap = [
        // 小米设备标识
        "com.android.id.impl.IdProviderImpl": [
            "getOAID", "getAAID", "getVAID"
        ],

        // 华为设备标识
        "com.huawei.hms.ads.identifier.AdvertisingIdClient": [
            "getAdvertisingIdInfo"
        ],

        // 自定义工具类
        "com.example.utils.DeviceUtils": [
            "getDeviceId",
            "getIMEI",
            "getAndroidId"
        ]
    ]
}
```

**场景 3：极光推送/个推配置**

```gradle
privacy {
    hookReflex = true

    reflexMap = [
        // 极光推送反射获取设备信息
        "cn.jpush.android.api.JCoreInterface": [
            "getDeviceId",
            "getRegistrationID"
        ],

        // 个推反射获取设备信息
        "com.igexin.sdk.PushManager": [
            "getClientid"
        ]
    ]
}
```

#### 反射 Hook 注意事项

- ✅ **精确匹配**：类名和方法名必须完全匹配
- ✅ **性能影响**：轻微（仅影响 LDC 指令匹配）
- ⚠️ **必须启用**：`hookReflex = true` 才生效
- ⚠️ **无法拦截**：动态生成的类名或方法名

### 静态扫描文件说明

#### replaceFileName 配置

```gradle
privacy {
    // 生成静态扫描文件
    replaceFileName = "privacy_hook.json"

    // 不生成文件
    // replaceFileName = null
}
```

#### 文件内容示例

**文件位置**：项目根目录 `/privacy_hook.json`

```json
{
    "hookServiceList": [
        "com.example.TestService",
        "com.example.BackgroundService"
    ],

    "replaceMethodMap": {
        "android.app.ActivityManager.getRunningTasks": {
            "count": 5,
            "originMethodList": [
                {
                    "originClassName": "com.example.MainActivity",
                    "originMethodName": "checkRunningTasks"
                },
                {
                    "originClassName": "com.example.utils.AppUtils",
                    "originMethodName": "getRunningApps"
                }
            ]
        },
        "android.telephony.TelephonyManager.getDeviceId": {
            "count": 2,
            "originMethodList": [
                {
                    "originClassName": "com.example.DeviceManager",
                    "originMethodName": "getIMEI"
                }
            ]
        }
    }
}
```

#### 文件用途

1. **静态分析**：离线分析敏感 API 调用情况
2. **合规检查**：提供给安全团队审查
3. **调试参考**：确认插件是否正确拦截了目标方法
4. **版本对比**：对比不同版本的 API 调用变化

### 配置优先级建议

| 优先级 | 配置项 | 建议值 | 说明 |
|--------|--------|--------|------|
| ⭐⭐⭐ 必须 | `enablePrivacy` | `true` | 插件总开关 |
| ⭐⭐⭐ 强烈推荐 | `blackList` | 根据实际情况 | 避免 ASM 冲突 |
| ⭐⭐ 推荐 | `replaceFileName` | `"privacy_hook.json"` | 生成静态扫描文件 |
| ⭐ 按需配置 | `hookReflex` | `false` 或 `true` | 根据是否有反射调用 |
| ⭐ 按需配置 | `reflexMap` | `[:]` 或配置 | 配合 hookReflex 使用 |
| ❌ 不推荐 | 所有废弃配置 | - | 已停止维护 |

## 🔧 SDK 初始化

### PrivacySentryBuilder 配置项

| 方法 | 参数类型 | 默认值 | 说明 |
|------|---------|--------|------|
| `configResultFileName(String)` | String | 自动生成 | 自定义输出文件名 |
| `syncDebug(Boolean)` | Boolean | `false` | 开启 debug 日志 |
| `enableFileResult(Boolean)` | Boolean | `true` | 是否输出到文件 |
| `configWatchTime(Long)` | Long (毫秒) | 180000 (3分钟) | 监控时长 |
| `configResultCallBack(PrivacyResultCallBack)` | PrivacyResultCallBack | `null` | 文件输出完成回调 |
| `enableReadClipBoard(Boolean)` | Boolean | `true` | 是否允许读取剪贴板 |

### 最佳实践

#### 1. 初始化时机

```kotlin
class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // ✅ 推荐：第一个调用
        PrivacySentry.Privacy.init(this, builder)

        // ❌ 不推荐：在其他初始化之后
        // MultiDex.install(this)
        // PrivacySentry.Privacy.init(this, builder)
    }
}
```

**原因**：attachBaseContext 之后才能通过反射获取 ActivityThread 的 application，如果初始化太晚，可能无法捕获早期的敏感 API 调用。

#### 2. Debug vs Release 配置

```kotlin
private fun initPrivacySentry() {
    val builder = PrivacySentryBuilder()
        .syncDebug(BuildConfig.DEBUG)           // Debug 开启日志
        .enableFileResult(BuildConfig.DEBUG)    // Release 关闭文件输出
        .configWatchTime(
            if (BuildConfig.DEBUG) 30 * 60 * 1000  // Debug: 30分钟
            else 3 * 60 * 1000                      // Release: 3分钟（谨慎）
        )

    PrivacySentry.Privacy.init(this, builder)
}
```

#### 3. 隐私协议状态管理

```kotlin
override fun onCreate() {
    super.onCreate()

    // 检查是否已同意隐私协议
    if (!hasAgreedPrivacyPolicy()) {
        showPrivacyDialog {
            // 用户同意后保存状态
            savePrivacyAgreement()

            // ⚠️ 必须调用，告知 SDK 用户已同意
            PrivacySentry.Privacy.updatePrivacyShow()
        }
    } else {
        // 已同意，直接告知 SDK
        PrivacySentry.Privacy.updatePrivacyShow()
    }
}
```

**重要提示**：
- ✅ 用户同意隐私协议后，**必须**调用 `updatePrivacyShow()`
- ✅ 调用前的敏感 API 会返回空数据并标记 `check!!!`
- ✅ 调用后的敏感 API 返回真实数据并记录日志

#### 4. 手动停止监控

```kotlin
// 手动停止监控和文件写入
PrivacySentry.Privacy.stop()
```

## 🎯 自定义拦截

### 创建自定义代理类

```kotlin
import androidx.annotation.Keep
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil.Util.doFilePrinter

@Keep
@PrivacyClassProxy
object MyCustomProxy {

    /**
     * 示例 1：拦截实例方法
     */
    @PrivacyMethodProxy(
        originalClass = YourClass::class,
        originalMethod = "getSensitiveData",
        originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getSensitiveData(
        instance: YourClass,  // 第一个参数是实例对象
        param1: String
    ): String {
        // 记录日志
        doFilePrinter("getSensitiveData", "获取敏感数据: $param1")

        // 检查隐私协议状态
        if (PrivacySentry.Privacy.inDangerousState()) {
            return ""  // 未同意返回空
        }

        // 调用原始方法
        return instance.getSensitiveData(param1)
    }

    /**
     * 示例 2：拦截静态方法
     */
    @PrivacyMethodProxy(
        originalClass = YourUtilClass::class,
        originalMethod = "getDeviceId",
        originalOpcode = MethodInvokeOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getDeviceId(): String {
        doFilePrinter("getDeviceId", "获取设备ID")

        if (PrivacySentry.Privacy.inDangerousState()) {
            return ""
        }

        return YourUtilClass.getDeviceId()
    }

    /**
     * 示例 3：拦截接口方法
     */
    @PrivacyMethodProxy(
        originalClass = YourInterface::class,
        originalMethod = "getData",
        originalOpcode = MethodInvokeOpcode.INVOKEINTERFACE
    )
    @JvmStatic
    fun getData(instance: YourInterface): String {
        doFilePrinter("getData", "接口方法调用")
        return instance.getData()
    }
}
```

### Java 自定义代理示例

```java
import androidx.annotation.Keep;
import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.sentry.hook.PrivacySentry;
import static com.yl.lib.sentry.hook.util.PrivacyProxyUtil.Util.doFilePrinter;

@Keep
@PrivacyClassProxy
public class MyCustomProxyJava {

    @PrivacyMethodProxy(
        originalClass = YourClass.class,
        originalMethod = "getSensitiveData",
        originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static String getSensitiveData(YourClass instance, String param) {
        doFilePrinter("getSensitiveData", "获取敏感数据: " + param, false);

        if (PrivacySentry.Privacy.INSTANCE.inDangerousState()) {
            return "";
        }

        return instance.getSensitiveData(param);
    }
}
```

### 方法签名规则

#### 实例方法 (INVOKEVIRTUAL)

```kotlin
// 原始方法
class TargetClass {
    fun method(param1: String, param2: Int): String
}

// 代理方法：第一个参数是实例对象
@JvmStatic
fun method(
    instance: TargetClass,  // ⬅️ 额外的第一个参数
    param1: String,
    param2: Int
): String
```

#### 静态方法 (INVOKESTATIC)

```kotlin
// 原始方法
object TargetClass {
    fun method(param1: String): String
}

// 代理方法：参数完全相同
@JvmStatic
fun method(param1: String): String
```

#### 接口方法 (INVOKEINTERFACE)

```kotlin
// 原始接口
interface TargetInterface {
    fun method(param: String): String
}

// 代理方法：第一个参数是接口实例
@JvmStatic
fun method(
    instance: TargetInterface,  // ⬅️ 接口实例
    param: String
): String
```

## 📋 支持的敏感 API

### 设备标识

- ✅ IMEI / DeviceId (`TelephonyManager.getDeviceId()`)
- ✅ IMSI (`TelephonyManager.getSubscriberId()`)
- ✅ MEID (`TelephonyManager.getMeid()`)
- ✅ Android ID (`Settings.Secure.getAndroidId()`)
- ✅ Serial (`Build.getSerial()`, `Build.SERIAL`)
- ✅ MAC 地址 (`WifiInfo.getMacAddress()`)
- ✅ ICCID (`TelephonyManager.getSimSerialNumber()`)

### 网络信息

- ✅ WiFi 信息 (`WifiManager.getConnectionInfo()`)
- ✅ WiFi 扫描结果 (`WifiManager.getScanResults()`)
- ✅ IP 地址 (`NetworkInterface`, `WifiInfo.getIpAddress()`)
- ✅ DHCP 信息 (`WifiManager.getDhcpInfo()`)

### 位置信息

- ✅ GPS 定位 (`LocationManager.getLastKnownLocation()`)
- ✅ 基站信息 (`TelephonyManager.getAllCellInfo()`)
- ✅ 位置监听 (`LocationManager.requestLocationUpdates()`)

### 应用信息

- ✅ 已安装应用列表 (`PackageManager.getInstalledPackages()`)
- ✅ 运行中任务 (`ActivityManager.getRunningTasks()`)
- ✅ 运行中进程 (`ActivityManager.getRunningAppProcesses()`)
- ✅ 最近任务 (`ActivityManager.getRecentTasks()`)

### 联系人和日历

- ✅ 联系人查询 (`ContentResolver.query()`)
- ✅ 联系人插入 (`ContentResolver.insert()`)
- ✅ 日历事件 (Calendar Provider)

### 传感器

- ✅ 传感器列表 (`SensorManager.getSensorList()`)
- ✅ 传感器注册 (`SensorManager.registerListener()`)

### 其他

- ✅ 剪贴板 (`ClipboardManager.getPrimaryClip()`)
- ✅ 蓝牙 (`BluetoothAdapter.getAddress()`)
- ✅ 权限请求 (`requestPermissions()`)
- ✅ SIM 卡信息 (`TelephonyManager.getSimOperator()`)

> 完整列表请参考 [privacy-proxy](./privacy-proxy) 模块

## 🔍 检测结果说明

### 日志格式

```
[PrivacyOfficer] getDeviceId-线程名: main | 读取IMEI | com.example.MainActivity.onCreate(MainActivity.kt:42)
                                                           ↑ 调用堆栈
```

### 危险状态标记

如果在日志中看到 `check!!!` 标记：

```
check!!! 还未展示隐私协议，Illegal print
```

**说明**：此时还未同意隐私协议，调用了敏感 API

**解决方法**：
1. 检查是否在隐私协议同意后调用了 `updatePrivacyShow()`
2. 优化代码，避免在隐私协议同意前调用敏感 API

## 🛠️ 常见问题

### Q1: 为什么某些 API 没有被拦截？

**可能原因**：
1. 该 API 未在 privacy-proxy 中实现
2. 包名在黑名单中
3. 使用动态加载的代码（热修复、插件化）

**解决方法**：
1. 查看 `privacy_hook.json` 确认是否包含该 API
2. 检查黑名单配置
3. 自定义拦截规则

### Q2: 编译失败或运行时崩溃

**可能原因**：
1. ASM 版本冲突（特别是高德地图）
2. AGP 版本不兼容

**解决方法**：
1. 添加冲突库到黑名单
2. 升级到 AGP 8.0+
3. 检查 Gradle 和 Kotlin 版本

### Q3: 如何在线上环境使用？

**建议配置**：

```kotlin
PrivacySentryBuilder()
    .syncDebug(false)              // 关闭 debug 日志
    .enableFileResult(false)       // 关闭文件输出
    .configWatchTime(3 * 60 * 1000) // 缩短监控时间
```

**注意**：
- ❌ 线上版本**不要**开启 `enableFileResult`，避免隐私数据泄露
- ✅ 可以通过 `configResultCallBack` 上报统计数据

### Q4: 多进程如何处理？

SDK 自动支持多进程，会为不同进程生成独立的日志文件：

```
主进程：privacy_result.xls
子进程：com.example.service_privacy_result.xls
```

### Q5: 性能影响如何？

- **编译时间**：增加 < 2 秒
- **APK 体积**：增加 ~200KB
- **运行时性能**：零额外开销（字节码已修改）
- **内存占用**：缓存数据占用，可通过 watchTime 控制

## 📖 更新日志

### 1.3.7_v820_beta4 (2025-05-18)
- ✅ 支持 AGP 8.0+
- ❌ 不兼容 AGP 8.0 以下版本（请使用 1.3.6）

### 1.3.6 (2024-11-01)
- 修复 `T.(args..):T` 函数 hook 失败的问题

### 1.3.5 (2024-05-08)
- 修复读取小米系统 OAID 反射代理失败的问题
- 修复 SHA-256 digest error 问题

### 1.3.4 (2023-09-18)
- 修复内存缓存数据转换问题
- 修复 `getSimState` 闪退问题

### 1.3.3 (2023-08-22)
- 重构 plugin 部分，引入 Booster
- 适配 AGP 和 Gradle 高版本
- 支持 AGP 7.0+

> 完整更新日志请查看 [CHANGELOG](./CHANGELOG.md)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

- **Bug 报告**：请详细描述问题和复现步骤
- **功能建议**：欢迎提出改进建议
- **Pull Request**：请先创建 Issue 讨论

## 📄 许可证

本项目采用 [MIT License](LICENSE)

## 💰 打赏支持

如果这个项目对你有帮助，欢迎打赏支持！

<img width="290" alt="image" src="https://github.com/user-attachments/assets/4d966c38-e1cb-44cd-bff3-09efed7b16a6">

<img width="290" alt="image" src="https://github.com/user-attachments/assets/1b7c628f-dc72-45b6-8ff6-161a1c90d463">

## 🌟 Star History

如果觉得有用，请给个 Star ⭭！

## 🔗 相关资源

- **架构文档**：[docs/architecture.md](./docs/architecture.md)
- **开发指南**：[CLAUDE.md](./CLAUDE.md)
- **示例项目**：[app](./app)
- **GitHub Issues**：[提交问题](https://github.com/allenymt/PrivacySentry/issues)

---

**注意事项**：

1. ⚠️ 线上版本请关闭 `enableFileResult`
2. ⚠️ 尽可能在 `attachBaseContext` 中第一个调用初始化
3. ⚠️ 用户同意隐私协议后必须调用 `updatePrivacyShow()`
4. ⚠️ 使用高德地图等三方库需配置黑名单
5. ⚠️ 动态加载的代码无法被拦截

---

> 如有问题，请提 [Issue](https://github.com/allenymt/PrivacySentry/issues)
>
> 兄弟们，走过路过请给个 Star ⭭~~~
