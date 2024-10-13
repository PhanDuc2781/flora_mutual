package com.cmd.flora.utils.widget.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.ViewHeaderBackBinding

class BackHeaderView : FrameLayout {
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

    private lateinit var binding: ViewHeaderBackBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = ViewHeaderBackBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setOnBackClickListener(listener: (View) -> Unit) {
        binding.btnBack.setOnSingleClickListener { listener.invoke(binding.btnBack) }
    }
}