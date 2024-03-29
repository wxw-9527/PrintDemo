package com.print.demo

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.printer.sdk.PrinterConstants
import com.printer.sdk.PrinterInstance
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2023/10/24 14:41
 * desc   :
 */
class HcctgPrinter : BasePrinter() {

    companion object {

        @Volatile
        private var instance: HcctgPrinter? = null

        /**
         * 当前类单例对象
         */
        fun getInstance(): HcctgPrinter =
            instance ?: synchronized(this) {
                instance ?: HcctgPrinter().also { instance = it }
            }
    }

    //
    private var mDisposable: Disposable? = null

    // 打印机实例
    private lateinit var mPrinterInstance: PrinterInstance

    //
    private lateinit var mBluetoothDevice: BluetoothDevice

    override fun connect(context: Context, device: BluetoothDevice) {
        onConnectListener?.onConnectStart()
        // 初始化全局变量
        mBluetoothDevice = device
        // 获取打印机实例
        mPrinterInstance = PrinterInstance.getPrinterInstance(device, mHandler)
        // 连接打印机
        val observable = Observable.create { emitter ->
            val result = mPrinterInstance.openConnection()
            emitter.onNext(result)
            emitter.onComplete()
        }
        mDisposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    //
    private val mHandler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                // 连接成功
                PrinterConstants.Connect.SUCCESS -> {
                    val status = mPrinterInstance.currentStatus
                    val printerStatusEnum = PrinterStatusEnum.fromStatus(status)
                    if (PrinterStatusEnum.NORMAL == printerStatusEnum) {
                        Timber.d("12、打印机连接成功")
                        onConnectListener?.onConnectSuccessful(mBluetoothDevice)
                    } else {
                        disconnect()
                        onConnectListener?.onConnectFail()
                    }
                }
                // 其他
                else -> {
                    disconnect()
                    onConnectListener?.onConnectFail()
                }
            }
        }
    }

    override fun disconnect() {
        // 断开连接
        if (isConnected()) {
            mPrinterInstance.closeConnection()
        }
        // 释放资源
        mDisposable?.dispose()
    }

    override fun isConnected(): Boolean {
        return ::mPrinterInstance.isInitialized && 0 == mPrinterInstance.currentStatus
    }

    override fun isStatusNormal(): Boolean {
        return 0 == mPrinterInstance.currentStatus
    }

    override fun print(bitmap: Bitmap): Boolean {
        // 设置打印参数
        mPrinterInstance.pageSetup(PrinterConstants.LablePaperType.Size_58mm, 362, 217)
        mPrinterInstance.drawGraphic(224, 0, bitmap)
        mPrinterInstance.print(PrinterConstants.PRotate.Rotate_0, 1)
        // 返回打印结果
        return 0 == mPrinterInstance.getPrintingStatus(12 * 1000)
    }
}