package com.cmd.flora.utils.widget.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.cmd.flora.R
import com.cmd.flora.databinding.TabBarItemLayoutBinding

class TabBarItemView : FrameLayout {

    var text: String? = null
        set(value) {
            binding.tabText.text = value
            field = value
        }

    @DrawableRes
    var icon: Int? = null
    @ColorInt
    var defaultColor: Int = ContextCompat.getColor(context, R.color.primaryColor)
        set(value) {
            field = value
            isSelectedTab = isSelectedTab
        }

    @ColorInt
    var selectedColor: Int = ContextCompat.getColor(context, R.color.primaryColor)
        set(value) {
            field = value
            isSelectedTab = isSelectedTab
        }

    var isSelectedTab: Boolean = false
        set(value) {
            binding.imageIcon.setColorFilter(
                if (value) selectedColor else defaultColor
            )
            field = value
        }

    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    lateinit var binding: TabBarItemLayoutBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = TabBarItemLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.TabBarItemView, defStyleAttr, 0)

        try {
            icon = customAttributesStyle.getResourceId(
                R.styleable.TabBarItemView_tab_icon, R.drawable.ic_home
            )
            icon?.let { binding.imageIcon.setImageResource(it) }
            text = customAttributesStyle.getString(R.styleable.TabBarItemView_tab_text)
        } finally {
            customAttributesStyle.recycle()
        }
    }

}