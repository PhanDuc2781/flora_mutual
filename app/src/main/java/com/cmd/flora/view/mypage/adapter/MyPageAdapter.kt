package com.cmd.flora.view.mypage.adapter

import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.model.ServiceHistory
import com.cmd.flora.data.network.response.NotificationResponse
import com.cmd.flora.databinding.ItemHistoryServiceBinding
import com.cmd.flora.databinding.ItemMyPageContractBinding
import com.cmd.flora.databinding.ItemMyPageMessageBinding
import com.cmd.flora.databinding.ItemMyPageSeeMoreBinding
import com.cmd.flora.utils.DateFormat
import com.cmd.flora.utils.compactDrawable
import com.cmd.flora.utils.dateFormat
import com.cmd.flora.utils.formatJapan
import com.cmd.flora.utils.getScoreText
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible
import com.cmd.flora.utils.widget.CustomTypefaceSpan
import com.cmd.flora.view.home.orNull
import com.cmd.flora.view.main.userInformation
import com.cmd.flora.view.mypage.MyPageFragment

class MyPageAdapter(
    private val fragment: MyPageFragment,
    private val onItemMessageClick: (NotificationResponse) -> Unit,
    private val onMemberStatusClick: () -> Unit,
    private val onShowListNoticeClick: () -> Unit,
    private val onSeeMoreClick: (Boolean) -> Unit,
    private val onServiceClick: (ServiceHistory) -> Unit
) : BaseAdapter<MyPageData>() {
    //    var isEnableView = true
    override fun createViewHolder(parent: ViewGroup, valueBase: MyPageData): BaseViewHolder<*> {
        val holder = when (valueBase) {
            is MyPageData.MessageFlora -> {
                val binding = ItemMyPageMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                val holder = BaseViewHolder(binding)
                val messageNoticeAdapter = MessageNoticeAdapter(onItemMessageClick)
                holder.binding.recyclerView.apply {
                    layoutManager =
                        object : LinearLayoutManager(binding.root.context, VERTICAL, false) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                    adapter = messageNoticeAdapter
                }
                holder.binding.btnSeeMore.setOnClickListener {
                    onShowListNoticeClick.invoke()
                }
                holder
            }

            is MyPageData.ContractData -> {
                val binding = ItemMyPageContractBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                val holder = BaseViewHolder(binding)
                holder.binding.btnMemberStatus.setOnSingleClickListener {
                    onMemberStatusClick.invoke()
                }

                fragment.userInformation?.observe(fragment.viewLifecycleOwner) {
                    val point = (it.member?.point ?: "0").toDouble()
                    holder.binding.tvCoin.text =
                        getScoreText(holder.binding.root.context, point, 1f, 15f / 40)
                }

                fragment.userInformation?.map { it.member?.payment_count_in_month }
                    ?.observe(fragment.viewLifecycleOwner) {
                        if (it == null || it <= 0) {
                            holder.binding.tvCurrentNumber.gone()
                        } else {
                            holder.binding.tvCurrentNumber.visible()
                            val value = it
                            val originalString = holder.binding.root.context.getString(
                                R.string.txt_current_number, value
                            )
                            holder.binding.tvCurrentNumber.setSpannableBold(
                                originalString,
                                value.toString().length
                            )
                        }

                    }

                fragment.userInformation?.map { it.member?.rank_display }
                    ?.observe(fragment.viewLifecycleOwner) {
                        holder.binding.tvPeaceOfMind.text = it
                    }

                fragment.userInformation?.map { it.member?.contract?.expected_completion_date }
                    ?.observe(fragment.viewLifecycleOwner) {
                        val dateFormat =
                            it?.orNull()
                                ?.dateFormat(DateFormat.FULL_DATE_API, DateFormat.FULL_DATE_JP)
                                ?: "-"
                        val originalString = holder.binding.root.context.getString(
                            R.string.txt_date_completion,
                            dateFormat
                        )
                        holder.binding.tvDateCompletion.setSpannableBold(
                            originalString,
                            dateFormat.length
                        )
                    }

                fragment.viewModel.visibleHistoryView.observe(fragment.viewLifecycleOwner) {
                    holder.binding.groupHistory.visibility =
                        if (it.first) View.VISIBLE else View.GONE
                    holder.binding.progressBar.visibility =
                        if (it.first || it.second == 0) View.GONE else View.VISIBLE
                    holder.binding.root.background = holder.binding.root.context.compactDrawable(
                        R.drawable.bg_white_border_top_10
                    )
                }
                holder
            }

            is MyPageData.SeeMoreView -> {
                val binding = ItemMyPageSeeMoreBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                val holder = BaseViewHolder(binding)
                fragment.viewModel.showSeeMoreOrClose.observe(fragment.viewLifecycleOwner) { seeMore ->
                    holder.binding.btnSeeMore.apply {
                        if (seeMore.show) {
                            setOnSingleClickListener {
                                gone()
                                holder.binding.progressBar.visible()
                                onSeeMoreClick.invoke(!seeMore.isClose && seeMore.show)
                            }
                            visible()
                            text =
                                holder.binding.root.context.getString(if (!seeMore.isClose && seeMore.show) R.string.see_more else R.string.close)
                        } else {
                            gone()
                        }
                    }
                    holder.binding.progressBar.gone()
                }
                holder
            }

            is MyPageData.ServiceHistoryView -> {
                val binding = ItemHistoryServiceBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BaseViewHolder(binding).apply {
                    binding.root.setOnSingleClickListener {
                        onServiceClick.invoke(valueBase.history)
                    }
                }
            }

        }
        return holder
    }

    private fun TextView.setSpannableBold(originalString: String, textIntNormal: Int) {
        val spannableString = SpannableString.valueOf(originalString)
        val numberEnd = originalString.length - (textIntNormal + 1)
        spannableString.setSpan(
            CustomTypefaceSpan(
                ResourcesCompat.getFont(
                    context,
                    R.font.hiragino_kaku_gothic_pro_w6
                )
            ),
            0,
            numberEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        this.text = spannableString
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, data: MyPageData, position: Int) {
        when (data) {
            is MyPageData.MessageFlora -> {
                if (holder.binding is ItemMyPageMessageBinding) {
                    val listMessageNotice = data.list.orEmpty()
                    holder.binding.apply {
                        layoutMess.visibility =
                            if (listMessageNotice.isNotEmpty()) View.VISIBLE else View.GONE
                    }

                    (holder.binding.recyclerView.adapter as? MessageNoticeAdapter)?.apply {
                        submitList(listMessageNotice)
                        notifyDataSetChanged()
                    }
                }
            }

            is MyPageData.ContractData -> {
                if (holder.binding is ItemMyPageContractBinding) {
                    holder.binding.layout.background = holder.binding.root.context.compactDrawable(
                        R.drawable.bg_radius_10_white
                    )
                }
            }

            is MyPageData.SeeMoreView -> {
                if (holder.binding is ItemMyPageSeeMoreBinding) {
//                    holder.binding.btnSeeMore.isEnabled = isEnableView
                }
            }

            is MyPageData.ServiceHistoryView -> {
                if (holder.binding is ItemHistoryServiceBinding) {
                    val listItem = data.history
                    listItem.let {
                        holder.binding.tvTitle.text = it.message
//                        holder.binding.tvDetail.text = it.product
                        holder.binding.tvDate.text =
                            it.date?.dateFormat(DateFormat.FULL_DATE_ORDER, DateFormat.FULL_DATE)
                        holder.binding.tvYenPrice.text =
                            holder.binding.root.context
                                .getString(
                                    R.string.price,
                                    it.price?.formatJapan() ?: 0
                                )
                    }
                }
            }
        }
    }
}

sealed class MyPageData : AdapterEquatable {

    companion object {
        const val MESSAGE_FLORA_VIEW_TYPE = 0
        const val CONTRACT_VIEW_TYPE = 1
        const val SEE_MORE_VIEW_TYPE = 2
        const val SERVICE_HISTORY_VIEW_TYPE = 3
    }

    data class MessageFlora(
        val list: List<NotificationResponse>?
    ) : MyPageData() {
        override fun equalsContent(other: Any?): Boolean =
            this.list === (other as? MessageFlora)?.list

        override fun getViewType() = MESSAGE_FLORA_VIEW_TYPE
    }

    data object ContractData :
        MyPageData() {
        override fun getViewType() = CONTRACT_VIEW_TYPE
    }

    data object SeeMoreView : MyPageData() {
        override fun getViewType() = SEE_MORE_VIEW_TYPE
    }

    data class ServiceHistoryView(val history: ServiceHistory) : MyPageData() {
        override fun equalsContent(other: Any?): Boolean =
            this.history === (other as? ServiceHistoryView)?.history

        override fun getViewType() = SERVICE_HISTORY_VIEW_TYPE
    }
}