package com.cmd.flora.view.funeral

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.network.request.FacilityGenre
import com.cmd.flora.data.network.request.FacilityPageRequest
import com.cmd.flora.data.network.request.Prefecture.Companion.Fukushima
import com.cmd.flora.data.network.request.Prefecture.Companion.Gifu
import com.cmd.flora.data.network.request.Prefecture.Companion.Miyagi
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.repository.FacilityInformationRepository
import com.cmd.flora.databinding.FragmentFuneralBinding
import com.cmd.flora.view.BasePageListFragment
import com.cmd.flora.view.detail.DetailFragment
import com.cmd.flora.view.setUpData
import com.cmd.flora.view.wedding.FacilityInformationAdapter
import com.cmd.flora.view.wedding.FacilityView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FuneralFragment : BaseVMFragment<FragmentFuneralBinding, FacilityViewModel>(
    FragmentFuneralBinding::inflate
), BasePageListFragment.DataSource {
    override val viewModel: FacilityViewModel by viewModels()

    private lateinit var prefectureAdapter: PrefectureAdapter

    override fun initView() {
        super.initView()

        prefectureAdapter = PrefectureAdapter { index ->
<<<<<<< HEAD
            binding.viewpager.currentItem = index
            prefectureAdapter.setSelected(index)
=======
            viewModel.currentSelected.postValue(index)
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
        }

        binding.recyclerPrefecture.adapter = prefectureAdapter

        viewModel.prefectureLiveData.observe(this) {
            binding.recyclerPrefecture.layoutManager = GridLayoutManager(context, it.size)
            prefectureAdapter.submitList(it)
            binding.viewpager.setUpData(this, it.size)
        }
<<<<<<< HEAD
=======

        viewModel.currentSelected.observe(this) {
            binding.viewpager.currentItem = it
            prefectureAdapter.setSelected(it)
        }
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.loadPrefecture) viewModel.getListPrefecture()
    }

    override suspend fun onLoadData(
        position: Int, page: Int
    ): Result<Pair<List<AdapterEquatable?>, Boolean>> {
        return viewModel.getValues(position + 1, page)
    }

    override fun onCreateAdapter(): BaseAdapter<AdapterEquatable>? {
        return FacilityInformationAdapter {
            it.url?.let { url ->
                DetailFragment.start(mActivity, url)
            }
        } as? BaseAdapter<AdapterEquatable>
    }
}

@HiltViewModel
class FacilityViewModel @Inject constructor(
    private val facilityInformationRepository: FacilityInformationRepository,
) : BaseViewModel() {
    var loadPrefecture = true
<<<<<<< HEAD
=======
    val currentSelected = MutableLiveData<Int>(0)
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
    val prefectureLiveData = MutableLiveData<List<Prefecture>>()
    suspend fun getValues(idx: Int, page: Int): Result<Pair<List<FacilityView>, Boolean>> {
        return when (idx) {
            0 -> facilityInformationRepository.getFacilityPrefecture(
                FacilityPageRequest(
                    page = page, genre = FacilityGenre.Wedding
                )
            )

            1 -> facilityInformationRepository.getFacilityPrefecture(
                FacilityPageRequest(
                    page = page, prefecture = Fukushima, genre = FacilityGenre.Funeral
                )
            )

            2 -> facilityInformationRepository.getFacilityPrefecture(
                FacilityPageRequest(
                    page = page, prefecture = Miyagi, genre = FacilityGenre.Funeral
                )
            )

            else -> facilityInformationRepository.getFacilityPrefecture(
                FacilityPageRequest(
                    page = page, prefecture = Gifu, genre = FacilityGenre.Funeral
                )
            )

        }.map {
            val (values, hasNext) = it
            val mapValues = values.mapIndexed { index, callResponse ->
                FacilityView(callResponse)
            }
            return@map mapValues to hasNext
        }

    }

    fun getListPrefecture() {
        loadPrefecture = !loadPrefecture
        viewModelScope.launch {
            val listPrefecture = facilityInformationRepository.getPrefecture()
            prefectureLiveData.postValue(listPrefecture)
        }
    }
}
