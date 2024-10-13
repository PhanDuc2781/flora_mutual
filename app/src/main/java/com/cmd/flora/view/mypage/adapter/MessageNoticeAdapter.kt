package com.cmd.flora.view.mypage.adapter

import android.view.View
import com.cmd.flora.base.BaseSingleAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.network.response.NotificationResponse
import com.cmd.flora.databinding.ItemMessageNoticeBinding
import com.cmd.flora.utils.DateFormat
import com.cmd.flora.utils.dateFormat
import com.cmd.flora.utils.hidden
import com.cmd.flora.utils.visible

class MessageNoticeAdapter(private val selected: (NotificationResponse) -> Unit) :
    BaseSingleAdapter<NotificationResponse, ItemMessageNoticeBinding>(ItemMessageNoticeBinding::inflate) {
    override fun createViewHolder(binding: ItemMessageNoticeBinding): BaseViewHolder<ItemMessageNoticeBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.setOnSingleClickListener {
                getItem(bindingAdapterPosition)?.let { selected.invoke(it) }
            }
        }
    }


    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemMessageNoticeBinding>,
        position: Int
    ) {
        getItem(position)?.let {
            holder.binding.tvTitle.text = it.message
            holder.binding.tvDetail.text = it.description
            holder.binding.imgSeen.apply { if (it.isRead == false) visible() else hidden() }
            holder.binding.tvDate.text =
                it.date?.dateFormat(DateFormat.FULL_DATE_ORDER to DateFormat.FULL_DATE)
            holder.binding.linePage.visibility =
                if ((itemCount - 1) == position) View.GONE else View.VISIBLE
        }
    }

}