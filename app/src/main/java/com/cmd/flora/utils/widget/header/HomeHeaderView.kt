package com.cmd.flora.utils.widget.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.CustomHeaderviewBinding
import com.cmd.flora.utils.hidden
import com.cmd.flora.utils.visible

class HomeHeaderView : FrameLayout {

    var isShowBack: Boolean = false
        set(value) {
            field = value
            binding.imgLeftAction.apply { if (value) hidden() else visible() }
            binding.imgBackAction.apply { if (!value) hidden() else visible() }
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

    private lateinit var binding: CustomHeaderviewBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = CustomHeaderviewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setHasNotify(isHas: Boolean = false) {
        binding.imgHasNotify.apply { if (isHas) visible() else hidden() }
    }

    fun setOnLeftClickListener(
        onHambergerClick: (View) -> Unit,
        onBackClick: (View) -> Unit
    ) {
        binding.btnHumbeger.setOnSingleClickListener {
            if (isShowBack) onBackClick.invoke(it)
            else onHambergerClick.invoke(it)
        }
    }

    fun setOnUserClickListener(listener: (View) -> Unit) {
        binding.btnUser.setOnSingleClickListener { listener.invoke(binding.btnUser) }
    }

    fun setOnNotifyClickListener(listener: (View) -> Unit) {
        binding.btnNotify.setOnSingleClickListener { listener.invoke(binding.btnNotify) }
    }
}
