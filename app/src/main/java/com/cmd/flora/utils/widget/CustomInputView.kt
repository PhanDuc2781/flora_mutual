package com.cmd.flora.utils.widget

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.cmd.flora.R
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.CustomInputViewBinding
import com.cmd.flora.utils.compactDrawable
import com.cmd.flora.utils.cv
import com.cmd.flora.utils.dpToPx
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible
import com.cmd.flora.view.inquiry.enableScrollText

class CustomInputView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    private var isValid: Boolean? = null
        set(value) {
            if (!validateMessage.isNullOrEmpty() && value == false) {
                binding.validateMessage.visible()
                binding.validateMessage.text = validateMessage
            } else {
                binding.validateMessage.gone()
            }
            binding.input.background = context.compactDrawable(
                if (value == false) R.drawable.bg_radius_10_border_red else R.drawable.bg_radius_10_border_gray
            )
            field = value
        }

    var validateMessage: String? = null
    val isValidData: Boolean
        get() {
            return isValid == true
        }

    var title: String = ""
        set(value) {
            if (value.isEmpty()) {
                binding.title.visibility = GONE
            } else {
                binding.title.visibility = VISIBLE
                binding.title.text = value
            }
            field = value
        }

    private var hint: String = ""
        set(value) {
            binding.input.hint = value
            field = value
        }

    var value: String = ""
        set(value) {
            if (value.isEmpty()) {
                binding.input.text.clear()
            } else {
                binding.input.setText(value)
            }
            field = value
        }
        get() {
            return binding.input.text.toString()
        }


    lateinit var binding: CustomInputViewBinding

    private fun initView(attrs: AttributeSet?) {
        binding = CustomInputViewBinding.inflate(LayoutInflater.from(context), this, true)
        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.CustomInputView)

        try {
            title = customAttributesStyle.getString(R.styleable.CustomInputView_title) ?: ""
            hint = customAttributesStyle.getString(R.styleable.CustomInputView_hint) ?: ""
            validateMessage = customAttributesStyle.getString(
                R.styleable.CustomInputView_validateMessage
            )

            val inputHeight = customAttributesStyle.getInteger(
                R.styleable.CustomInputView_inputHeight,
                64
            )

            if (inputHeight > 0) binding.input.layoutParams = binding.input.layoutParams.apply {
                height = inputHeight.cv.dpToPx()
            }

            val singleLine = customAttributesStyle.getBoolean(
                R.styleable.CustomInputView_isSingleLine,
                true
            )

            if (singleLine) {
                binding.input.maxLines = 1
            } else {
                binding.input.enableScrollText()
            }

            val maxChar = customAttributesStyle.getInteger(
                R.styleable.CustomInputView_maxCharacterCount,
                0
            )

            if (customAttributesStyle.getBoolean(
                    R.styleable.CustomInputView_isDropdown,
                    false
                )
            ) {
                binding.input.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_drop_down,
                    0
                )
                binding.input.focusable = NOT_FOCUSABLE
                binding.input.inputType = InputType.TYPE_NULL
                binding.input.setPadding(11.cv.dpToPx(), 0, 17.cv.dpToPx(), 0)
                binding.input.setOnSingleClickListener {
                    inputClick.invoke()
                }
            } else {
                binding.input.focusable = FOCUSABLE_AUTO
                when (InputEditTextType.getValue(
                    customAttributesStyle.getInt(
                        R.styleable.CustomInputView_input_type,
                        0
                    )
                )) {
                    InputEditTextType.TEXT -> binding.input.inputType =
                        if (singleLine) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                        else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE

                    InputEditTextType.EMAIL -> binding.input.inputType =
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                    InputEditTextType.NUMBER -> binding.input.inputType =
                        InputType.TYPE_CLASS_NUMBER

                    InputEditTextType.PHONE -> binding.input.inputType = InputType.TYPE_CLASS_PHONE
                }
            }

            if (maxChar > 0) {
                binding.input.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                        maxChar
                    )
                )
                binding.input.addTextChangedListener {
                    val text = it.toString()
                    binding.inputCounter.text =
                        context.getString(R.string.counter_inquiry, text.length, maxChar)
                    isValid = validChange.invoke(text)
                    textChange.invoke(text)
                }
                binding.inputCounter.text = context.getString(R.string.counter_inquiry, 0, maxChar)
                binding.inputCounter.visible()
            } else {
                binding.input.addTextChangedListener {
                    val text = it.toString()
                    isValid = validChange.invoke(text)
                    textChange.invoke(text)
                }
                binding.inputCounter.gone()
            }
        } finally {
            customAttributesStyle.recycle()
        }
    }

    private var textChange: (String) -> Unit = {}
    private var validChange: (String) -> Boolean = { true }
    private var inputClick: () -> Unit = {}

    fun setOnTextChangedListener(textChange: (String) -> Unit) {
        this.textChange = textChange
    }

    fun setOnValidChangedListener(validChange: (String) -> Boolean) {
        this.validChange = validChange
    }


    fun setOnInputClickListener(inputClick: () -> Unit) {
        this.inputClick = inputClick
    }

    fun resetValidate() {
        isValid = null
    }

    fun setOnFocusChangedListener(changeGravityLostFocus: Boolean = false, hasFocus: (Boolean) -> Unit) {
        binding.input.setOnFocusChangeListener { _, isFocus ->
            if (changeGravityLostFocus) {
                if (isFocus) {
                    binding.input.gravity = Gravity.CENTER_VERTICAL
                } else if (value.isNotEmpty()) {
                    binding.input.gravity = Gravity.START
                }
            }
            hasFocus(isFocus)
        }
    }

}

enum class InputEditTextType(val value: Int) {
    TEXT(value = 0),
    EMAIL(value = 1),
    NUMBER(value = 2),
    PHONE(value = 3);

    companion object {
        fun getValue(int: Int): InputEditTextType {
            return entries.firstOrNull { it.value == int } ?: TEXT
        }
    }
}

fun CustomInputView.enableScrollText() {
    this.binding.input.enableScrollText()
}