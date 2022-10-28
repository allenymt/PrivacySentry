package com.yl.lib.sentry.hook.watcher

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-12-01 17:32
 * 分段延时触发辅助类
 */
class DelayTimeWatcher {
    private var watchTime: Long? = null//ms
    private var callBack: Runnable? = null
    private val minDelayTime: Long = 60 * 1000 // 一分钟写一次
    private var countDownTimer: CountDownTimer? = null

    constructor(watchTime: Long, callBack: Runnable) {
        this.watchTime = watchTime + minDelayTime
        this.callBack = callBack

        if (Looper.myLooper() != Looper.getMainLooper()) {
            // 子线程
            Handler(Looper.getMainLooper()).post {
                initCountDownTimer()
            }
        } else {
            initCountDownTimer()
        }

    }

    private fun initCountDownTimer() {
        countDownTimer = object : CountDownTimer(watchTime!!, minDelayTime) {
            override fun onTick(millisUntilFinished: Long) {
                PrivacyLog.i("DelayTimeWatcher onTick $millisUntilFinished")
                callBack?.run()
            }

            override fun onFinish() {
                PrivacyLog.i("DelayTimeWatcher onFinish")
                callBack?.run()
            }
        }
    }

    fun start() {
        quit()
        countDownTimer?.start()
    }

    fun quit() {
        countDownTimer?.cancel()
    }


    // 为什么不行？？
//    fun countDownCoroutines(onTick:(Int)->Unit,onFinish:()->Unit,
//                            scope: CoroutineScope = GlobalScope):Job{
//        return flow{
//            for (i in 5 downTo 0){
//                emit(i)
//                delay(1000)
//            }
//        }.flowOn(Dispatchers.Main)
//            .onStart {  }
//            .onCompletion { onFinish.invoke() }
//            .onEach { onTick.invoke(it) }
//            .launchIn(scope)
//
//    }

}