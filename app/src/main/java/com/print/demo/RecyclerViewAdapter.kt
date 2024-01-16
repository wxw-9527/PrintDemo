package com.print.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseQuickAdapter

/**
 * author : Saxxhw
 * email  : xingwangwang@cloudinnov.com
 * time   : 2022/11/17 16:32
 * desc   :
 */

/**
 * ViewHolder类
 */
open class VbHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

/**
 * 基础类型 Adapter（包含点击事件、数据操作、动画、空视图）
 */
abstract class BaseVbAdapter<VB : ViewBinding, T : Any>(items: List<T> = emptyList()) :
    BaseQuickAdapter<T, VbHolder<VB>>(items) {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): VbHolder<VB> {
        val binding = onCreateViewBinding(LayoutInflater.from(parent.context), parent)
        return VbHolder(binding)
    }

    override fun onBindViewHolder(holder: VbHolder<VB>, position: Int, item: T?) {
        if (item != null) {
            onBindView(holder.binding, position, item)
        }
    }

    override fun onBindViewHolder(
        holder: VbHolder<VB>,
        position: Int,
        item: T?,
        payloads: List<Any>,
    ) {
        if (item != null) {
            onBindView(holder.binding, position, item, payloads)
        }
    }

    abstract fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup): VB

    abstract fun onBindView(binding: VB, position: Int, item: T)

    protected open fun onBindView(binding: VB, position: Int, item: T, payloads: List<Any>) {}
}