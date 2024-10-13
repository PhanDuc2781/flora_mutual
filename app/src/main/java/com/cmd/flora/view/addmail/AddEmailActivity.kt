package com.cmd.flora.view.addmail

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.cmd.flora.R
import com.cmd.flora.base.BaseVMActivity
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.noInset
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.data.model.WindowInsetModel
import com.cmd.flora.data.network.addEmailAddress
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.base.BaseErrorRes
import com.cmd.flora.data.network.response.EmailErrors
import com.cmd.flora.data.repository.onFailureDecode
import com.cmd.flora.databinding.ActivityAddEmailBinding
import com.cmd.flora.utils.MessageError
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.utils.bind
import com.cmd.flora.utils.compactDrawable
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.isEmail
import com.cmd.flora.utils.textFlow
import com.cmd.flora.utils.visible
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.main.MainActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddEmailActivity :
    BaseVMActivity<ActivityAddEmailBinding, AddEmailViewModel>(ActivityAddEmailBinding::inflate) {
    override val viewModel: AddEmailViewModel by viewModels()

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        binding.back.setOnBackClickListener {
            finish()
        }

        binding.btnSend.setOnSingleClickListener {
            viewModel.validateEmail(binding.edtEmail.text.toString())
        }

        binding.edtEmail.textFlow.bind(viewModel.observerEdittext, lifecycleScope) {
            viewModel.clearErrorTxt()
        }

        viewModel.observerEdittext.observe(this) { text ->
            binding.btnSend.apply {
                isEnabled = text.isNotEmpty()
                background = compactDrawable(
                    if (isEnabled) R.drawable.bg_btn_login else R.drawable.bg_disable_btn_login
                )
            }
        }

        viewModel.errorFormatEmail.observe(this) {
            binding.txtError.apply {
                text = it
                if (it.isNotEmpty()) visible() else gone()
            }
        }

        viewModel.addEmailState.observe(this) { isSuccess ->
            if (!isSuccess) return@observe
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(MainActivity.StartActivityForResult, AddEmailActivity::class.java.name)
            })
            finish()
        }

        viewModel.errorRequest.observe(this) {
            showCustomAlertDialog(
                AlertData(
                    title = it.title.toString(),
                    msg = it.errors?.email?.firstOrNull()
                        ?: getString(R.string.msg_erorr_request_add_email),
                    posTitle = getString(R.string.ok)
                )
            )
        }
    }

    override fun setupInset(insets: WindowInsetModel) {
        super.setupInset(insets)
        noInset()
        binding.root.setPadding(0, insets.top, 0, insets.bottom)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}

@HiltViewModel
class AddEmailViewModel @Inject constructor(val apiRequest: APIRequest, private val gson: Gson) :
    BaseViewModel() {

    val errorFormatEmail = MutableLiveData<String>()
    val addEmailState = SingleLiveEvent<Boolean>()
    val observerEdittext = MutableLiveData<String>()
    val errorRequest = SingleLiveEvent<BaseErrorRes<EmailErrors>>()

    fun validateEmail(email: String) {
        when {
            !email.isEmail() -> errorFormatEmail.postValue(MessageError.INVALID_EMAIL_MESSAGE)

            else -> {
                addEmailAddress(email)
            }
        }
    }

    private fun addEmailAddress(email: String) {
        showProgress(true)
        viewModelScope.launch {
            apiRequest.addEmailAddress(email).onSuccess {
                addEmailState.postValue(true)
                showProgress(false)
            }.onFailureDecode<Any?, EmailErrors>(gson) {
                showProgress(false)
                errorRequest.postValue(it)
            }
        }
    }

    fun clearErrorTxt() {
        errorFormatEmail.value = ""
    }
}