package com.cmd.flora.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import coil.size.Dimension
import coil.size.Size
import coil.size.SizeResolver
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.MyFragment
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.network.response.HomeNotify
import com.cmd.flora.data.network.response.NewResponse
import com.cmd.flora.data.network.response.NotificationResponse
import com.cmd.flora.databinding.ItemHomeCallBinding
import com.cmd.flora.databinding.ItemHomeHeaderBinding
import com.cmd.flora.databinding.ItemHomeMenuBinding
import com.cmd.flora.databinding.ItemHomeQrBinding
import com.cmd.flora.databinding.ItemNewBinding
import com.cmd.flora.databinding.ItemNewHorizontalBinding
import com.cmd.flora.databinding.ItemNewTitleBinding
import com.cmd.flora.databinding.ItemToastBinding
import com.cmd.flora.utils.compactDrawable
import com.cmd.flora.utils.cv
import com.cmd.flora.utils.dpToPx
import com.cmd.flora.utils.getScoreText
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.hidden
import com.cmd.flora.utils.load
import com.cmd.flora.utils.loadLocal
import com.cmd.flora.utils.visible
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.main.userInformation
import com.cmd.flora.view.menu.MenuHomeAdapter
import com.cmd.flora.view.menu.MenuItem

class HomeAdapter(
    private val fragment: MyFragment,
    private val bgLoadingColor: Int = R.color.white,
    private val onNewClick: (NewResponse) -> Unit = {},
    private val onNotificationClick: (NotificationResponse) -> Unit = {},
    private val onDismissToast: (HomeNotify) -> Unit = {},
    private val onToastClick: (HomeNotify) -> Unit = {}
) : BaseAdapter<HomeData>() {

    override fun onBindLoadingViewHolder(parent: ViewGroup): ViewBinding {
        val binding = super.onBindLoadingViewHolder(parent)
        binding.root.background =
            binding.root.context.compactDrawable(bgLoadingColor)
        return binding
    }


    override fun createViewHolder(parent: ViewGroup, valueBase: HomeData): BaseViewHolder<*> {
        val holder = when (valueBase) {
            is HomeData.QRView -> {
                val binding = ItemHomeQrBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                val holder = BaseViewHolder(binding)
                binding.btnQr.setOnSingleClickListener {
                    (fragment.activity as? MainActivity)?.handleMenuItem(MenuItem.FEATURE_QR)
                }

                fragment.userInformation?.observe(fragment.viewLifecycleOwner) {
                    val point = (it.member?.point ?: "0").toDouble()
                    holder.binding.pointCount.text =
                        getScoreText(holder.binding.root.context, point, 1f, 12f / 30f)
                }
                holder
            }

            is HomeData.NewGridView -> {
                val binding =
                    ItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                holder.binding.root.setOnSingleClickListener {
                    getData(holder, HomeData.NewGridView::class.java)?.let {
                        onNewClick.invoke(it.newResponse)
                    }
                }
                holder
            }

            is HomeData.ToastView -> {
                val binding =
                    ItemToastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                binding.icClose.setOnSingleClickListener {
                    (getItem(holder.bindingAdapterPosition) as? HomeData.ToastView)
                        ?.toastMessage?.let(onDismissToast)
                }

                binding.root.setOnSingleClickListener {
                    (getItem(holder.bindingAdapterPosition) as? HomeData.ToastView)
                        ?.toastMessage?.let(onToastClick)
                }
                holder
            }

            HomeData.CallView -> {
                val binding =
                    ItemHomeCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                binding.phoneCallBtn.setOnSingleClickListener {
                    (fragment.activity as? MainActivity)?.goToPhoneList()
                }
                binding.image loadLocal R.drawable.banner_home
                holder
            }

            HomeData.MenuView -> {
                val binding =
                    ItemHomeMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                val menuHomeAdapter = MenuHomeAdapter(fragment.requireContext()) {
                    (fragment.activity as? MainActivity)?.handleMenuItem(it)
                }
                holder.binding.recyclerMenu.layoutManager =
                    GridLayoutManager(holder.binding.root.context, 4)
                holder.binding.recyclerMenu.adapter = menuHomeAdapter
                return holder
            }

            is HomeData.TitleView -> {
                val binding =
                    ItemHomeHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                BaseViewHolder(binding)
            }

            is HomeData.NewHorizontalView -> {
                val binding =
                    ItemNewHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                val holder = BaseViewHolder(binding)
                holder.binding.root.setOnSingleClickListener {
                    getData(holder, HomeData.NewHorizontalView::class.java)?.let {
                        onNewClick.invoke(it.newResponse)
                    }
                }
                holder
            }

            is HomeData.NewTitleView -> {
                val binding =
                    ItemNewTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                BaseViewHolder(binding)
            }

            is HomeData.NotificationView -> {
                val binding =
                    ItemNewHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                val holder = BaseViewHolder(binding)
                holder.binding.root.setOnSingleClickListener {
                    getData(holder, HomeData.NotificationView::class.java)?.let {
                        onNotificationClick.invoke(valueBase.notificationResponse)
                    }
                }
                holder
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, data: HomeData, position: Int) {
        when (data) {
            is HomeData.QRView -> {
//                if (holder.binding is ItemHomeQrBinding) {
//
//                }
            }

            is HomeData.NewGridView -> {
                if (holder.binding is ItemNewBinding) {
                    holder.binding.cardView.apply {
                        if (data.newResponse.id == null) hidden() else visible()
                    }
                    if (position % 2 == 0) {
                        holder.binding.spaceStart.apply {
                            layoutParams = layoutParams.apply {
                                width = 3.cv.dpToPx()
                            }
                        }
                        holder.binding.spaceEnd.apply {
                            layoutParams = layoutParams.apply {
                                width = 12.cv.dpToPx()
                            }
                        }
                    } else {
                        holder.binding.spaceStart.apply {
                            layoutParams = layoutParams.apply {
                                width = 12.cv.dpToPx()
                            }
                        }
                        holder.binding.spaceEnd.apply {
                            layoutParams = layoutParams.apply {
                                width = 3.cv.dpToPx()
                            }
                        }
                    }

                    holder.binding.image.load(data.newResponse.image) {
                        size(SizeResolver(Size(Dimension.Undefined, 96.cv.dpToPx())))
                    }
                    holder.binding.btnDetail.text = data.newResponse.displayGenre
                    holder.binding.message.text = data.newResponse.message
                }
            }

            is HomeData.ToastView -> {
                if (holder.binding is ItemToastBinding) {
                    if (!data.toastMessage?.title?.trim().isNullOrEmpty()) {
                        holder.binding.title.text = data.toastMessage?.title
                        holder.binding.description.text = data.toastMessage?.message
                        holder.binding.toastLayout.visible()
                    } else {
                        holder.binding.toastLayout.gone()
                    }
                }
            }

            is HomeData.TitleView -> {
                if (holder.binding is ItemHomeHeaderBinding) {
                    if (data.isVisible == null) {
                        holder.binding.titleItem.gone()
                        holder.binding.progressBar.visible()
                    } else {
                        if (data.isVisible) holder.binding.titleItem.visible()
                        else holder.binding.titleItem.gone()
                        holder.binding.progressBar.gone()
                    }
                }
            }

            is HomeData.NewHorizontalView -> {
                if (holder.binding is ItemNewHorizontalBinding) {
                    holder.binding.itemLayout.background =
                        holder.binding.root.context.compactDrawable(
                            when {
                                data.isFirstItem && data.isEndItem -> R.drawable.bg_radius_11_white
                                data.isFirstItem -> R.drawable.bg_new_horizontal_top
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
                    holder.binding.image.load(data.newResponse.image) {
                        size(320, 320)
                    }
                    holder.binding.message.text = data.newResponse.message
<<<<<<< HEAD
                    holder.binding.description.text = data.newResponse.description
=======
                    holder.binding.description.text = data.newResponse.description?.trim()
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
                }
            }

            is HomeData.NewTitleView -> {
                if (holder.binding is ItemNewTitleBinding) {
                    holder.binding.text.setText(data.title)
                }
            }

            is HomeData.NotificationView -> {
                if (holder.binding is ItemNewHorizontalBinding) {
                    holder.binding.itemLayout.background =
                        holder.binding.root.context.compactDrawable(
                            when {
                                data.isFirstItem && data.isEndItem -> R.drawable.bg_radius_11_white
                                data.isFirstItem -> R.drawable.bg_new_horizontal_top
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
                    holder.binding.image.load(data.notificationResponse.image)
                    holder.binding.message.text = data.notificationResponse.message
                    holder.binding.description.text = data.notificationResponse.description
                }
            }

            HomeData.CallView -> {}
            HomeData.MenuView -> {}
        }
    }

}


sealed class HomeData : AdapterEquatable {

    companion object {
        const val TOAST_VIEW_TYPE = 0
        const val HEADER_VIEW_TYPE = 1
        const val NEW_GRID_VIEW_TYPE = 2
        const val MENU_VIEW_TYPE = 3
        const val CALL_VIEW_TYPE = 4
        const val TITLE_VIEW_TYPE = 5
        const val NEW_HORIZONTAL_VIEW_TYPE = 6
        const val NEW_TITLE_VIEW_TYPE = 7
        const val NOTIFICATION_VIEW_TYPE = 8
    }

    data class ToastView(
        val toastMessage: HomeNotify?
    ) : HomeData() {
        override fun equalsContent(other: Any?): Boolean =
            this.toastMessage === (other as? ToastView)?.toastMessage

        override fun getViewType() = TOAST_VIEW_TYPE
    }

    data object QRView : HomeData() {
        override fun getViewType() = HEADER_VIEW_TYPE
    }

    data object MenuView : HomeData() {
        override fun getViewType() = MENU_VIEW_TYPE
    }

    data object CallView : HomeData() {
        override fun getViewType() = CALL_VIEW_TYPE
    }

    data class TitleView(val isVisible: Boolean?) : HomeData() {
        override fun equalsContent(other: Any?): Boolean =
            this.isVisible === (other as? TitleView)?.isVisible

        override fun getViewType() = TITLE_VIEW_TYPE
    }

    data class NewGridView(val newResponse: NewResponse) : HomeData() {
        override fun equalsContent(other: Any?): Boolean =
            this.newResponse === (other as? NewGridView)?.newResponse

        override fun getViewType() = NEW_GRID_VIEW_TYPE
    }

    data class NewHorizontalView(
        val isFirstItem: Boolean = false,
        val isEndItem: Boolean = false,
        val newResponse: NewResponse
    ) : HomeData() {
        override fun getViewType() = NEW_HORIZONTAL_VIEW_TYPE
    }

    data class NewTitleView(@StringRes val title: Int) : HomeData() {
        override fun getViewType() = NEW_TITLE_VIEW_TYPE
    }

    data class NotificationView(
        val isFirstItem: Boolean = false,
        val isEndItem: Boolean = false,
        val notificationResponse: NotificationResponse
    ) : HomeData() {
        override fun getViewType() = NOTIFICATION_VIEW_TYPE
    }

}