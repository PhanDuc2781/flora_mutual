package com.cmd.flora.view.newsletter

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.repository.NewsLetterRepository
import com.cmd.flora.databinding.FragmentNewsLetterBinding
import com.cmd.flora.view.BasePageListFragment
import com.cmd.flora.view.funeral.PrefectureAdapter
import com.cmd.flora.view.setUpData
import com.cmd.flora.view.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewsLetterFragment : BaseVMFragment<FragmentNewsLetterBinding, NewsLetterViewModel>(
    FragmentNewsLetterBinding::inflate
), BasePageListFragment.DataSource {
    override val viewModel: NewsLetterViewModel by viewModels()

    private lateinit var prefectureAdapter: PrefectureAdapter

    companion object {
        const val URL_PREFECTURE = "https://www.flora-g.co.jp/egao2"
        const val UL_CLASS = "egaoList content_row size_one-fifth"
    }

    override fun initView() {
        super.initView()

        prefectureAdapter = PrefectureAdapter { index ->
            binding.viewPager.currentItem = index
            prefectureAdapter.setSelected(index)
        }

        binding.recyclerPrefecture.adapter = prefectureAdapter

        viewModel.prefectureLiveData.observe(this) {
            binding.recyclerPrefecture.layoutManager =
                GridLayoutManager(context, it.size)
            prefectureAdapter.submitList(it)
            binding.viewPager.setUpData(this, it.size)

        }
    }

    override suspend fun onLoadData(
        position: Int,
        page: Int
    ): Result<Pair<List<NewsLetterView>, Boolean>> {
        return viewModel.getValues(position, page)
    }

    override fun onCreateAdapter(): BaseAdapter<AdapterEquatable>? {
        return NewsLetterAdapter {
            it.url?.let { url ->
                mActivity?.let { it1 ->
                    WebViewActivity.start(
                        it1,
                        url
                    )
                }
            }
        } as? BaseAdapter<AdapterEquatable>
    }
}

@HiltViewModel
class NewsLetterViewModel @Inject constructor(
    private val newsLetterRepository: NewsLetterRepository
) : BaseViewModel() {

    val prefectureLiveData = MutableLiveData<List<Prefecture>>()

    suspend fun getValues(idx: Int, page: Int): Result<Pair<List<NewsLetterView>, Boolean>> {
        return newsLetterRepository.getNewsLetter(idx, page == 1).map {
            val value = it?.mapIndexed { index, callResponse ->
                NewsLetterView(
                    (index == 0) to (it.size == 1),
                    callResponse,
                    it.size - 1 == index
                )
            } ?: emptyList()
            return@map value to false
        }
    }

    private fun getListPrefecture() {
        viewModelScope.launch {
            val listPrefecture = newsLetterRepository.getPrefecture()
            prefectureLiveData.postValue(listPrefecture)
        }
    }

    init {
        getListPrefecture()
    }
}
