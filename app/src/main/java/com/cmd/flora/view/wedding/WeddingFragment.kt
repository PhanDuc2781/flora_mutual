package com.cmd.flora.view.wedding

import androidx.fragment.app.viewModels
import com.cmd.flora.base.AdapterEquatable
import com.cmd.flora.base.BaseAdapter
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.databinding.FragmentWeddingBinding
import com.cmd.flora.view.BasePageListFragment
import com.cmd.flora.view.detail.DetailFragment
import com.cmd.flora.view.funeral.FacilityViewModel
import com.cmd.flora.view.setUpData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeddingFragment : BaseVMFragment<FragmentWeddingBinding, FacilityViewModel>(
    FragmentWeddingBinding::inflate
), BasePageListFragment.DataSource {
    override val viewModel: FacilityViewModel by viewModels()
    override fun initView() {
        super.initView()
        binding.viewPager.setUpData(this, 1)
    }

    override suspend fun onLoadData(
        position: Int,
        page: Int
    ): Result<Pair<List<AdapterEquatable?>, Boolean>> {
        return viewModel.getValues(position, page)
    }

    override fun onCreateAdapter(): BaseAdapter<AdapterEquatable>? {
        return FacilityInformationAdapter {
            it.url?.let { url -> DetailFragment.start(mActivity, url) }
        } as? BaseAdapter<AdapterEquatable>
    }
}
