package com.cmd.flora.view.newsletter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.model.NewsLetter
import com.cmd.flora.databinding.ItemNewLetterBinding
import com.cmd.flora.utils.compactDrawable
import com.cmd.flora.utils.load
import com.cmd.flora.utils.splitStringWhitespace

class NewsLetterAdapter(private val selected: (NewsLetter) -> Unit) :
    BaseAdapter<NewsLetterView>() {

    override fun createViewHolder(parent: ViewGroup, valueBase: NewsLetterView): BaseViewHolder<*> {
        val binding =
            ItemNewLetterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BaseViewHolder(binding).apply {
            binding.btnViewTheDetails.setOnSingleClickListener {
                currentList.getOrNull(this.absoluteAdapterPosition)?.newsLetter?.let {
                    selected.invoke(it)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, data: NewsLetterView, position: Int) {
        if (holder.binding is ItemNewLetterBinding) {
            data.newsLetter.let {
                holder.binding.apply {
                    image.load(it.image)
                    txtPublished.text = ""
                    txtNames.text = ""
                    it.issue_at?.let {
                        val (first, second) = it.splitStringWhitespace()
                        txtPublished.text = first
                        txtNames.text = second
                    }

                    layout.background = root.context.compactDrawable(
                        when {
                            data.firstItem.second -> R.drawable.bg_radius_11_white
                            data.firstItem.first -> R.drawable.bg_new_horizontal_top
                            data.lastItem -> R.drawable.bg_new_horizontal_bot
                            else -> R.color.white
                        }
                    )
                    space.visibility =
                        if (!data.lastItem) View.GONE else View.VISIBLE
                    divider.visibility =
                        if (data.lastItem) View.INVISIBLE else View.VISIBLE
                }
            }
        }
    }

}

data class NewsLetterView(
    val firstItem: Pair<Boolean, Boolean>,
    val newsLetter: NewsLetter,
    val lastItem: Boolean
) : AdapterEquatable {
    override fun getViewType() = 1
}
