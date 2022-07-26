package com.yl.lib.privacy_proxy

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import androidx.annotation.Keep
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil

/**
 * @author yulun
 * @since 2022-06-17 17:56
 */
@Keep
class PrivacySensorProxy {

    @Keep
    @PrivacyClassProxy
    object SensorProxy {

        // 这个方法的注册放在了PrivacyProxyCall2中，提供了一个java注册的例子
        @PrivacyMethodProxy(
            originalClass = SensorManager::class,
            originalMethod = "registerListener",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun registerListener(
            sensorManager: SensorManager?,
            listener: SensorEventListener?, sensor: Sensor?,
            samplingPeriodUs: Int
        ): Boolean {
            logSensorManager(sensor)
            return sensorManager?.registerListener(listener, sensor, samplingPeriodUs) == true
        }

        @PrivacyMethodProxy(
            originalClass = SensorManager::class,
            originalMethod = "registerListener",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun registerListener(
            sensorManager: SensorManager?,
            listener: SensorEventListener?, sensor: Sensor?,
            samplingPeriodUs: Int, maxReportLatencyUs: Int
        ): Boolean {
            logSensorManager(sensor)
            return sensorManager?.registerListener(
                listener,
                sensor,
                samplingPeriodUs,
                maxReportLatencyUs
            ) == true
        }


        @PrivacyMethodProxy(
            originalClass = SensorManager::class,
            originalMethod = "registerListener",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun registerListener(
            sensorManager: SensorManager?,
            listener: SensorEventListener?, sensor: Sensor?,
            samplingPeriodUs: Int, handler: Handler?
        ): Boolean {
            logSensorManager(sensor)
            return sensorManager?.registerListener(
                listener,
                sensor,
                samplingPeriodUs,
                handler
            ) == true
        }

        @PrivacyMethodProxy(
            originalClass = SensorManager::class,
            originalMethod = "registerListener",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun registerListener(
            sensorManager: SensorManager?,
            listener: SensorEventListener?, sensor: Sensor?,
            samplingPeriodUs: Int, maxReportLatencyUs: Int, handler: Handler?
        ): Boolean {
            logSensorManager(sensor)
            return sensorManager?.registerListener(
                listener,
                sensor,
                samplingPeriodUs,
                maxReportLatencyUs,
                handler
            ) == true
        }
        
        @JvmStatic
        private fun logSensorManager(sensor: Sensor?) {
            sensor?.let {
                var sensorType: String? = ""
                var sensorDesc: String? = ""
                when (sensor.type) {
                    // 加速度，摇一摇
                    Sensor.TYPE_ACCELEROMETER -> {
                        sensorType = "加速度"
                        sensorDesc = "常用于摇一摇"
                    }
                    // 磁场。。
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        sensorType = "磁场"
                    }
                    // 方向, 弃用了
                    Sensor.TYPE_ORIENTATION -> {
                        sensorType = "方向"
                    }
                    // 陀螺仪。 用来感应手机的旋转和倾斜
                    Sensor.TYPE_GYROSCOPE -> {
                        sensorType = "陀螺仪"
                        sensorDesc = "用来感应手机正面的光线强弱"
                    }
                    // 光线 用来感应手机正面的光线强弱
                    Sensor.TYPE_LIGHT -> {
                        sensorType = "光线 "
                        sensorDesc = "用来感应手机正面的光线强弱"
                    }
                    // 压力。
                    Sensor.TYPE_PRESSURE -> {
                        sensorType = "压力"
                    }
                    // 距离。
                    Sensor.TYPE_PROXIMITY -> {
                        sensorType = "距离"
                    }
                    // 重力
                    Sensor.TYPE_GRAVITY -> {
                        sensorType = "重力"
                    }
                    // 线性加速度
                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        sensorType = "线性加速度"
                    }
                    // 旋转矢量。
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        sensorType = "旋转矢量"
                    }
                    // 相对湿度
                    Sensor.TYPE_RELATIVE_HUMIDITY -> {
                        sensorType = "相对湿度"
                    }
                    // 环境温度
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        sensorType = "环境温度"
                    }
                    // 无标定磁场
                    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                        sensorType = "无标定磁场"
                    }
                    // 无标定旋转矢量
                    Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                        sensorType = "无标定旋转矢量"
                    }
                    // 未校准陀螺仪
                    Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> {
                        sensorType = "未校准陀螺仪"
                    }
                    // 特殊动作
                    Sensor.TYPE_SIGNIFICANT_MOTION -> {
                        sensorType = "特殊动作"
                    }
                    // 步行检测
                    Sensor.TYPE_STEP_DETECTOR -> {
                        sensorType = "步行检测"
                    }
                    // 步行计数
                    Sensor.TYPE_STEP_COUNTER -> {
                        sensorType = "步行计数"
                    }
                    // 地磁旋转矢量
                    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                        sensorType = "地磁旋转矢量"
                    }
                    // 心跳速率
                    Sensor.TYPE_HEART_RATE -> {
                        sensorType = "心跳速率"
                    }
                }
                PrivacyProxyUtil.Util.doFilePrinter(
                    "registerListener",
                    methodDocumentDesc = "注册-${sensorType}传感器,$sensorDesc"
                )
            }
        }

    }
}