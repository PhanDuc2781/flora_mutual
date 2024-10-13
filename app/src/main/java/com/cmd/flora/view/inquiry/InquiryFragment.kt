package com.cmd.flora.view.inquiry

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cmd.flora.BuildConfig
import com.cmd.flora.R
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.network.request.ContactInformationRequestModel
import com.cmd.flora.data.network.response.ContactGenreResponse
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.repository.InquiryRepository
import com.cmd.flora.databinding.FragmentInquiryBinding
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.utils.cv
import com.cmd.flora.utils.dpToPx
import com.cmd.flora.utils.isEmail
import com.cmd.flora.utils.isKatakana
import com.cmd.flora.utils.isPhoneNumber
import com.cmd.flora.utils.isPostalCode
import com.cmd.flora.utils.loadLocal
import com.cmd.flora.utils.widget.enableScrollText
import com.cmd.flora.utils.zip
import com.cmd.flora.view.fullscreenalert.FullScreenAlertActivity
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.main.userInformation
import com.cmd.flora.view.menu.MenuItem
import com.cmd.flora.view.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InquiryFragment :
    BaseVMFragment<FragmentInquiryBinding, InquiryViewModel>(FragmentInquiryBinding::inflate),
    BottomSheetPickerData.OnConfirmClickListener {
    override val viewModel: InquiryViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        setupInset(binding.bottomBar)
        binding.callItem.image loadLocal R.drawable.banner_home
        listOf(
            binding.input3,
            binding.input4,
            binding.input5,
            binding.input6,
            binding.input7,
            binding.input9,
            binding.input10,
            binding.input11
        ).forEach {
            it.setOnFocusChangedListener(it == binding.input11) { b ->
                val isInput11Focus = (it == binding.input11 && b)
                val bottomPadding = if (isInput11Focus) 180.cv.dpToPx() else 80.cv.dpToPx()
                binding.scrollContent.updatePadding(bottom = bottomPadding)
                if (b) {
                    binding.scrollContent.post {
                        if (!isDestroyed()) {
                            binding.scrollLayout.scrollTo(
                                0,
                                it.bottom - binding.scrollLayout.height / 2
                            )
                        }
                    }
                }
            }
        }

        binding.btnSend.setOnSingleClickListener {
            viewModel.sendContact(
                ContactInformationRequestModel(
<<<<<<< HEAD
                    user_id = userInformation?.value?.member_code,
=======
                    user_id = null,
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
                    genre = viewModel.input1.value?.second,
                    facility_id = viewModel.input2.value?.second,
                    name = binding.input3.value,
                    name_kana = binding.input4.value,
                    tel = binding.input5.value,
                    email = binding.input6.value,
                    postcode = binding.input7.value,
                    prefecture = viewModel.input8.value?.second,
                    city = binding.input9.value,
                    address = binding.input10.value,
                    opinion = binding.input11.value
                )
            )
        }

        binding.callItem.phoneCallBtn.setOnSingleClickListener {
            (mActivity as? MainActivity)?.goToPhoneList()
        }

        binding.input1.setOnInputClickListener {
            val mapList = viewModel.valuesContent.first.mapNotNull { it.label }
            BottomSheetPickerData.show(
                childFragmentManager,
                viewModel.input1.value?.first,
                1,
                mapList
            )
        }

        binding.input2.setOnInputClickListener {
            viewModel.getValueInput2().let {
                if (it.isNotEmpty()) {
                    BottomSheetPickerData.show(
                        childFragmentManager,
                        viewModel.input2.value?.first,
                        code = 2,
                        values = it
                    )
                }
            }
        }

        binding.input3.setOnValidChangedListener {
            return@setOnValidChangedListener it.isNotBlank()
        }

        binding.input4.setOnValidChangedListener {
            return@setOnValidChangedListener if (it.isKatakana()) {
                true
            } else if (it.isEmpty()) {
                binding.input4.validateMessage = getString(R.string.validate_name_2)
                false
            } else {
                binding.input4.validateMessage = getString(R.string.validate_name_2_format)
                false
            }
        }

        binding.input5.setOnValidChangedListener {
            return@setOnValidChangedListener if (it.isPhoneNumber()) {
                true
            } else if (it.isEmpty()) {
                binding.input5.validateMessage = getString(R.string.validate_phone)
                false
            } else {
                binding.input5.validateMessage = getString(R.string.validate_phone_format)
                false
            }
        }


        binding.input6.setOnValidChangedListener {
            return@setOnValidChangedListener if (it.isEmail()) {
                true
            } else if (it.isEmpty()) {
                binding.input6.validateMessage = getString(R.string.validate_email)
                false
            } else {
                binding.input6.validateMessage = getString(R.string.validate_email_format)
                false
            }
        }

        binding.input7.setOnValidChangedListener {
            return@setOnValidChangedListener it.isEmpty() || it.isPostalCode()
        }

        binding.input7.setOnTextChangedListener {
            binding.btnSend.isEnabled = (isInputValid()
                    && (viewModel.input1.value to viewModel.input2.value).isValid()
                    && binding.checkbox.isChecked)
        }

        binding.input8.setOnInputClickListener {
            val mapList = viewModel.valuesContent.second.map { it.value.orEmpty() }
            BottomSheetPickerData.show(
                childFragmentManager,
                viewModel.input8.value?.first,
                8,
                mapList
            )
        }

        binding.input11.setOnValidChangedListener {
            return@setOnValidChangedListener if (it.length in 1..1000) {
                true
            } else if (it.isEmpty()) {
                binding.input11.validateMessage = getString(R.string.validate_inquiry)
                false
            } else {
                binding.input11.validateMessage = getString(R.string.validate_inquiry_format)
                false
            }
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isDestroyed()) return@setOnCheckedChangeListener
            binding.btnSend.isEnabled = (isInputValid()
                    && (viewModel.input1.value to viewModel.input2.value).isValid()
                    && isChecked)
        }

        binding.checkboxContent.setOnSingleClickListener {
            WebViewActivity.start(requireActivity(), BuildConfig.Privacy_URL)
        }

        validateInput.forEach {
            it.setOnTextChangedListener {
                if (isDestroyed()) return@setOnTextChangedListener
                binding.btnSend.isEnabled = (isInputValid()
                        && (viewModel.input1.value to viewModel.input2.value).isValid()
                        && binding.checkbox.isChecked)
            }
        }

        (viewModel.input1 zip viewModel.input2).observe(this) {
            binding.input1.value = it.first?.first ?: ""
            binding.input2.value = it.second?.first ?: ""
            binding.btnSend.isEnabled =
                (isInputValid() && it.isValid() && binding.checkbox.isChecked)
        }

        viewModel.input8.observe(this) {
            binding.input8.value = it?.first ?: ""
        }
        viewModel.isSendSuccess.observe(this) {
            clearData()
            if (it) {
                FullScreenAlertActivity.start(
                    mActivity, FullScreenAlertActivity.Data(
                        title = getString(R.string.send_request_title),
                        message = getString(R.string.send_request_msg),
                        btnActionTitle = getString(R.string.back_to_home)
                    )
                ) {
                    (mActivity as? MainActivity)?.handleMenuItem(MenuItem.HOME)
                }
            } else {
                Log.d("TAG", "initView: fall")
            }
        }
    }

    private fun Pair<Pair<String, String>?, Pair<String, String>?>.isValid() =
        !first?.second.isNullOrEmpty() && !second?.second.isNullOrEmpty()

    private val validateInput by lazy {
        listOf(
            binding.input3,
            binding.input4,
            binding.input5,
            binding.input6,
            binding.input11
        )
    }

    fun clearData() {
        if (isDestroyed()) return
        binding.scrollLayout.scrollTo(0, 0)
        viewModel.input1.value = null
        viewModel.input2.value = null
        binding.input3.value = ""
        binding.input4.value = ""
        binding.input5.value = ""
        binding.input6.value = ""
        binding.input7.value = ""
        viewModel.input8.value = null
        binding.input9.value = ""
        binding.input10.value = ""
        binding.input11.value = ""
        binding.input11.binding.input.gravity = Gravity.CENTER_VERTICAL
        binding.checkbox.isChecked = false
        validateInput.forEach {
            it.resetValidate()
        }
    }

    private fun isInputValid(): Boolean {
        return validateInput.all { it.isValidData } && (binding.input7.value.isEmpty() || binding.input7.isValidData)
    }

    override fun onConfirm(code: Int, label: String) {
        when (code) {
            1 -> {
                if (label == viewModel.input1.value?.first) return
                val value = viewModel.getValueForInput1(label) ?: return
                viewModel.input1.value = label to value
                viewModel.input2.postValue(null)
                viewModel.getContacts()
            }

            2 -> {
                val value = viewModel.getValueForInput2(label) ?: return
                viewModel.input2.postValue(label to value)
            }

            8 -> {
                val value = viewModel.getValueForInput8(label) ?: return
                viewModel.input8.postValue(label to value)
            }
        }
    }

}

