package com.cmd.flora.view.call

import android.view.LayoutInflater
import android.view.ViewGroup
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.network.response.CallResponse
import com.cmd.flora.databinding.ItemCallHorizontalBinding
import com.cmd.flora.utils.compactDrawable
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible

class CallAdapter(
    private val onCallClick: (CallResponse) -> Unit = {}
) : BaseAdapter<CallView>() {

    override fun createViewHolder(parent: ViewGroup, valueBase: CallView): BaseViewHolder<*> {
        val binding =
            ItemCallHorizontalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = BaseViewHolder(binding)
        holder.binding.layoutPhone.setOnSingleClickListener {
            getData(holder, valueBase::class.java)?.let {
                onCallClick.invoke(it.callResponse)
            }
        }
        return holder
    }


    override fun onBindViewHolder(holder: BaseViewHolder<*>, data: CallView, position: Int) {
        if (holder.binding is ItemCallHorizontalBinding) {
            holder.binding.itemLayout.background = holder.binding.root.context.compactDrawable(
                when {
                    data.isFirstItem.second -> R.drawable.bg_radius_11_white
                    data.isFirstItem.first -> R.drawable.bg_new_horizontal_top
                    data.isEndItem -> R.drawable.bg_new_horizontal_bot
                    else -> R.color.white
                }
            )
            if (data.isEndItem) {
                holder.binding.divider.gone()
                holder.binding.space.visible()
            } else {
                holder.binding.divider.visible()
                holder.binding.space.gone()
            }
            if (data.callResponse.note.isNullOrEmpty()) holder.binding.time.gone()
            else {
                holder.binding.time.visible()
                holder.binding.time.text = data.callResponse.note
            }
            holder.binding.location.text = data.callResponse.name
            holder.binding.phone.text = data.callResponse.tel
        }
    }


}


data class CallView(
    val isFirstItem: Pair<Boolean, Boolean>,
    val isEndItem: Boolean = false,
    val callResponse: CallResponse
) : AdapterEquatable {
    override fun getViewType() = 1
}