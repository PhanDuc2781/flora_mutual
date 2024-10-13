package com.cmd.flora.view.qrscreen

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cmd.flora.R
import com.cmd.flora.base.BaseVMActivity
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.noInset
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.data.model.QRData
import com.cmd.flora.data.model.WindowInsetModel
import com.cmd.flora.data.network.response.PointResponse
import com.cmd.flora.data.repository.QRRepository
import com.cmd.flora.data.repository.onFailureDecode
import com.cmd.flora.databinding.ActivityQrBinding
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.utils.getScoreText
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.loadSVG
import com.cmd.flora.utils.visible
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.main.MainActivity.Companion.StartActivityForResult
import com.cmd.flora.view.main.MainActivity.Companion.StartActivityForResult_Logout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QRActivity : BaseVMActivity<ActivityQrBinding, QRViewModel>(ActivityQrBinding::inflate) {
    companion object {
        const val VIEW_MODE = "VIEW_MODE"
        const val TARGET_ID = "TARGET_ID"
        const val VIEW_MODE_QR = 11
        const val VIEW_MODE_SCORE = 12
    }

    override val viewModel: QRViewModel by viewModels()
    private val layout: WindowManager.LayoutParams by lazy { window.attributes }
    private var previousBrightness: Float = 1F
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        if (intent.getIntExtra(VIEW_MODE, VIEW_MODE_QR) == VIEW_MODE_QR) {

            binding.btnBack.setOnSingleClickListener {
                setResult(RESULT_OK, Intent()
                    .apply { putExtra(StartActivityForResult, QRActivity::class.java.name) })
                finish()
            }

            previousBrightness = layout.screenBrightness
            layout.screenBrightness = 1f
            window.attributes = layout

            binding.scoreLayout.gone()
            binding.qrLayout.visible()
            viewModel.qrData.observe(this) {
                binding.imgQR.loadSVG(it.qrUrl)
            }
            viewModel.getQRData()
        } else {
            binding.btnBack.setOnSingleClickListener { finish() }
            binding.qrLayout.gone()
            binding.scoreLayout.visible()
            viewModel.score.observe(this) {
                binding.tvTitle.text = getString(R.string.label_current_point_title)
                it.data?.add_point?.let {
                    binding.tvAddScore.text = getScoreText(it)
                }
                it.data?.sub_point?.let {
                    binding.tvUseScore.text = getScoreText(it)
                }
                if (!it.data?.facility_name?.trim().isNullOrEmpty()) {
                    binding.tvPlace.text = it.data?.facility_name
                }
                if (!it.data?.updated_at?.trim().isNullOrEmpty()) {
                    binding.tvDate.text = it.data?.updated_at
                }
            }

            viewModel.checkScore(intent.getStringExtra(TARGET_ID))

            viewModel.pointErrorRequest.observe(this) {
                it?.let {
                    showCustomAlertDialog(AlertData(msg = it, posTitle = getString(R.string.ok)) {
                        finish()
                    })
                }
            }

        }
    }

    override fun setupInset(insets: WindowInsetModel) {
        super.setupInset(insets)
        noInset()
        binding.containerQr.setPadding(0, insets.top, 0, insets.bottom)
    }

    private fun getScoreText(score: Double): CharSequence {
        return getScoreText(this, score, 1f, 15f / 28)
    }

    override fun onDestroy() {
        super.onDestroy()
        layout.screenBrightness = previousBrightness
        window.attributes = layout
    }

    override fun reloadWhenLogout() {
        super.reloadWhenLogout()
        setResult(RESULT_OK, Intent()
            .apply { putExtra(StartActivityForResult_Logout, true) })
        finish()
    }
}

@HiltViewModel
class QRViewModel @Inject constructor(val qrRepository: QRRepository, private val gson: Gson) :
    BaseViewModel() {

    val qrData = MutableLiveData<QRData>()
    val score = MutableLiveData<PointResponse>()
    val pointErrorRequest = SingleLiveEvent<String?>()
    fun getQRData() {
//        showProgress(true)
        viewModelScope.launch {
            val data = qrRepository.getQRData()
            data.onSuccess {
                this@QRViewModel.showProgress(false)
                it?.let { qrData.postValue(it) }
            }
        }
    }

    fun checkScore(targetId: String?) {
        showProgress(true)
        viewModelScope.launch {
            val data = targetId?.let { qrRepository.checkScore(it.toInt()) }
            data?.onSuccess {
                this@QRViewModel.showProgress(false)
                it?.let { score.postValue(it) }
            }?.onFailureDecode<PointResponse?, Unit>(gson) {
                this@QRViewModel.showProgress(false)
                it?.detail?.message?.let { msg ->
                    pointErrorRequest.postValue(msg)
                }
            }
        }
    }
}