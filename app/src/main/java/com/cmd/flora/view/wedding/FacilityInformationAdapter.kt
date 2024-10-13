package com.cmd.flora.view.wedding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseViewHolder
import com.cmd.flora.base.Inflate2
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.network.response.FacilityInformation
import com.cmd.flora.databinding.ItemFacilityInformationBinding
import com.cmd.flora.databinding.ItemTagBinding
import com.cmd.flora.utils.cv
import com.cmd.flora.utils.dpToPx
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.load
import com.cmd.flora.utils.visible
import com.google.android.material.chip.ChipGroup


class FacilityInformationAdapter(private val selected: (FacilityInformation) -> Unit) :
    BaseAdapter<FacilityView>() {
    override fun createViewHolder(parent: ViewGroup, valueBase: FacilityView): BaseViewHolder<*> {
        val binding = ItemFacilityInformationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BaseViewHolder(binding).apply {
            binding.btnViewTheDetails.setOnSingleClickListener {
                getItem(absoluteAdapterPosition)?.let {
                    selected.invoke(it.facilityInformation)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, data: FacilityView, position: Int) {
        if (holder.binding is ItemFacilityInformationBinding) {
            data.facilityInformation.let {
                val listTag = it.tags.orEmpty().map { list -> list.name ?: "" }
                holder.binding.apply {
                    if (it.description?.isNotEmpty() == true) txtEvents.visible() else txtEvents.gone()
                    image.load(it.image)
                    chipGroup.addTags(listTag)
                    txtNameFacility.text = it.name
                    txtEvents.text = it.description
                    txtAddress.text = buildString {
                        append(if (it.zip.isNullOrEmpty()) "" else it.zip + " ")
                        append(it.prefecture ?: "")
                        append(it.address)
                    }
                    txtPhoneNumber.text = buildString {
                        append("TEL.")
                        append(it.tel)
                    }
                }
            }
        }
    }

}

fun <B : ViewBinding> ChipGroup.addChip(
    values: List<String>,
    inflate: Inflate2<B>,
    handle: (B, String) -> Unit
) {
    removeAllViews()
    values.map {
        val view = inflate.invoke(LayoutInflater.from(this.context), this, false)
        handle.invoke(view, it)
        return@map view
    }.forEach { this.addView(it.root) }
}

fun ChipGroup.addTags(values: List<String>) =
    addChip(values, ItemTagBinding::inflate) { binding, value ->
        binding.txtIntendTime.apply {
            setText(value, TextView.BufferType.NORMAL)
            chipHorizontalPadding = 24.cv.dpToPx()
        }
    }

data class FacilityView(
    val facilityInformation: FacilityInformation
) : AdapterEquatable {
    override fun getViewType() = 1
}
