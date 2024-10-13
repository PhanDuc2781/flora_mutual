package com.cmd.flora.view.inquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.cmd.flora.R
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.BottomsheetPickerDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetPickerData : BottomSheetDialogFragment() {

    companion object {

        private const val SELECTED = "SELECTED"
        private const val VALUES = "VALUES"
        private const val CODE = "CODE"

        fun show(
            fMNG: FragmentManager,
            selectedValue: String? = null,
            code: Int = 0,
            values: List<String>
        ) {
            BottomSheetPickerData().apply {
                arguments = Bundle().apply {
                    putString(SELECTED, selectedValue)
                    putStringArray(VALUES, values.toTypedArray())
                    putInt(CODE, code)
                }
                show(fMNG, "")
            }
        }
    }

    private val onConfirmClickListener: OnConfirmClickListener?
        get() = parentFragment as? OnConfirmClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomsheetPickerDataBinding.inflate(inflater, container, false)
        val values = arguments?.getStringArray(VALUES)
        if (!values.isNullOrEmpty()) {
            val selected = arguments?.getString(SELECTED) ?: values.first()
            binding.numberPicker.minValue = 0
            binding.numberPicker.wrapSelectorWheel = false
            binding.numberPicker.maxValue = values.size - 1
            binding.numberPicker.displayedValues = values
            binding.numberPicker.value = values.indexOf(selected)
            binding.tvConfirm.setOnSingleClickListener {
                onConfirmClickListener?.onConfirm(
                    arguments?.getInt(CODE) ?: 0,
                    values[binding.numberPicker.value]
                )
                dismiss()
            }
        }

        binding.tvDelete.setOnSingleClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_BottomSheet)
    }


    interface OnConfirmClickListener {
        fun onConfirm(code: Int, label: String)
    }
}