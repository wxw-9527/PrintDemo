package com.print.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.StringRes
import androidx.core.os.postDelayed
import com.print.demo.databinding.MainActivityBinding
import com.printer.sdk.utils.Utils
import kotlin.concurrent.thread

class MainActivity : BaseActivity<MainActivityBinding>(), OnClickListener {

    companion object {
        // 图片列表
        private val DRAWABLE_ARRAY = arrayOf(R.drawable.test1, R.drawable.test2)
    }

    // 打印机实例
    private lateinit var mPrinter: HcctgPrinter

    // 当前打印下标
    private var mIndex = 0

    override fun onCreateViewBinding(layoutInflater: LayoutInflater): MainActivityBinding {
        return MainActivityBinding.inflate(layoutInflater)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        // 初始化
        mPrinter = HcctgPrinter.getInstance()
        // 绑定监听事件
        binding.btnPrint.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        // 刷新按钮状态
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (R.id.action_disconnect == id) {
            onDisconnectClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // 断开连接
    private fun onDisconnectClick() {
        // 断开连接
        mPrinter.disconnect()
        // 刷新按钮状态
        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_disconnect)?.isVisible = mPrinter.isConnected()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnPrint.id -> onPrintClick()
        }
    }

    //
    private fun onPrintClick() {
        if (mPrinter.isConnected() && mPrinter.isStatusNormal()) {
            // 展示进度条
            showProgress(R.string.print__printing)
            // 重置页码
            mIndex = 0
            thread {
                for (id in DRAWABLE_ARRAY) {
                    // 打印机状态异常
                    if (!mPrinter.isStatusNormal()) {
                        showWarningTipAndDismissProgress(R.string.print__printer_exception)
                        break
                    }
                    // 获取打印结果
                    val originalBitmap = BitmapFactory.decodeResource(resources, id)
                    val zoomedBitmap = Utils.zoomImage(originalBitmap, 362.0, 0)
                    val binationalBitmap = PrintUtil.getBinationalBitmap(zoomedBitmap) // 二值化
                    val isPrintSuccessful = mPrinter.print(binationalBitmap)
                    // 打印成功
                    if (isPrintSuccessful) {
                        // 变更下标
                        mIndex += 1
                        // 判断是否打印完全部条码
                        if (mIndex >= DRAWABLE_ARRAY.size) {
                            // 展示提示信息
                            showSuccessTipAndDismissProgress(R.string.print__print_successful)
                        }
                    }
                }
            }
        }
        // 连接打印机
        else {
            ConnectPortablePrinterActivity.start(this)
        }
    }

    /**
     * 展示警告提示并关闭进度条
     */
    private fun showWarningTipAndDismissProgress(@StringRes id: Int) {
        runOnUiThread {
            dismissProgress()
            showWarningTip(id)
        }
    }

    /**
     * 展示成功提示并关闭进度条
     */
    private fun showSuccessTipAndDismissProgress(@StringRes id: Int) {
        runOnUiThread {
            dismissProgress()
            showSuccessTip(id)
        }
    }
}