@HiltViewModel
class InquiryViewModel @Inject constructor(private val inquiryRepository: InquiryRepository) :
    BaseViewModel() {

    var valuesContent = Pair(
        emptyList<ContactGenreResponse>(),
        emptyList<Prefecture>()
    )


    private var valuesContact = emptyList<ContactGenreResponse>()

    val input1 = MutableLiveData<Pair<String, String>?>()
    val input2 = MutableLiveData<Pair<String, String>?>()
    val input8 = MutableLiveData<Pair<String, String>?>()

    val isSendSuccess = SingleLiveEvent<Boolean>()

    fun getValueInput2(): List<String> {
        return valuesContact.map { it.label.orEmpty() }
    }

    fun getValueForInput1(label: String): String? {
        return valuesContent.first.find { it.label == label }?.value
    }

    fun getValueForInput2(label: String): String? {
        return valuesContact.find { it.label == label }?.value
    }

    fun getValueForInput8(label: String): String? {
        return valuesContent.second.find { it.label == label }?.value
    }

    fun getContacts() {
        viewModelScope.launch {
            val valueContact =
                valuesContent.first.find { it.label == input1.value?.first.orEmpty() }
            valuesContact =
                inquiryRepository.getContacts(valueContact?.value.orEmpty())
        }
    }

    fun sendContact(dataRequest: ContactInformationRequestModel) {
        showProgress(true)
        viewModelScope.launch {
            inquiryRepository.sendContact(dataRequest)?.onSuccess {
                isSendSuccess.postValue(true)
            }?.onFailure {
                isSendSuccess.postValue(false)
            }
            showProgress(false)
        }
    }

    init {
        viewModelScope.launch {
            valuesContent = inquiryRepository.getDataInquiry()
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.enableScrollText() {
    overScrollMode = View.OVER_SCROLL_ALWAYS
    scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
    isVerticalScrollBarEnabled = true
    setOnTouchListener { view, event ->
        if (view is EditText) {
            if (!view.text.isNullOrEmpty()) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        false
    }
}