package com.print.demo

import android.app.Application
import com.tencent.mmkv.MMKV
import timber.log.Timber

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2024/1/15 16:56
 * desc   :
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化MMKV
        initMmkv()
    }

    // 初始化MMKV
    private fun initMmkv() {
        val rootDir = MMKV.initialize(this)
        Timber.d("mmkv root：$rootDir")
    }
}