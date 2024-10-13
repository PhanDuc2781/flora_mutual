package com.cmd.flora.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.cmd.flora.R
import com.cmd.flora.databinding.ViewScreenTitleBinding

class ScreenNameTitle : FrameLayout {
    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    var text: String = ""
        set(value) {
            binding.text.text = value
            field = value
        }
        get() = binding.text.text.toString()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    private lateinit var binding: ViewScreenTitleBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = ViewScreenTitleBinding.inflate(LayoutInflater.from(context), this, true)

        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.ScreenNameTitle, defStyleAttr, 0)

        try {
            text = customAttributesStyle.getString(R.styleable.ScreenNameTitle_text) ?: ""
            binding.text.text = text
        } finally {
            customAttributesStyle.recycle()
        }
    }

}