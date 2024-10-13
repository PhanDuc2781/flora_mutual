package com.cmd.flora.view.mypage

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.model.ServiceHistory
import com.cmd.flora.data.repository.MyPageRepository
import com.cmd.flora.databinding.FragmentMyPageBinding
import com.cmd.flora.utils.navigate
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.mypage.adapter.MyPageAdapter
import com.cmd.flora.view.mypage.adapter.MyPageData
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment :
    BaseVMFragment<FragmentMyPageBinding, MyPageViewModel>(FragmentMyPageBinding::inflate) {
    override val viewModel: MyPageViewModel by viewModels()
    private val myPageAdapter by lazy {
        MyPageAdapter(fragment = this,
            onItemMessageClick = { messageNotice ->
                mActivity?.navigate(messageNotice) {
                    viewModel.getDetailInformation(messageNotice.id ?: "0")
                }
            },
            onMemberStatusClick = { -> },
            onShowListNoticeClick = {
                (mActivity as? MainActivity)?.goToNotification(true)
            },
            onSeeMoreClick = {
                if (binding.refresh.isRefreshing) return@MyPageAdapter
                viewModel.updateServiceHistory(it)
            },
            onServiceClick = { serviceHistory -> })
    }


    override fun initView() {
        super.initView()
        binding.recyclerHome.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerHome.adapter = myPageAdapter

        viewModel.listMyPageData.observe(this) {
            myPageAdapter.submitList(it) {
                if (isDestroyed()) return@submitList
                if (binding.refresh.isRefreshing || (!viewModel.isFirstLoad && it != viewModel.defaultValues)) {
                    viewModel.isFirstLoad = true
                    binding.recyclerHome.scrollToPosition(0)
                }
                binding.refresh.isRefreshing = false
            }
        }
        binding.refresh.setOnRefreshListener {
            getData()
        }
        getData()
    }

    private fun getData() {
        viewModel.getMyPageData()
        (mActivity as? MainActivity)?.viewModel?.getUserInformation()
    }
}


@HiltViewModel
class MyPageViewModel @Inject constructor(private val myPageRepository: MyPageRepository) :
    BaseViewModel() {

    companion object {
        const val INITIAL_HISTORY_SIZE = 4
        const val INITIAL_NOTIFY_SIZE = 3
    }

    data class HistoryPageData(
        var page1Value: List<ServiceHistory> = emptyList(),
        var values: List<ServiceHistory> = emptyList(),
        var page: Int = 1,
        var hasNextPage: Boolean = false,
        var visible: List<ServiceHistory> = emptyList(),
    )

    private fun HistoryPageData.showClose(): Boolean =
        values.size > INITIAL_HISTORY_SIZE && !hasNextPage && visible.size == values.size

    private fun HistoryPageData.hasSeeMore(): Boolean =
        values.size > visible.size || hasNextPage || showClose()

    private fun HistoryPageData.mapToSeeMoreShow(): SeeMoreShow =
        SeeMoreShow(hasSeeMore(), showClose())

    var isFirstLoad = false

    data class SeeMoreShow(var show: Boolean = false, var isClose: Boolean = false)

    val visibleHistoryView = MutableLiveData(Pair(false, 1))
    val showSeeMoreOrClose = MutableLiveData(SeeMoreShow())
    val defaultValues = mutableListOf(
        MyPageData.MessageFlora(listOf()), MyPageData.ContractData
    )
    val listMyPageData = MutableLiveData<List<MyPageData>>(defaultValues)

    val historyData = HistoryPageData()

    private fun getCurrentValues(): MutableList<MyPageData> =
        listMyPageData.value?.toMutableList() ?: defaultValues

    fun getMyPageData() {
        viewModelScope.launch {
            fun handleError() {
                if (getCurrentValues() == defaultValues) {
                    val current = defaultValues.toMutableList()
                    current[0] = MyPageData.MessageFlora(emptyList())
                    current.addAll(emptyList())
                    current.add(MyPageData.SeeMoreView)
                    listMyPageData.postValue(current)
                } else {
                    listMyPageData.postValue(listMyPageData.value)
                }
            }
            try {
                val page = 1
                val (listHistoryView, hasNext) = myPageRepository.getServiceHistory(page)
                    .getOrThrow()
                val listMessageNotice =
                    myPageRepository.getMessageNotice(INITIAL_NOTIFY_SIZE).getOrThrow()

                visibleHistoryView.postValue(
                    Pair(
                        listHistoryView.isNotEmpty(),
                        listHistoryView.size
                    )
                )

                //update data
                historyData.page = page
                historyData.values = listHistoryView
                historyData.hasNextPage = hasNext
                historyData.visible = listHistoryView.take(INITIAL_HISTORY_SIZE)
                historyData.page1Value = listHistoryView

                //updateView
                val current = defaultValues.toMutableList()
                current[0] = MyPageData.MessageFlora(listMessageNotice.take(INITIAL_NOTIFY_SIZE))
                current.addAll(historyData.visible.map { MyPageData.ServiceHistoryView(it) })
                current.add(MyPageData.SeeMoreView)
                listMyPageData.postValue(current)
                // update seemore
                showSeeMoreOrClose.postValue(historyData.mapToSeeMoreShow())
            } catch (e: Throwable) {
                handleError()
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun updateServiceHistory(isSeeMore: Boolean) {
        var page = historyData.page
        if (historyData.hasNextPage) page++
        if (!isSeeMore) page = 1

        viewModelScope.launch {
            fun handleError() {
                listMyPageData.postValue(getCurrentValues())
                showSeeMoreOrClose.postValue(showSeeMoreOrClose.value)
            }

            try {

                if (isSeeMore && historyData.page == 1 && historyData.visible.size < historyData.values.size) {
                    historyData.visible = historyData.values
                } else {
                    val (listHistoryRes, hasNext) = myPageRepository.getServiceHistory(page)
                        .getOrThrow()

                    val temp = mutableListOf<ServiceHistory>()
                    temp.addAll(historyData.values)
                    temp.addAll(listHistoryRes)
                    historyData.values = temp
                    historyData.hasNextPage = hasNext
                    historyData.visible = historyData.values
                }

                val current = getCurrentValues()

                if (!isSeeMore || page == 1) {
                    current.removeAll { it is MyPageData.ServiceHistoryView }
                }
                current.removeAll { it is MyPageData.SeeMoreView }

                current.addAll((if (isSeeMore) historyData.visible else run {
                    val tmp = historyData.page1Value.take(INITIAL_HISTORY_SIZE)
                    historyData.visible = tmp
                    historyData.values = historyData.page1Value
                    tmp
                }).map { MyPageData.ServiceHistoryView(it) })

                current.add(MyPageData.SeeMoreView)
                historyData.page = page
                listMyPageData.postValue(current)
                showSeeMoreOrClose.postValue(historyData.mapToSeeMoreShow())
            } catch (e: Throwable) {
                handleError()
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun getDetailInformation(idx: String) {
        val current = listMyPageData.value ?: return
        (current.firstOrNull() as? MyPageData.MessageFlora)?.list?.forEach { it2 ->
            if (it2.id == idx) {
                it2.isRead = true
                return@forEach
            }
        }
        listMyPageData.postValue(current)
    }
}