package com.cmd.flora.utils.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.cmd.flora.R
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.ViewSwitchBinding
import com.cmd.flora.utils.cv
import com.cmd.flora.utils.dpToPx

class CustomSwitchView : FrameLayout {
    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    private var onChange: ((Boolean) -> Unit)? = null

    constructor(
        context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    private lateinit var binding: ViewSwitchBinding

    fun setChecked(isChecked: Boolean, withAnimation: Boolean = true) {
        this.isChecked = isChecked
        binding.thumb.animate()
            .setDuration(if (withAnimation) 220 else 0)
            .translationX(if (isChecked) 19.cv.dpToPx().toFloat() else 0f)
            .start()
        binding.bg.setCardBackgroundColor(if (!isChecked) defaultColor else selectedColor)
    }

    var isChecked: Boolean = false

    @ColorInt
    var defaultColor: Int = Color.parseColor("#e9e9e9")
        set(value) {
            field = value
            if (!isChecked) binding.bg.setCardBackgroundColor(defaultColor)
        }

    @ColorInt
    var selectedColor: Int = ContextCompat.getColor(context, R.color.tab_selected_color)
        set(value) {
            field = value
            if (isChecked) binding.bg.setCardBackgroundColor(selectedColor)
        }

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = ViewSwitchBinding.inflate(LayoutInflater.from(context), this, true)
        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.CustomSwitchView, defStyleAttr, 0)

        try {
            defaultColor = customAttributesStyle.getColor(
                R.styleable.CustomSwitchView_defaultColor,
                Color.parseColor("#e9e9e9")
            )

            selectedColor = customAttributesStyle.getColor(
                R.styleable.CustomSwitchView_selectedColor,
                ContextCompat.getColor(context, R.color.tab_selected_color)
            )

            isChecked =
                customAttributesStyle.getBoolean(R.styleable.CustomSwitchView_isChecked, false)
            setChecked(isChecked, false)

        } finally {
            customAttributesStyle.recycle()
        }


        binding.root.setOnSingleClickListener {
            val isChecked = !isChecked
            onChange?.invoke(isChecked)
            setChecked(isChecked, true)
        }
    }

    fun setOnCheckedChangeListener(onChange: (Boolean) -> Unit) {
        this.onChange = onChange
    }

}