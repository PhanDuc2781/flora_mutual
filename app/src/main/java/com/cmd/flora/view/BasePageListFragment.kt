package com.cmd.flora.view

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cmd.flora.application.isInternetAvailable
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.databinding.FragmentBasePageListBinding
import com.cmd.flora.utils.setupLoadMore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BasePageListFragment :
    BaseVMFragment<FragmentBasePageListBinding, BasePageListViewModel>(FragmentBasePageListBinding::inflate) {

    companion object {
        const val POSITION = "POSITION"
        fun newInstance(index: Int): BasePageListFragment {
            return BasePageListFragment().apply {
                arguments = bundleOf(POSITION to index)
            }
        }
    }

    interface DataSource {
        suspend fun onLoadData(
            position: Int,
            page: Int
        ): Result<Pair<List<AdapterEquatable?>, Boolean>>

        fun onCreateAdapter(): BaseAdapter<AdapterEquatable>?
    }

    private val dataSource: DataSource? by lazy {
        parentFragment as? DataSource ?: activity as? DataSource
    }

    override val viewModel: BasePageListViewModel by viewModels()

    open val adapter: BaseAdapter<AdapterEquatable>? by lazy {
        dataSource?.onCreateAdapter()
    }

    open fun onRefresh() {
        getData(INITIAL)
    }

    open fun onConfigRecyclerView() {
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(mActivity)
            adapter = this@BasePageListFragment.adapter
        }
    }

    override fun initView() {
        super.initView()
        onConfigRecyclerView()
        viewModel.data.observe(this) {
            viewModel.isLoading.value = false
            adapter?.submitList(it) {
                if (isDestroyed()) return@submitList
                if (viewModel.pageData.page == 1 && binding.refresh.isRefreshing) {
                    binding.recycler.smoothScrollToPosition(0)
                }

                binding.refresh.isRefreshing = false
            }
        }

        binding.refresh.setOnRefreshListener {
            onRefresh()
        }

        binding.recycler.setupLoadMore(addCondition = {
            (adapter?.itemCount
                ?: 0) > 4 && viewModel.pageData.hasNextPage && viewModel.isLoading.value == false && isInternetAvailable
        }, onLoadMore = {
            adapter?.showLoading()
            getData(LOADMORE)
        })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.data.value.isNullOrEmpty() && viewModel.isLoading.value != true) {
            getData(INITIAL)
            adapter?.showLoading()
        }
    }

    private fun getData(state: LoadState) {
        val page: Int = when (state) {
            INITIAL -> 1
            LOADMORE -> viewModel.pageData.page + 1
            is PAGE -> state.page
        }
        viewModel.viewModelScope.launch {
            viewModel.isLoading.value = true
            val request = dataSource?.onLoadData(arguments?.getInt(POSITION, 0) ?: 0, page)
            request?.onSuccess {
                val (items, hasNext) = if (page == 1) {
                    it
                } else {
                    val (values, hasNext) = it
                    val current = viewModel.pageData.values.toMutableList()
                    current.addAll(values)
                    current.distinct() to hasNext
                }

                viewModel.pageData.values = items
                viewModel.pageData.hasNextPage = hasNext
                viewModel.pageData.page = page

                viewModel.data.postValue(viewModel.pageData.values)
                viewModel.isLoading.value = false
            }

            request?.onFailure {
                viewModel.data.postValue(viewModel.pageData.values)
                viewModel.isLoading.value = false
            }
        }
    }
}

data class PageData(
    var values: List<AdapterEquatable?> = emptyList(),
    var page: Int = 0,
    var hasNextPage: Boolean = false
)

@HiltViewModel
open class BasePageListViewModel @Inject constructor() : BaseViewModel() {
    var pageData = PageData()
    val data = MutableLiveData<List<AdapterEquatable?>>()
    val isLoading = MutableLiveData(false)
}

fun ViewPager2.setUpData(fragment: Fragment, size: Int) {
    if (size == 0) return
//    offscreenPageLimit = size
    isUserInputEnabled = false
//    isSaveEnabled = false
    val list = (0..<size).map { BasePageListFragment.newInstance(it) }
    adapter = object : FragmentStateAdapter(fragment.childFragmentManager, fragment.lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return list.getOrNull(position) ?: list.first()
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
            super.setStateRestorationPolicy(StateRestorationPolicy.PREVENT)
        }
    }
}

sealed interface LoadState
data object INITIAL : LoadState
data class PAGE(val page: Int) : LoadState
data object LOADMORE : LoadState