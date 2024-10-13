package com.cmd.flora.view.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.cmd.flora.application.isInternetAvailable
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.network.response.HomeNotify
import com.cmd.flora.data.network.response.NewResponse
import com.cmd.flora.data.network.response.NewResponse.Companion.emptyValue
import com.cmd.flora.data.repository.HomeRepository
import com.cmd.flora.databinding.FragmentHomeBinding
import com.cmd.flora.utils.navigate
import com.cmd.flora.utils.setupLoadMore
import com.cmd.flora.view.home.HomeFragment.Companion.READ_HOME_NOTY
import com.cmd.flora.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment :
    BaseVMFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()

    companion object {
        val READ_HOME_NOTY = mutableSetOf<String>()
    }

    private val homeAdapter by lazy {
        HomeAdapter(
            this,
            onNewClick = {
                mActivity?.navigate(it)
            },
            onDismissToast = viewModel::dismissToast,
            onToastClick = {
                mActivity?.navigate(it)
//                (mActivity as? MainActivity)?.checkLoginAndOpen {
//                    mActivity?.navigate(it)
//                }
            }
        )
    }

    private val layoutManager: GridLayoutManager
        get() = GridLayoutManager(mActivity, 2).apply {
            spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (homeAdapter.getItemViewType(position)) {
                        HomeData.NEW_GRID_VIEW_TYPE -> 1
                        else -> 2
                    }
                }
            }
        }

    override fun initView() {
        viewModel.homeItems.observe(this) {
            homeAdapter.submitList(it) {
                if (isDestroyed()) return@submitList
                viewModel.canLoadOrRefresh.value = it.size > viewModel.defaultValues.size
                if (it == viewModel.defaultValues) return@submitList
                if (binding.refresh.isRefreshing || viewModel.firstLaunch) {
                    viewModel.firstLaunch = false
                    binding.refresh.isRefreshing = false
                    lifecycleScope.launch {
                        binding.recyclerHome.smoothScrollToPosition(0)
                    }
                }
            }
        }

//        viewModel.canLoadOrRefresh.observe(this) {
//            binding.refresh.isEnabled = it
//        }

        binding.refresh.setOnRefreshListener {
            fullRefresh()
        }
        binding.recyclerHome.layoutManager = layoutManager
        binding.recyclerHome.adapter = homeAdapter
        binding.recyclerHome.setupLoadMore(addCondition = {
            viewModel.hasNextPage && viewModel.canLoadOrRefresh.value == true && isInternetAvailable
        }) {
            homeAdapter.showLoading()
            viewModel.fetchNextPage()
        }

        viewModel.hasNotify.observe(this) {
            (mActivity as? MainActivity)?.binding?.header?.setHasNotify(it)
        }
    }

    override fun reloadWhenLogin() {
        super.reloadWhenLogin()
        viewModel.firstLaunch = true
        lifecycleScope.launch(Dispatchers.Main) {
            fullRefresh()
            binding.refresh.isRefreshing = true
        }
    }

    private fun fullRefresh() {
        viewModel.fullRefresh()
        (mActivity as? MainActivity)?.viewModel?.getUserInformation()
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {

    var firstLaunch = true

    val hasNotify = MutableLiveData(false)

    val defaultValues = listOf(
        HomeData.ToastView(null),
        HomeData.MenuView,
        HomeData.QRView,
        HomeData.CallView,
        HomeData.TitleView(null)
    )

    val homeItems = MutableLiveData(defaultValues)
    val canLoadOrRefresh = MutableLiveData(false)

    var page = 1
    var hasNextPage = false

    fun fetchNextPage() {
        canLoadOrRefresh.value = false
        page++
        fetchData()
    }

    fun fullRefresh() {
        page = 1
        fetchData()
    }

    private fun getCurrentValues(): MutableList<HomeData> =
        homeItems.value?.toMutableList() ?: defaultValues.toMutableList()

    private fun fetchData() {
        viewModelScope.launch {
            fun handleError() {
                if (getCurrentValues() == defaultValues) {
                    val current = defaultValues.toMutableList()
                    current[0] = HomeData.ToastView(null)
                    current[4] = HomeData.TitleView(false)
                    current.addAll(emptyList())
                    current.addNewGridViewEmpty(emptyList())
                    current to false
                    homeItems.postValue(current)
                } else {
                    homeItems.postValue(homeItems.value)
                }
            }
            try {
                val (items, hasNext) = if (page == 1) {
                    val toast = homeRepository.getHomeNotify()
                    hasNotify.postValue((toast?.unreadCount ?: 0) > 0)
                    val (news, hasNext) = homeRepository.getNewsHome(page).getOrThrow()
                    val current = defaultValues.toMutableList()
                    current[0] = HomeData.ToastView(
                        if (READ_HOME_NOTY.contains(
                                toast?.id ?: "-1"
                            )
                        ) null else toast
                    )
                    current[4] = HomeData.TitleView(news.isNotEmpty())
                    current.addAll(news.map { newResponse ->
                        HomeData.NewGridView(
                            newResponse
                        )
                    })
                    current.addNewGridViewEmpty(news)
                    current to hasNext
                } else {
                    val (news, hasNext) = homeRepository.getNewsHome(page).getOrThrow()
                    val current = getCurrentValues()
                    current.addAll(news.map { newResponse ->
                        HomeData.NewGridView(
                            newResponse
                        )
                    })
                    current.removeAll { it is HomeData.NewGridView && it.newResponse.id == null }
                    current.addNewGridViewEmpty(news)
                    current to hasNext
                }
                homeItems.postValue(items)
                hasNextPage = hasNext
            } catch (e: Throwable) {
                handleError()
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    private fun MutableList<HomeData>.addNewGridViewEmpty(news: List<NewResponse>) {
        if (news.size % 2 != 0) {
            this.add(HomeData.NewGridView(emptyValue))
        }
    }

    fun dismissToast(data: HomeNotify) {
        val current = getCurrentValues()
        current[0] = HomeData.ToastView(null)
        homeItems.postValue(current)
        data.id?.orNull()?.let { READ_HOME_NOTY.add(it) }
    }

    init {
        fetchData()
    }
}

fun String.orNull(): String? = if (isNullOrEmpty() || isNullOrBlank()) null else this






