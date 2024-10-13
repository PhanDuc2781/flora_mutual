package com.cmd.flora.view.funeral

import com.cmd.flora.R
import com.cmd.flora.base.BaseSingleAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.databinding.ItemPrefectureBinding

class PrefectureAdapter(private val selected: (Int) -> Unit) :
    BaseSingleAdapter<Prefecture, ItemPrefectureBinding>(ItemPrefectureBinding::inflate) {

    var selectedID = 0

    fun setSelected(position: Int) {
        selectedID = position
        notifyDataSetChanged()
    }

    override fun createViewHolder(binding: ItemPrefectureBinding): BaseViewHolder<ItemPrefectureBinding> {
        return BaseViewHolder(binding).apply {
            binding.tvPrefecture.setOnSingleClickListener {
                getItem(bindingAdapterPosition)?.let {
                    selected.invoke(
                        bindingAdapterPosition
                    )
                }
            }
        }
    }


    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemPrefectureBinding>, position: Int
    ) {
        getItem(position)?.let {
            holder.binding.apply {
                tvPrefecture.text = it.value
                tvPrefecture.isEnabled = position != selectedID
                val padding =
                    parent.context.resources.getDimension(if (itemCount > 2) R.dimen.dp_2 else R.dimen.dp_5)
                        .toInt()
                parent.setPadding(padding, 0, padding, 0)
            }
        }
    }

}