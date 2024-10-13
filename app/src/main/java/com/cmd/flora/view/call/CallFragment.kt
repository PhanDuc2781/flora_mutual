package com.cmd.flora.view.call

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.cmd.flora.R
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.data.network.request.FacilityGenre
import com.cmd.flora.data.network.request.TelPageRequest
import com.cmd.flora.data.repository.CallRepository
import com.cmd.flora.databinding.FragmentCallBinding
import com.cmd.flora.utils.launchIntent
import com.cmd.flora.view.BasePageListFragment
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.setUpData
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CallFragment :
    BaseVMFragment<FragmentCallBinding, CallViewModel>(FragmentCallBinding::inflate),
    BasePageListFragment.DataSource {
    override val viewModel: CallViewModel by viewModels()

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        mActivity?.launchIntent(intent)
    }

    override fun initView() {
        setupInset(binding.bottomBar)
        binding.viewPager.setUpData(this, 3)

        binding.first.setOnSingleClickListener {
            viewModel.currentIdxLiveData.postValue(0)
        }
        binding.second.setOnSingleClickListener {
            viewModel.currentIdxLiveData.postValue(1)
        }
        binding.third.setOnSingleClickListener {
            viewModel.currentIdxLiveData.postValue(2)
        }

        viewModel.currentIdxLiveData.observe(this) { menuType ->
            binding.viewPager.currentItem = menuType
            when (menuType) {
                0 -> {
                    binding.first.isEnabled = false
                    binding.second.isEnabled = true
                    binding.third.isEnabled = true
                }

                1 -> {
                    binding.first.isEnabled = true
                    binding.second.isEnabled = false
                    binding.third.isEnabled = true
                }

                else -> {
                    binding.first.isEnabled = true
                    binding.second.isEnabled = true
                    binding.third.isEnabled = false
                }
            }
        }

    }

    override suspend fun onLoadData(
        position: Int,
        page: Int
    ): Result<Pair<List<AdapterEquatable?>, Boolean>> {
        return viewModel.getValues(position, page)
    }

    override fun onCreateAdapter(): BaseAdapter<AdapterEquatable>? {
        return CallAdapter {
            mActivity?.showCustomAlertDialog(
                AlertData(
                    getString(R.string.call_request_title),
                    getString(R.string.call_request_msg, it.tel),
                    getString(R.string.outgoing),
                    getString(R.string.cancelMsg)
                ) { pos ->
                    if (pos) dialPhoneNumber(it.tel ?: return@AlertData)
                })
        } as? BaseAdapter<AdapterEquatable>
    }

}

@HiltViewModel
class CallViewModel @Inject constructor(private val callRepository: CallRepository) :
    BaseViewModel() {

    val currentIdxLiveData = MutableLiveData<Int>(0)
    suspend fun getValues(idx: Int, page: Int): Result<Pair<List<CallView>, Boolean>> {
        return when (idx) {
            0 -> callRepository.getTelPage(TelPageRequest(page, genre = FacilityGenre.Funeral))
            1 -> callRepository.getTelPage(TelPageRequest(page, genre = FacilityGenre.Wedding))
            else -> callRepository.getTelPage(TelPageRequest(page, genre = FacilityGenre.Benefit))
        }.map { value ->
            val (values, hasNext) = value
            val mapValues = values.mapIndexed { index, callResponse ->
                CallView(
                    isFirstItem = (index == 0 && page == 1) to (page == 1 && values.size == 1),
                    callResponse = callResponse,
                    isEndItem = (index == values.size - 1 && !hasNext),
                )
            }
            return@map mapValues to hasNext
        }
    }
}

