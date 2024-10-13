//package com.cmd.flora.view.home
//
//import androidx.fragment.app.Fragment
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.cmd.flora.base.BaseFragment
//import com.cmd.flora.base.MyFragment
//import com.cmd.flora.databinding.FragmentHomeTabBinding
//import com.cmd.flora.utils.findFragmentAtPosition
//import com.cmd.flora.view.empty.EmptyFragment
//import com.cmd.flora.view.inquiry.InquiryFragment
//import com.cmd.flora.view.main.MainActivity
//import com.cmd.flora.view.main.MainViewModel
//import com.cmd.flora.view.news.NewsFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class HomeTabbarFragment :
//    BaseFragment<FragmentHomeTabBinding>(FragmentHomeTabBinding::inflate) {
//
//    override fun initView() {
//        super.initView()
//        initViewPager()
//    }
//
//    private fun initViewPager() {
//        val tabs = listOf(HomeFragment(), NewsFragment(), EmptyFragment(), InquiryFragment())
//        binding.viewPager.isUserInputEnabled = false
//        binding.viewPager.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
//            override fun createFragment(position: Int): Fragment {
//                return tabs.getOrNull(position) ?: EmptyFragment()
//            }
//
//            override fun getItemCount(): Int {
//                return tabs.size
//            }
//        }
//
//        val tabBarView = (mActivity as? MainActivity)?.binding?.tabbar
//
//        (mActivity as? MainActivity)?.viewModel?.currentTabSelected?.observe(this) {
//            if (it == MainViewModel.TabBarItem.QR) return@observe
//            tabBarView?.currentSelectedIndex = it.position
//            if (binding.viewPager.currentItem == it.position) return@observe
//            if (it == MainViewModel.TabBarItem.CONTACT) {
//                (binding.viewPager.findFragmentAtPosition(
//                    childFragmentManager,
//                    it.position
//                ) as? InquiryFragment)?.clearData()
//            }
//            binding.viewPager.setCurrentItem(it.position, false)
//        }
//    }
//
//    override fun reloadWhenLogin() {
//        super.reloadWhenLogin()
//        val maxItem = binding.viewPager.adapter?.itemCount ?: 0
//        (0..<maxItem).forEach {
//            (binding.viewPager.findFragmentAtPosition(
//                childFragmentManager,
//                it
//            ) as? MyFragment)?.reloadWhenLogin()
//        }
//    }
//}