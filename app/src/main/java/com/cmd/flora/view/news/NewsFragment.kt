package com.cmd.flora.view.news

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.data.repository.HomeRepository
import com.cmd.flora.databinding.FragmentNewsBinding
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.navigate
import com.cmd.flora.utils.visible
import com.cmd.flora.view.BasePageListFragment
import com.cmd.flora.view.home.HomeAdapter
import com.cmd.flora.view.home.HomeData
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.setUpData
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment :
    BaseVMFragment<FragmentNewsBinding, NewsViewModel>(FragmentNewsBinding::inflate),
    BasePageListFragment.DataSource {
    override val viewModel: NewsViewModel by viewModels()

    override fun initView() {
        binding.viewPager.setUpData(this, 4)
        binding.first.setOnSingleClickListener {
            viewModel.currentIdxLiveData.postValue(0 to true)
        }

        binding.second.setOnSingleClickListener {
            viewModel.currentIdxLiveData.postValue(1 to true)
        }

        binding.third.setOnSingleClickListener {
            binding.thirdFirst.isEnabled = false
            binding.thirdSecond.isEnabled = true
            viewModel.currentIdxLiveData.postValue(2 to true)
        }

        binding.thirdFirst.setOnSingleClickListener {
            binding.thirdFirst.isEnabled = false
            binding.thirdSecond.isEnabled = true
            viewModel.currentIdxLiveData.postValue(2 to true)
        }
        binding.thirdSecond.setOnSingleClickListener {
            binding.thirdFirst.isEnabled = true
            binding.thirdSecond.isEnabled = false
            viewModel.currentIdxLiveData.postValue(3 to true)
        }

        (mActivity as? MainActivity)?.viewModel?.notifyNewSelect?.apply {
            observe(this@NewsFragment) {
                if (it >= 0) {
                    viewModel.currentIdxLiveData.value = (it to false)
                    this.postValue(-1)
                }
            }
        }

        viewModel.currentIdxLiveData.observe(this) { menuType ->
            binding.viewPager.setCurrentItem(menuType.first, menuType.second)
            when (menuType.first) {
                0 -> {
                    binding.first.isEnabled = false
                    binding.second.isEnabled = true
                    binding.third.isEnabled = true
                    binding.thirdFirst.gone()
                    binding.thirdSecond.gone()
                }

                1 -> {
                    binding.first.isEnabled = true
                    binding.second.isEnabled = false
                    binding.third.isEnabled = true
                    binding.thirdFirst.gone()
                    binding.thirdSecond.gone()
                }

                2 -> {
                    binding.first.isEnabled = true
                    binding.second.isEnabled = true
                    binding.third.isEnabled = false
                    binding.thirdFirst.visible()
                    binding.thirdSecond.visible()
                    binding.thirdFirst.isEnabled = false
                    binding.thirdSecond.isEnabled = true
                }

                3 -> {
                    binding.first.isEnabled = true
                    binding.second.isEnabled = true
                    binding.third.isEnabled = false
                    binding.thirdFirst.visible()
                    binding.thirdSecond.visible()
                    binding.thirdFirst.isEnabled = true
                    binding.thirdSecond.isEnabled = false
                }
            }
        }
    }

    override suspend fun onLoadData(
        position: Int, page: Int
    ): Result<Pair<List<AdapterEquatable?>, Boolean>> {
        return viewModel.getValues(position, page)
    }

    override fun onCreateAdapter(): BaseAdapter<AdapterEquatable>? {
        return HomeAdapter(this, bgLoadingColor = android.R.color.transparent, onNewClick = {
            mActivity?.navigate(it)
        }) as? BaseAdapter<AdapterEquatable>
    }

}

@HiltViewModel
class NewsViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {

    val currentIdxLiveData = MutableLiveData(Pair(0, false))

    suspend fun getValues(idx: Int, page: Int): Result<Pair<List<HomeData>, Boolean>> {
        return when (idx) {
            0 -> homeRepository.getNews1(page)
            1 -> homeRepository.getNews2(page)
            else -> homeRepository.getNews3(page, idx == 3)
        }.map {
            val (news, hasNext) = it
            val mapValues = news.mapIndexed { index, newResponse ->
                HomeData.NewHorizontalView(
                    (index == 0 && page == 1), (index == news.size - 1 && !hasNext), newResponse
                )
            }
            return@map mapValues to hasNext
        }

    }
}
