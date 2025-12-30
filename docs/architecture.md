# PrivacySentry 架构深度分析

> Android 隐私合规检测工具的完整技术解析

## 目录

- [执行摘要](#执行摘要)
- [第一部分：架构总体设计](#第一部分架构总体设计)
- [第二部分：注解层详细分析](#第二部分注解层详细分析)
- [第三部分：插件层详细分析](#第三部分插件层详细分析)
- [第四部分：Hook 层详细分析](#第四部分hook-层详细分析)
- [第五部分：代理层详细分析](#第五部分代理层详细分析)
- [第六部分：完整数据流分析](#第六部分完整数据流分析)
- [第七部分：关键交互关系](#第七部分关键交互关系)
- [第八部分：高级特性](#第八部分高级特性)
- [第九部分：生成的产物分析](#第九部分生成的产物分析)
- [第十部分：完整工作流程示例](#第十部分完整工作流程示例)
- [第十一部分：架构优势和设计模式](#第十一部分架构优势和设计模式)
- [第十二部分：使用建议和最佳实践](#第十二部分使用建议和最佳实践)

---

## 执行摘要

PrivacySentry 是一套完整的 Android 隐私合规检测解决方案，采用**编译期注解 + 字节码插桩 + 运行时 Hook** 的三层架构设计。总共包含 67 个 Kotlin/Java 文件，通过 ASM 和 Booster 框架在编译期对敏感 API 调用进行拦截和代理，运行时记录完整的隐私合规数据。

### 核心技术栈

- **Gradle Plugin**: 基于 AGP 8.0+
- **字节码操作**: ASM 9.1
- **Transform 框架**: Booster
- **语言**: Kotlin 1.8.10 + Java 8
- **最低支持**: Android API 19

---

## 第一部分：架构总体设计

### 1.1 四层架构模型

```
┌─────────────────────────────────────────────────────────────┐
│  4. 代理层 (privacy-proxy)                                  │
│  - 预置的 API 拦截实现                                      │
│  - 使用注解声明拦截规则                                    │
└─────────────────────────────────────────────────────────────┘
                           ↑
┌─────────────────────────────────────────────────────────────┐
│  3. Hook 层 (hook-sentry)                                   │
│  - 运行时 SDK 初始化和管理                                  │
│  - 缓存机制（内存、磁盘）                                  │
│  - 日志收集和文件输出                                      │
└─────────────────────────────────────────────────────────────┘
                           ↑
┌─────────────────────────────────────────────────────────────┐
│  2. 插件层 (plugin-sentry)                                  │
│  - Gradle 编译期 Transform 执行                             │
│  - ASM 字节码操作和修改                                     │
│  - Booster 框架集成                                        │
└─────────────────────────────────────────────────────────────┘
                           ↑
┌─────────────────────────────────────────────────────────────┐
│  1. 注解层 (privacy-annotation)                             │
│  - 编译期注解定义                                          │
│  - 运行时不可见 (@Retention(CLASS))                        │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 项目模块说明

| 模块 | 路径 | 职责 | 关键类 |
|------|------|------|--------|
| **privacy-annotation** | `/privacy-annotation` | 注解定义 | `@PrivacyMethodProxy`, `@PrivacyClassProxy` |
| **plugin-sentry** | `/plugin-sentry` | Gradle 插件和字节码转换 | `PrivacySentryPlugin`, Transform 类 |
| **hook-sentry** | `/hook-sentry` | 运行时 SDK 和日志收集 | `PrivacySentry`, 缓存管理器 |
| **privacy-proxy** | `/privacy-proxy` | 预置的拦截实现 | `PrivacyProxyCall`, 各种 Proxy 类 |
| **privacy-replace** | `/privacy-replace` | 类替换功能（已废弃） | - |
| **app** | `/app` | 示例应用 | - |

---

## 第二部分：注解层详细分析

### 2.1 核心注解系统

**文件位置**: `privacy-annotation/src/main/java/com/yl/lib/privacy_annotation/`

#### 2.1.1 `@PrivacyMethodProxy` - 方法代理注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyMethodProxy {
    Class originalClass();              // 原始类
    String originalMethod() default ""; // 原始方法名
    int originalOpcode() default MethodInvokeOpcode.INVOKESTATIC;  // 调用方式
    boolean ignoreClass() default false; // 是否忽略类名
}
```

**用途**: 标记需要代理的方法。编译期插件通过该注解收集需要拦截的方法列表。

**关键字段说明**:
- `originalClass`: 要拦截的目标类
- `originalMethod`: 要拦截的目标方法名
- `originalOpcode`: 调用方式（静态/虚实例/接口等）
- `ignoreClass`: 允许只按方法名和签名匹配，忽略类名（用于反射方法代理）

**使用示例**:

```kotlin
@PrivacyMethodProxy(
    originalClass = ActivityManager::class,
    originalMethod = "getRunningTasks",
    originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
)
@JvmStatic
fun getRunningTasks(manager: ActivityManager, maxNum: Int): List<RunningTaskInfo> {
    // 代理实现
}
```

#### 2.1.2 `@PrivacyClassProxy` - 类标记注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyClassProxy { }
```

**用途**: 标记包含代理方法的类。告诉编译器该类需要被解析以提取 `@PrivacyMethodProxy` 标注的方法。

#### 2.1.3 `@PrivacyFieldProxy` - 字段代理注解

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyFieldProxy {
    Class originalClass();          // 原始类
    String originalFieldName() default ""; // 原始字段名
}
```

**用途**: 标记需要代理的字段（如 `Build.SERIAL`）。

#### 2.1.4 `@PrivacyClassBlack` - 黑名单注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PrivacyClassBlack { }
```

**用途**: 标记不需要拦截的类，跳过字节码修改。

#### 2.1.5 MethodInvokeOpcode 定义

| Opcode | 值 | 含义 | 使用场景 |
|--------|-----|------|---------|
| `INVOKEVIRTUAL` | 182 | 调用实例方法 | 普通实例方法 |
| `INVOKESPECIAL` | 183 | 调用特殊方法 | 私有方法、构造函数 |
| `INVOKESTATIC` | 184 | 调用静态方法 | 静态方法 |
| `INVOKEINTERFACE` | 185 | 调用接口方法 | 接口实现 |
| `INVOKEDYNAMIC` | 186 | 动态方法调用 | Lambda 表达式 |

---

## 第三部分：插件层详细分析

### 3.1 Gradle 插件入口

**文件**: `plugin-sentry/src/main/java/com/yl/lib/plugin/sentry/PrivacySentryPlugin.kt`

```kotlin
class PrivacySentryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. 创建配置扩展点
        var extension = project.extensions.create("privacy", PrivacyExtension::class.java)

        if (!extension.enablePrivacy) {
            return  // 功能关闭时直接返回
        }

        // 2. 只对 App 插件生效
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            // 3. 清空历史数据
            HookMethodManager.MANAGER.clear()
            HookFieldManager.MANAGER.clear()
            ReplaceClassManager.MANAGER.clear()

            // 4. 注册 Transform
            registerTransform(project)

            // 5. 设置 Manifest 和 Assets 处理任务
            setupTasks(project)
        }
    }
}
```

### 3.2 Transform 执行流程

#### 3.2.1 两阶段 Transform 架构

```
编译期流程：
┌──────────────────────────────────┐
│  输入：所有 .class 文件和 Jar    │
└──────────────┬───────────────────┘
               │
        ┌──────▼─────────────┐
        │  第一阶段（预处理） │
        │  Pre-Transform     │
        └──────┬─────────────┘
               │
    ┌──────────┴──────────────┐
    │                         │
┌───▼────────────────┐   ┌───▼──────────────────┐
│MethodProxy         │   │ ClassProxy           │
│ CollectTransform   │   │ CollectTransform     │
│ (收集方法拦截规则) │   │ (收集类替换规则)    │
└───┬────────────────┘   └───┬──────────────────┘
    │                         │
    │ 结果写入：            │
    │ HookMethodManager     │ ReplaceClassManager
    │ HookFieldManager      │
    │
        ┌──────▼────────────────┐
        │  第二阶段（执行替换）  │
        │  Transform            │
        └──────┬────────────────┘
               │
    ┌──────────┼──────────────┬──────────────┐
    │          │              │              │
┌───▼───┐ ┌───▼───┐ ┌───────▼──┐ ┌────────▼───┐
│Method │ │Field  │ │ Class    │ │ Service    │
│Hook   │ │Proxy  │ │ Proxy    │ │ Hook       │
│       │ │       │ │          │ │            │
└───┬───┘ └───┬───┘ └───────┬──┘ └────────┬───┘
    │         │            │             │
    └─────────┴────────────┴─────────────┘
               │
        ┌──────▼──────────┐
        │ FlushHookData   │
        │ Transform       │
        │ (生成hook配置)  │
        └────────┬────────┘
                 │
    ┌────────────▼──────────────┐
    │ 输出：修改后的 .class 和  │
    │ privacy_hook.json         │
    └───────────────────────────┘
```

### 3.3 第一阶段：收集拦截规则

#### MethodProxyCollectTransform

**职责**: 扫描所有带 `@PrivacyClassProxy` 的类，收集 `@PrivacyMethodProxy` 标注的代理方法

**核心逻辑**:

```kotlin
class MethodProxyCollectTransform : AbsClassTransformer() {
    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        // 1. 过滤只有 @PrivacyClassProxy 注解的类
        klass.invisibleAnnotations?.find {
            it.desc.privacyClassProxy()
        } ?: return klass

        // 2. 收集方法
        klass.methods.filter { methodNode ->
            methodNode.invisibleAnnotations?.find {
                it.desc.privacyMethodProxy()
            } != null
        }.forEach { methodNode ->
            // 3. 提取注解信息
            val hookMethodItem = HookMethodItem(
                proxyClassName = klass.formatName(),
                proxyMethodName = methodNode.name,
                proxyMethodDesc = methodNode.desc
            )

            // 4. 获取目标方法信息
            val annotationNode = methodNode.invisibleAnnotations
                ?.find { it.desc.privacyMethodProxy() }

            hookMethodItem.originClassName =
                annotationNode?.privacyGetValue<Type>("originalClass")
            hookMethodItem.originMethodName =
                annotationNode?.privacyGetValue<String>("originalMethod")
            hookMethodItem.originMethodAccess =
                annotationNode?.privacyGetValue<Int>("originalOpcode")

            // 5. 处理方法描述符
            if (hookMethodItem.originMethodAccess == INVOKESTATIC) {
                // 静态方法：签名相同
                hookMethodItem.originMethodDesc = hookMethodItem.proxyMethodDesc
            } else {
                // 实例方法：需要移除第一个参数（this 对象）
                hookMethodItem.originMethodDesc =
                    hookMethodItem.proxyMethodDesc.replaceFirst(
                        "L${originClassName};", ""
                    )
            }

            // 6. 存储到全局管理器
            HookMethodManager.MANAGER.appendHookMethod(hookMethodItem)
        }

        return klass
    }
}
```

**数据结构**:

```kotlin
data class HookMethodItem(
    val proxyClassName: String,           // 代理类名
    val proxyMethodName: String,          // 代理方法名
    val proxyMethodDesc: String,          // 代理方法签名
    var originClassName: String = "",     // 目标类名
    var originMethodName: String = "",    // 目标方法名
    var originMethodDesc: String = "",    // 目标方法签名
    var originMethodAccess: Int = 0,      // 目标方法调用方式
    var ignoreClass: Boolean = false      // 是否忽略类名
)
```

### 3.4 第二阶段：执行字节码替换

#### MethodHookTransform

**职责**: 遍历所有 class 文件，将原始方法调用替换为代理方法调用

**核心逻辑**:

```kotlin
class MethodHookTransform : BaseHookTransform() {
    override fun transform(
        project: Project,
        privacyExtension: PrivacyExtension,
        context: TransformContext,
        klass: ClassNode
    ): ClassNode {
        klass.methods?.forEach { methodNode ->
            var bLdcHookMethod = false

            // 遍历方法内的所有指令
            methodNode.instructions?.iterator()?.asSequence()?.forEach { node ->
                if (node is MethodInsnNode) {
                    // 1. 查询是否需要拦截该方法
                    val methodItem = HookMethodManager.MANAGER.findHookItemByName(
                        node.name,
                        node.owner,
                        node.desc,
                        node.opcode
                    )

                    if (methodItem != null && shouldHook(...)) {
                        // 2. 记录被替换的方法调用
                        HookedDataManger.MANAGER.addReplaceMethodItem(
                            ReplaceMethodItem(
                                originClassName = node.owner,
                                originMethodName = node.name
                            )
                        )

                        // 3. 修改方法调用指令
                        node.opcode = INVOKESTATIC      // 改为静态调用
                        node.owner = methodItem.proxyClassName
                        node.name = methodItem.proxyMethodName
                        node.desc = methodItem.proxyMethodDesc
                        node.itf = false
                    }
                } else if (node is LdcInsnNode) {
                    // 处理反射调用（需要 hookReflex = true）
                    if (node.cst is String) {
                        bLdcHookMethod = HookMethodManager.MANAGER
                            .findByClsOrMethod(node.cst as String)
                    }
                }
            }
        }
        return klass
    }
}
```

**字节码替换原理**:

原始代码:
```java
ActivityManager manager = ...;
List<RunningTaskInfo> tasks = manager.getRunningTasks(maxNum);
```

原始字节码:
```
ALOAD 1              // 加载 manager 对象
ILOAD 2              // 加载 maxNum 参数
INVOKEVIRTUAL ActivityManager.getRunningTasks
```

替换后的字节码:
```
ALOAD 1              // 加载 manager 对象
ILOAD 2              // 加载 maxNum 参数
INVOKESTATIC PrivacyProxyCall$Proxy.getRunningTasks
```

### 3.5 插件配置

**文件**: `plugin-sentry/src/main/java/com/yl/lib/plugin/sentry/extension/PrivacyExtension.kt`

```gradle
privacy {
    // 黑名单：指定包名不进行字节码修改
    blackList = []

    // 总开关
    enablePrivacy = true

    // 是否 hook 反射方法
    hookReflex = false

    // 反射拦截配置（用于获取小米设备的 OAID 等）
    reflexMap = [
        "com.android.id.impl.IdProviderImpl": ["getOAID", "getAAID", "getVAID"]
    ]

    // 已废弃功能
    hookConstructor = false
    hookField = false
}
```

---

## 第四部分：Hook 层详细分析

### 4.1 运行时 SDK 初始化

**文件**: `hook-sentry/src/main/java/com/yl/lib/sentry/hook/PrivacySentry.kt`

```kotlin
class PrivacySentry {
    object Privacy {
        @Volatile
        private var mBuilder: PrivacySentryBuilder? = null
        private val bInit = AtomicBoolean(false)
        private var bShowPrivacy: AtomicBoolean? = null

        fun init(ctx: Application, builder: PrivacySentryBuilder?) {
            if (bInit.compareAndSet(false, true)) {
                mBuilder = builder
                initInner(ctx)
            }
        }

        private fun initInner(ctx: Application) {
            this.ctx = ctx

            // 启动文件监控
            if (mBuilder?.isEnableFileResult() == true) {
                mBuilder?.getWatchTime()?.let { watchTime ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        stop()  // 超时停止写入
                    }, watchTime)
                }
            }

            // 添加文件输出打印器
            mBuilder?.addPrinter(defaultFilePrinter(ctx, mBuilder))
        }

        fun updatePrivacyShow() {
            // 用户同意隐私协议，需要显式调用
            bShowPrivacy?.compareAndSet(false, true)
            diskCache.put("show_privacy_dialog", "true")
        }

        fun hasShowPrivacy(): Boolean {
            // 检查隐私协议是否已显示
        }

        fun inDangerousState(): Boolean {
            // true: 未同意隐私协议（危险状态）
            // 在危险状态下，返回空数据给调用者
            return !hasShowPrivacy()
        }
    }
}
```

**初始化时机**: 建议在 `Application.attachBaseContext()` 中第一个调用

```kotlin
class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // 必须最先初始化
        PrivacySentry.Privacy.init(
            this,
            PrivacySentryBuilder()
                .enableFileResult(true)
                .syncDebug(true)
                .configWatchTime(30 * 60 * 1000)
        )
    }

    override fun onCreate() {
        super.onCreate()
        // ... 其他初始化

        // 隐私协议确认后调用
        showPrivacyDialog {
            PrivacySentry.Privacy.updatePrivacyShow()
        }
    }
}
```

### 4.2 缓存机制详解

#### 4.2.1 三层缓存架构

```
┌───────────────────────────────────────────┐
│ CachePrivacyManager（统一入口）           │
└───────────────┬─────────────────────────────┘
                │
    ┌───────────┼───────────┬───────────┬───────────┐
    │           │           │           │           │
    ▼           ▼           ▼           ▼           ▼
┌─────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ Memory  │ │TimeLess  │ │Permanent │ │TimeLess  │
│ Cache   │ │Memory    │ │ Disk     │ │ Disk     │
│         │ │Cache     │ │ Cache    │ │ Cache    │
└────┬────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘
     │           │            │           │
     └───────────┴────────────┴───────────┘
                │
     ┌──────────▼──────────┐
     │ SharedPreferences   │
     │ (磁盘持久化)       │
     └─────────────────────┘
```

#### 4.2.2 缓存类型和使用场景

| 缓存类型 | 生命周期 | 使用场景 | 实现类 |
|---------|---------|---------|--------|
| **内存缓存** | 进程级别 | IMEI、IMSI、MEID、Serial 等设备标识 | `MemoryCache` |
| **时效内存缓存** | 时间限制 | 位置信息、WiFi 信息 | `TimeLessMemoryCache` |
| **永久磁盘缓存** | 持久化 | Android ID、设备名等不可变字段 | `DiskCache` |
| **时效磁盘缓存** | 磁盘+时效 | 需要定期刷新的敏感信息 | `TimeLessDiskCache` |

**API 示例**:

```kotlin
class CachePrivacyManager {
    object Manager {
        // 1. 内存缓存
        fun <T> loadWithMemoryCache(
            key: String,
            defaultValue: T,
            getValue: () -> T
        ): T

        // 2. 时效内存缓存
        fun <T> loadWithTimeMemoryCache(
            key: String,
            duration: Long = 0,
            getValue: () -> T
        ): T

        // 3. 永久磁盘缓存
        fun loadWithDiskCache(
            key: String,
            getValue: () -> String
        ): String

        // 4. 时效磁盘缓存
        fun loadWithTimeDiskCache(
            key: String,
            duration: Long = 30 * MINUTE,
            getValue: () -> String
        ): String
    }
}
```

#### 4.2.3 缓存实现细节

**MemoryCache** - 无竞争条件的内存存储:

```kotlin
class MemoryCache<T> : BasePrivacyCache<T> {
    private var paramMap: ConcurrentHashMap<String, T> = ConcurrentHashMap()

    override fun get(key: String, default: T): Pair<Boolean, T?> {
        return if (paramMap.containsKey(key)) {
            Pair(true, paramMap[key])  // 第一个元素 = 是否命中
        } else {
            Pair(false, null)
        }
    }

    override fun put(key: String, value: T) {
        paramMap[key] = value
    }
}
```

**DiskCache** - 两层缓存减少 I/O:

```kotlin
class DiskCache : BasePrivacyCache<String> {
    // 内存缓存层：避免频繁读 SharedPreferences
    private var paramMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    override fun get(key: String, default: String): Pair<Boolean, String?> {
        // 1. 先查内存缓存
        if (paramMap.containsKey(key)) {
            return Pair(true, paramMap[key])
        }

        // 2. 再查 SharedPreferences
        val cacheResult = CacheUtils.Utils.loadFromSp(key, default)
        if (cacheResult.first) {
            paramMap[key] = cacheResult.second!!  // 缓存到内存
        }
        return cacheResult
    }

    override fun put(key: String, value: String) {
        paramMap[key] = value
        CacheUtils.Utils.saveToSp(key, value)  // 同时写入磁盘
    }
}
```

### 4.3 日志收集和输出

#### 4.3.1 日志数据流

```
运行时敏感 API 调用
        │
        ▼
┌──────────────────────────┐
│ doFilePrinter()          │
│ (PrivacyProxyUtil)       │
└──────────┬───────────────┘
           │
   ┌───────▼──────────┐
   │ 构建 PrivacyFun  │
   │ Bean (调用信息)  │
   └───────┬──────────┘
           │
   ┌───────▼──────────────────┐
   │ 判断 SDK 初始化状态      │
   └───────┬──────────┬───────┘
           │          │
       已初始化   未初始化
           │          │
           │      ┌───▼──────────┐
           │      │ 粘性数据保存 │
           │      │ (粘性队列)   │
           │      └──────────────┘
           │
   ┌───────▼──────────────────┐
   │ PrivacyDataManager       │
   │ 管理数据列表             │
   └───────┬──────────────────┘
           │
   ┌───────▼──────────────────┐
   │ BasePrinter 列表         │
   │ (日志 + 文件输出)        │
   └───────┬──────────────────┘
           │
   ┌───────┴────────┬──────────────┐
   │                │              │
   ▼                ▼              ▼
DefaultLogPrint  DefaultFilePrint  ...
(Logcat输出)    (Excel文件输出)
```

#### 4.3.2 数据模型

```kotlin
class PrivacyFunBean(
    val funAlias: String,           // 函数别名（描述）
    val funName: String,            // 函数名
    val msg: String,                // 调用堆栈
    var count: Int = 1              // 调用次数（用于聚合）
) {
    var appendTime: Long = System.currentTimeMillis()  // 记录时间

    fun buildStackTrace(): String {
        // 构建堆栈信息用于聚合
    }

    fun addSelf() {
        count++  // 计数累加
    }
}
```

---

## 第五部分：代理层详细分析

### 5.1 预置拦截实现

**文件位置**: `privacy-proxy/src/main/java/com/yl/lib/privacy_proxy/`

#### 5.1.1 支持的敏感 API 类别

| 类别 | API 示例 | 拦截方式 | 文件 |
|------|---------|---------|------|
| **任务管理** | `ActivityManager.getRunningTasks()` | 返回空列表/代理 | `PrivacyProxyCall.kt` |
| | `ActivityManager.getRecentTasks()` | 返回空列表/代理 | |
| **剪贴板** | `ClipboardManager.getPrimaryClip()` | 代理访问 | `PrivacyProxyCall.kt` |
| **蓝牙** | `BluetoothAdapter.getAddress()` | 代理访问 | `PrivacyProxyCall.kt` |
| **WiFi** | `WifiManager.getConnectionInfo()` | 代理访问 | `PrivacyProxyCall.kt` |
| | `WifiManager.getScanResults()` | 返回空列表 | |
| **位置** | `LocationManager.getLastKnownLocation()` | 缓存+代理 | `PrivacyProxyCall.kt` |
| **设备标识** | `Build.getSerial()` | 缓存+代理 | `PrivacyProxyCall.kt` |
| | `Settings.Secure.getAndroidId()` | 缓存+代理 | |
| **应用列表** | `PackageManager.getInstalledPackages()` | 返回空列表 | `PrivacyProxyCall.kt` |
| **通话信息** | `TelephonyManager.getDeviceId()` | 缓存+代理 | `PrivacyTelephonyProxy.kt` |
| | `TelephonyManager.getIMSI()` | 缓存+代理 | |
| **内容提供者** | `ContentResolver.query()` | 记录日志 | `PrivacyProxyResolver.kt` |
| **传感器** | `SensorManager.registerListener()` | 记录日志 | `PrivacySensorProxy.kt` |
| **权限** | `requestPermissions()` | 记录日志 | `PrivacyPermissionProxy.kt` |

#### 5.1.2 具体实现示例

```kotlin
@PrivacyClassProxy
object PrivacyProxyCall {
    @PrivacyMethodProxy(
        originalClass = ActivityManager::class,
        originalMethod = "getRunningTasks",
        originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getRunningTasks(
        manager: ActivityManager,
        maxNum: Int
    ): List<ActivityManager.RunningTaskInfo?>? {
        doFilePrinter("getRunningTasks", "当前运行中的任务")

        // 核心逻辑：根据隐私协议状态决定返回值
        if (PrivacySentry.Privacy.inDangerousState()) {
            return emptyList()  // 危险状态返回空
        }

        return manager.getRunningTasks(maxNum)  // 正常状态返回真实数据
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @PrivacyMethodProxy(
        originalClass = android.os.Build::class,
        originalMethod = "getSerial",
        originalOpcode = MethodInvokeOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getSerial(): String? {
        var result = ""
        try {
            doFilePrinter("getSerial", "读取Serial")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return ""
            }
            result = Build.getSerial()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
```

---

## 第六部分：完整数据流分析

### 6.1 从注解定义到字节码替换的完整流程

```
开发阶段
────────────────────────────────
│
1️⃣ 开发者编写代理方法
   ├─ 使用 @PrivacyClassProxy 标注类
   ├─ 使用 @PrivacyMethodProxy 标注方法
   └─ 编译生成 class 文件（带注解元数据）
        │
        ▼
编译期间
────────────────────────────────
│
2️⃣ Gradle 执行 PrivacySentryPlugin
   ├─ 清空历史 Manager
   └─ 注册 Transform 任务
        │
        ▼
3️⃣ 第一阶段：预处理 (Pre-Transform)
   ├─ MethodProxyCollectTransform
   │  ├─ 扫描所有 class 文件
   │  ├─ 找到 @PrivacyClassProxy 的类
   │  ├─ 提取 @PrivacyMethodProxy 注解信息
   │  ├─ 解析代理方法的签名
   │  └─ 存储到 HookMethodManager
   │
   └─ ClassProxyCollectTransform
      ├─ 找到 @PrivacyClassReplace 的类
      └─ 存储到 ReplaceClassManager
        │
        ▼
4️⃣ 第二阶段：执行替换 (Transform)
   ├─ MethodHookTransform
   │  ├─ 遍历应用代码中的所有方法调用
   │  ├─ 在 HookMethodManager 中查询是否需要拦截
   │  ├─ 如果需要，修改字节码：
   │  │  ├─ 修改调用的目标类（从原类改为代理类）
   │  │  ├─ 修改调用的方法（从原方法改为代理方法）
   │  │  ├─ 修改调用方式（改为 INVOKESTATIC）
   │  │  └─ 修改方法签名
   │  └─ 记录到 HookedDataManger
   │
   └─ FlushHookDataTransform
      └─ 生成 privacy_hook.json
        │
        ▼
5️⃣ 输出产物
   ├─ 修改后的 .class 文件
   ├─ 修改后的 .jar/.aar 文件
   └─ privacy_hook.json (hook 配置清单)

运行期间
────────────────────────────────
│
6️⃣ 应用启动
   ├─ Application.attachBaseContext()
   │  └─ PrivacySentry.Privacy.init(...)  ⚠️ 必须最先调用
   │
   └─ Application.onCreate()
      └─ 其他初始化...
        │
        ▼
7️⃣ 用户行为：同意隐私协议
   └─ PrivacySentry.Privacy.updatePrivacyShow()
      ├─ 设置 bShowPrivacy = true
      └─ 写入磁盘缓存
        │
        ▼
8️⃣ 业务代码调用敏感 API
   例如：
   val tasks = activityManager.getRunningTasks(10)

   字节码执行流程：
   ├─ ALOAD (加载对象)
   ├─ ICONST (加载参数)
   ├─ INVOKESTATIC (调用代理方法！)
   │  └─ PrivacyProxyCall$Proxy.getRunningTasks()
   │     ├─ doFilePrinter() - 记录日志
   │     ├─ inDangerousState()? - 检查协议状态
   │     │  ├─ true: 返回 emptyList()
   │     │  └─ false: 调用真实方法
   │     └─ return result
   └─ ASTORE (存储结果)
        │
        ▼
9️⃣ 数据收集和输出
   ├─ PrivacyProxyUtil.doFilePrinter()
   ├─ PrivacyDataManager.addData()
   ├─ BasePrinter.filePrint()
   │  ├─ DefaultLogPrint: logcat 输出
   │  └─ DefaultFilePrint: Excel 文件输出
   └─ 结果保存到
      /storage/emulated/0/Android/data/packageName/files/privacy/
```

---

## 第七部分：关键交互关系

### 7.1 模块间的依赖关系

```
privacy-annotation (底层)
        ↑
        │ 依赖（注解定义）
        │
plugin-sentry ◄──── Booster 框架
        │            ASM 库
        │ 生成
        ▼
privacy_hook.json
        │
        ├─ 记录 hook 的方法列表
        ├─ 记录 hook 的类列表
        └─ 记录 hook 的 Service 列表
        │
        ▼ 加载
    应用 APK
        │
        ├─ 包含修改后的 bytecode
        └─ 包含 privacy_hook.json
        │
        ▼
hook-sentry (运行时)
        │ 依赖
        ├─ privacy-annotation (获取注解常数)
        └─ privacy-proxy (预���实现)
```

---

## 第八部分：高级特性

### 8.1 反射 Hook

支持拦截通过反射方式调用的敏感方法：

```kotlin
// 编译期配置
privacy {
    hookReflex = true
    reflexMap = [
        "com.android.id.impl.IdProviderImpl": [
            "getOAID",   // 小米设备的广告 ID
            "getAAID",
            "getVAID"
        ]
    ]
}
```

### 8.2 多进程支持

```kotlin
// 自动为不同进程添加进程名前缀
fun getResultFileName(): String? {
    return if (isMainProcess()) {
        resultFileName
    } else {
        val processName = getProcessName()
        "${processName}_$resultFileName"
    }
}
```

### 8.3 粘性数据 (Sticky Data)

处理 SDK 初始化晚于敏感 API 调用的场景：

```kotlin
PrivacyProxyUtil.doFilePrinter() {
    if (!PrivacySentry.Privacy.hasInit()) {
        // 保存到粘性队列
        PrivacyDataManager.Manager.addStickData(bean)
    }
}
```

### 8.4 黑名单配置

```gradle
privacy {
    // 指定包名不进行字节码修改
    blackList = [
        "com.loc",              // 高德地图（ASM 冲突）
        "com.amap.api",
        "io.openinstall.sdk"
    ]
}
```

---

## 第九部分：生成的产物分析

### 9.1 privacy_hook.json 结构

```json
{
    "hookServiceList": [
        "com.example.TestService"
    ],

    "replaceMethodMap": {
        "android.app.ActivityManager.getRunningAppProcesses": {
            "count": 3,
            "originMethodList": [
                {
                    "originClassName": "com.example.MainActivity",
                    "originMethodName": "getRunningAppProcesses"
                }
            ]
        }
    }
}
```

### 9.2 Excel 输出文件

**文件位置**: `/storage/emulated/0/Android/data/{packageName}/files/privacy/{processName_}privacy_result.xls`

**Sheet 1：隐私合规明细**

| 调用时间(倒序排序) | 别名 | 函数名 | 调用堆栈 |
|-------|------|--------|--------|
| 2024-12-30 10:25:15 | 当前运行中的任务 | getRunningTasks | com.example.MainActivity.onCreate() ... |
| 2024-12-30 10:25:14 | 读取Serial | getSerial | com.example.utils.Utils.getDeviceId() ... |

**Sheet 2：调用次数统计**

| 别名 | 函数名 | 调用堆栈 | 调用次数 |
|------|--------|--------|---------|
| 当前运行中的任务 | getRunningTasks | com.example.MainActivity.onCreate() | 3 |
| 读取Serial | getSerial | com.example.utils.Utils.getDeviceId() | 5 |

---

## 第十部分：完整工作流程示例

### 示例：拦截 Build.getSerial() 调用

#### 步骤 1：定义代理方法

```kotlin
@PrivacyClassProxy
object PrivacyProxyCall {
    @PrivacyMethodProxy(
        originalClass = android.os.Build::class,
        originalMethod = "getSerial",
        originalOpcode = MethodInvokeOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getSerial(): String? {
        doFilePrinter("getSerial", "读取Serial")

        if (PrivacySentry.Privacy.inDangerousState()) {
            return ""  // 危险状态返回空字符串
        }

        return Build.getSerial()
    }
}
```

#### 步骤 2：编译期收集

`MethodProxyCollectTransform` 扫描并收集：

```
找到: @PrivacyClassProxy 修饰的类
  └─ 提取所有 @PrivacyMethodProxy 方法
     └─ 存储到: HookMethodManager.MANAGER
```

#### 步骤 3：编译期替换

`MethodHookTransform` 修改字节码：

原始字节码:
```
INVOKESTATIC Build.getSerial ()Ljava/lang/String;
```

替换后:
```
INVOKESTATIC PrivacyProxyCall$Proxy.getSerial ()Ljava/lang/String;
```

#### 步骤 4：运行时执行

```kotlin
// 1. SDK 初始化
PrivacySentry.Privacy.init(application, builder)

// 2. 用户同意隐私协议
PrivacySentry.Privacy.updatePrivacyShow()

// 3. 业务代码调用
val serial = Build.getSerial()  // 实际调用代理方法
```

---

## 第十一部分：架构优势和设计模式

### 11.1 采用的设计模式

| 模式 | 位置 | 用途 |
|------|------|------|
| **代理模式** | privacy-proxy | 代理敏感 API 调用 |
| **工厂模式** | CachePrivacyManager | 创建不同类型的缓存 |
| **单例模式** | Manager 对象 | 全局唯一的管理器 |
| **观察者模式** | PrivacyDataManager | LiveData 通知数据变化 |
| **策略模式** | BasePrinter | 不同的输出策略 |
| **装饰模式** | Booster Transformer | 链式处理字节码 |
| **模板方法模式** | AbsClassTransformer | Transform 框架模板 |

### 11.2 关键架构优势

1. **编译期处理 vs 运行时 Hook**
   - 相比 Xposed，不需要 Root
   - 相比 Lint 扫描，能获取运行时的调用时机和次数
   - 零运行时 overhead（字节码已修改）

2. **完整的隐私合规方案**
   - 识别：通过注解和字节码扫描
   - 拦截：字节码替换
   - 记录：完整的堆栈和时间戳
   - 报告：Excel 统计

3. **灵活的扩展性**
   - 支持自定义代理实现
   - 支持黑名单配置
   - 支持反射方法拦截

4. **三层缓存的高效性**
   - 内存缓存：最快
   - 时效缓存：防止频繁读取
   - 磁盘缓存：程序重启保留

---

## 第十二部分：使用建议和最佳实践

### 12.1 集成步骤

**1. 在根 build.gradle 中添加插件依赖**

```gradle
buildscript {
    dependencies {
        classpath 'com.github.allenymt.PrivacySentry:plugin-sentry:1.3.7_v820_beta4'
    }
}

allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**2. 在 App build.gradle 中应用插件和库**

```gradle
apply plugin: 'privacy-sentry-plugin'

dependencies {
    implementation "com.github.allenymt.PrivacySentry:hook-sentry:1.3.7_v820_beta4"
    implementation "com.github.allenymt.PrivacySentry:privacy-annotation:1.3.7_v820_beta4"
    implementation "com.github.allenymt.PrivacySentry:privacy-proxy:1.3.7_v820_beta4"
}

privacy {
    enablePrivacy = true
    blackList = []
}
```

**3. 在 Application 中初始化 SDK**

```kotlin
class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        PrivacySentry.Privacy.init(
            this,
            PrivacySentryBuilder()
                .syncDebug(true)      // 开启 logcat
                .enableFileResult(true)  // 开启文件输出
                .configWatchTime(30 * 60 * 1000)
        )
    }
}
```

**4. 用户同意隐私协议后调用**

```kotlin
showPrivacyDialog {
    PrivacySentry.Privacy.updatePrivacyShow()
}
```

### 12.2 调试技巧

**1. 查看 Logcat 日志**

```bash
adb logcat | grep "PrivacyOfficer"
```

**2. 拉取生成的 Excel 文件**

```bash
adb pull /storage/emulated/0/Android/data/{packageName}/files/privacy/
```

**3. 查看静态扫描报告**

```bash
cat privacy_hook.json | jq .replaceMethodMap | less
```

### 12.3 性能考量

- **编译时间增加**: 新增两个 Transform 阶段，但通常 < 2 秒
- **APK 包体积**: 增加 ~200KB（主要是 privacy-proxy 代码）
- **运行时性能**: 零额外开销（字节码已修改）
- **内存占用**: 缓存数据占用，可通过 watchTime 定期清理

### 12.4 常见问题

**Q: 为什么某些敏感 API 调用没有被拦截？**

A: 检查以下几点：
1. 是否添加了代理实现
2. 检查黑名单配置
3. 检查是否同意了隐私协议
4. 查看 privacy_hook.json 是否包含该 API

**Q: 为什么应用闪退？**

A: 可能原因：
1. privacy-proxy 中的代��实现有 bug
2. ASM 库版本冲突（特别是高德地图）
3. 尝试添加到黑名单

**Q: 如何自定义拦截？**

A: 创建新的 `@PrivacyClassProxy` 类，按照 privacy-proxy 的格式实现。

---

## 总结

PrivacySentry 是一套**设计精妙的隐私合规解决方案**，通过以下特点提供完整的隐私检测能力：

✅ **四层架构** - 从注解定义到运行时拦截的完整链路
✅ **两阶段 Transform** - 先收集再替换，模块化清晰
✅ **智能缓存** - 多种缓存策略满足不同场景
✅ **完整日志** - Excel 报表化的统计分析
✅ **隐私协议状态管理** - 根据用户同意状态动态返回数据
✅ **多进程支持** - 自动处理多进程的数据隔离

**关键数据流**：注解 → 编译期收集 → 字节码替换 → 运行时拦截 → 日志收集 → Excel 输出

---

**文档版本**: v1.0
**最后更新**: 2024-12-30
**项目版本**: PrivacySentry 1.3.7_v820_beta4
