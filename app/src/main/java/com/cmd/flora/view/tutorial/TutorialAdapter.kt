package com.cmd.flora.view.tutorial

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseSingleAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.databinding.ItemTutorialBinding
import com.cmd.flora.utils.loadLocal

class TutorialAdapter :
    BaseSingleAdapter<TutorialData, ItemTutorialBinding>(ItemTutorialBinding::inflate) {
    override fun createViewHolder(binding: ItemTutorialBinding): BaseViewHolder<ItemTutorialBinding> {
        return BaseViewHolder(binding)
    }

    override fun bindingViewHolder(holder: BaseViewHolder<ItemTutorialBinding>, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            imageView loadLocal item?.image
            tvTitle.text = this.root.context.getString(item.title)
            tvDetail.text = this.root.context.getString(item.detail)
        }
    }
}

data class TutorialData(
    @DrawableRes val image: Int, @StringRes val title: Int, @StringRes val detail: Int
) : AdapterEquatable {
    companion object {
        fun imageTutorialList(): List<TutorialData> {
            return listOf(
                TutorialData(
                    R.drawable.tutorial_1, R.string.tutorial_title_1, R.string.tutorial_detail_1
                ),
                TutorialData(
                    R.drawable.tutorial_2, R.string.tutorial_title_2, R.string.tutorial_detail_2
                ),
                TutorialData(
                    R.drawable.tutorial_3, R.string.tutorial_title_3, R.string.tutorial_detail_3
                ),
                TutorialData(
                    R.drawable.tutorial_4, R.string.tutorial_title_4, R.string.tutorial_detail_4
                ),
                TutorialData(
                    R.drawable.tutorial_5, R.string.tutorial_title_5, R.string.tutorial_detail_5
                ),

                )
        }
    }
}
