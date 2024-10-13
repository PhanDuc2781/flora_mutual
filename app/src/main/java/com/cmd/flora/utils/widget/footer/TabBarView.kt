package com.cmd.flora.utils.widget.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.cmd.flora.R
import com.cmd.flora.databinding.TabBarLayoutBinding

class TabBarView : FrameLayout {

    private lateinit var binding: TabBarLayoutBinding

    @ColorInt
    var defaultColor: Int = ContextCompat.getColor(context, R.color.primaryColor)
        set(value) {
            field = value
            listTabItem.forEachIndexed { _, tabBarItemView ->
                tabBarItemView.defaultColor = value
            }
        }

    @ColorInt
    var selectedColor: Int = ContextCompat.getColor(context, R.color.primaryColor)
        set(value) {
            field = value
            listTabItem.forEachIndexed { _, tabBarItemView ->
                tabBarItemView.selectedColor = value
            }
        }

    @IntRange(from = 0, to = 4)
    var currentSelectedIndex: Int = 0
        set(value) {
            field = value
            listTabItem.forEachIndexed { index, tabBarItemView ->
                tabBarItemView.isSelectedTab = index == value
            }
        }

    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    private val listTabItem: List<TabBarItemView> by lazy {
        listOf(binding.tab1, binding.tab2, binding.tab3, binding.tab4)
    }

    private var callback: ((Int) -> Unit)? = null

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = TabBarLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.TabBarView, defStyleAttr, 0)

        try {
            defaultColor = customAttributesStyle.getColor(
                R.styleable.TabBarView_default_color,
                ContextCompat.getColor(context, R.color.tab_selected_color)
            )

            selectedColor = customAttributesStyle.getColor(
                R.styleable.TabBarView_selected_color,
                ContextCompat.getColor(context, R.color.tab_selected_color)
            )

        } finally {
            customAttributesStyle.recycle()
        }

        listTabItem.forEachIndexed { _, tabBarItemView ->
            tabBarItemView.selectedColor = selectedColor
            tabBarItemView.defaultColor = defaultColor
        }

        setupOnClick()
        currentSelectedIndex = 0
    }

    private fun setupOnClick() {
        listTabItem.forEachIndexed { index, tabBarItemView ->
            tabBarItemView.setOnClickListener {
                currentSelectedIndex = index
                callback?.invoke(index)
            }
        }
    }

    fun setOnTabChangeListener(callback: (Int) -> Unit) {
        this.callback = callback
    }

}

