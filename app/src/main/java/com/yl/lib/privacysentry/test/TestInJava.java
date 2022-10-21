package com.yl.lib.privacysentry.test;

import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.PrivacySentryBuilder;

/**
 * @author yulun
 * @sinice 2021-12-26 13:30
 */
class TestInJava {

    public static void testInitJava() {
        // 完整版配置
        PrivacySentryBuilder builder = new PrivacySentryBuilder()
                // 自定义文件结果的输出名
                .configResultFileName("demo_test")
                //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
                .configWatchTime(10 * 60 * 1000);
        // 添加默认结果输出，包含log输出和文件输出
        PrivacySentry.Privacy.INSTANCE.init(null, builder);
    }
}
