package com.cmd.flora.view.menu

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseSingleAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.ItemMenuDataBinding
import com.cmd.flora.databinding.ItemMenuHomeBinding
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible

enum class MenuItem(
    @DrawableRes val res: Int,
    @StringRes val title: Int,
    @StringRes val homeTitle: Int? = null,
    @DrawableRes val homeIcon: Int? = null
) :
    AdapterEquatable {
    HOME(R.drawable.ic_home, R.string.title_home),
    FEATURE_QR(R.drawable.ic_feature_qr, R.string.title_qr),
    PERSON(R.drawable.ic_person, R.string.title_person),
    CONTACT(R.drawable.ic_phone, R.string.title_contact),
    NEWS(R.drawable.ic_megaphone, R.string.title_news, R.string.homeTitle_news, R.drawable.ic_home_news),
    WEDDING(R.drawable.ic_ring, R.string.title_wedding, R.string.homeTitle_wedding, R.drawable.ic_home_wedding),
    FUNERAL(R.drawable.ic_infomation, R.string.title_funeral, R.string.homeTitle_funeral, R.drawable.ic_home_funeral),
    FLOWER(R.drawable.ic_flower, R.string.title_flower, R.string.homeTitle_flower, R.drawable.ic_home_flower),
    CART(R.drawable.ic_cart, R.string.title_cart, R.string.homeTitle_cart, R.drawable.ic_home_cart),
    NEWSLETTER(R.drawable.ic_news, R.string.title_newsletter, R.string.homeTitle_newsletter, R.drawable.ic_home_newsletter),
    SETTING(R.drawable.ic_setting, R.string.title_Setting, R.string.homeTitle_setting, R.drawable.ic_home_setting),
    MEMBER(R.drawable.ic_member, R.string.title_member, R.string.homeTitle_member, R.drawable.ic_home_menber)
}

sealed class MenuItemData : AdapterEquatable {
    data class Item(val item: MenuItem) : MenuItemData() {
        override fun getViewType(): Int = 1
    }

    data object Space : MenuItemData() {
        override fun getViewType(): Int = 2
    }
}

class MenuItemAdapter(private val context: Context , private val onMenuClick: (MenuItem) -> Unit) :
    BaseSingleAdapter<MenuItemData, ItemMenuDataBinding>(ItemMenuDataBinding::inflate) {

    init {
        submitList(
            mutableListOf(
                MenuItemData.Item(MenuItem.HOME),
                MenuItemData.Item(MenuItem.FEATURE_QR),
                MenuItemData.Item(MenuItem.PERSON),
                MenuItemData.Space,
                MenuItemData.Item(MenuItem.NEWS),
                MenuItemData.Item(MenuItem.WEDDING),
                MenuItemData.Item(MenuItem.FUNERAL),
                MenuItemData.Item(MenuItem.FLOWER),
                MenuItemData.Item(MenuItem.CART),
                MenuItemData.Item(MenuItem.NEWSLETTER),
                MenuItemData.Space,
                MenuItemData.Item(MenuItem.SETTING),
                MenuItemData.Item(MenuItem.CONTACT)
            )
        )
    }

    override fun createViewHolder(binding: ItemMenuDataBinding): BaseViewHolder<ItemMenuDataBinding> {
        val holder = BaseViewHolder(binding)
        holder.binding.root.setOnSingleClickListener {
            when (val data = getItem(holder.bindingAdapterPosition)) {
                is MenuItemData.Item -> {
                    onMenuClick.invoke(data.item)
                }

                else -> {}
            }
        }
        return holder
    }

    override fun bindingViewHolder(holder: BaseViewHolder<ItemMenuDataBinding>, position: Int) {
        getItem(position)?.let {
            when (it) {
                is MenuItemData.Item -> {
                    holder.binding.line.gone()
                    holder.binding.image.visible()
                    holder.binding.image.setImageResource(it.item.res)
                    holder.binding.title.visible()
                    holder.binding.title.text = context.getString(it.item.title)
                }

                MenuItemData.Space -> {
                    holder.binding.line.visible()
                    holder.binding.image.gone()
                    holder.binding.title.gone()
                }
            }
        }
    }
}

class MenuHomeAdapter(private val context: Context , private val onMenuClick: (MenuItem) -> Unit) :
    BaseSingleAdapter<MenuItem, ItemMenuHomeBinding>(ItemMenuHomeBinding::inflate) {

    init {
        val items = listOf(
            MenuItem.MEMBER,
            MenuItem.NEWS,
            MenuItem.WEDDING,
            MenuItem.FUNERAL,
            MenuItem.FLOWER,
            MenuItem.CART,
            MenuItem.NEWSLETTER,
            MenuItem.SETTING
        )
        submitList(items)
    }

    override fun createViewHolder(binding: ItemMenuHomeBinding): BaseViewHolder<ItemMenuHomeBinding> {
        val holder = BaseViewHolder(binding)
        holder.binding.root.setOnSingleClickListener {
            getItem(holder.bindingAdapterPosition)?.let(onMenuClick)
        }
        return holder
    }

    override fun bindingViewHolder(holder: BaseViewHolder<ItemMenuHomeBinding>, position: Int) {
        getItem(position)?.let {item->
            item.homeIcon?.let { icon ->
                holder.binding.image.setImageResource(icon)
            }
            holder.binding.title.text = item.homeTitle?.let { title ->
                context.getString(title)
            }
        }
    }
}





