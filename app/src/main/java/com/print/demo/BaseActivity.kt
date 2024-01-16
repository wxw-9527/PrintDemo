package com.print.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.WaitDialog

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2024/1/15 15:57
 * desc   :
 */
abstract class BaseActivity<VB : ViewBinding>: AppCompatActivity() {

    lateinit var binding: VB
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = onCreateViewBinding(layoutInflater)
        setContentView(binding.root)
        onParseData(intent)
        val bundle = intent?.extras
        if (bundle != null) {
            onParseData(bundle)
        }
        initToolbar()
        onInit(savedInstanceState)
    }

    // 初始化标题栏
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (null != toolbar) {
            setSupportActionBar(toolbar)
            if (hideNavButton()) return
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { onNavigationClick() }
        }
    }

    abstract fun onCreateViewBinding(layoutInflater: LayoutInflater): VB

    protected open fun onParseData(intent: Intent) = Unit

    protected open fun onParseData(bundle: Bundle) = Unit

    protected open fun hideNavButton() = false

    protected open fun onNavigationClick() = onBackPressedDispatcher.onBackPressed()

    protected open fun onInit(savedInstanceState: Bundle?) = Unit

    /**
     * 显示加载进度弹窗
     *
     * @param messageId   进度条消息文本资源 ID
     */
    protected fun showProgress(@StringRes messageId: Int?) {
        val message = messageId?.let { getString(it) }
        showProgress(message)
    }

    /**
     * 显示加载进度弹窗
     *
     * @param message  进度条消息文本
     */
    protected fun showProgress(message: CharSequence? = null) {
        WaitDialog.show(message)
    }

    /**
     * 销毁加载进度弹窗
     */
    protected fun dismissProgress() {
        WaitDialog.dismiss()
    }

    protected fun showSuccessTip(messageId: Int) {
        val message = getString(messageId)
        showSuccessTip(message)
    }

    protected fun showSuccessTip(message: CharSequence?) {
        PopTip.show(message)
            .iconSuccess()
            .autoDismiss(1000L)
    }

    protected fun showWarningTip(messageId: Int) {
        val message = getString(messageId)
        showWarningTip(message)
    }

    protected fun showWarningTip(message: CharSequence?) {
        PopTip.show(message)
            .iconWarning()
            .showLong()
    }

    protected  fun showErrorTip(messageId: Int) {
        val message = getString(messageId)
        showErrorTip(message)
    }

    protected fun showErrorTip(message: CharSequence?) {
        PopTip.show(message)
            .iconError()
            .showLong()
    }
}