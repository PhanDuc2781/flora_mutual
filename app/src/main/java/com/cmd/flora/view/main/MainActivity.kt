package com.cmd.flora.view.main

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.NavigationRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.cmd.flora.BuildConfig
import com.cmd.flora.R
import com.cmd.flora.application.storage
import com.cmd.flora.base.BaseVMActivity
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.MyActivity
import com.cmd.flora.base.MyFragment
import com.cmd.flora.base.isLogin
import com.cmd.flora.base.launch
import com.cmd.flora.base.noInset
import com.cmd.flora.base.popTo
import com.cmd.flora.base.popToRoot
import com.cmd.flora.base.pushTo
import com.cmd.flora.base.resetNav
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.visibleFragment
import com.cmd.flora.data.model.WindowInsetModel
import com.cmd.flora.data.network.response.Member
import com.cmd.flora.data.network.response.UserData
import com.cmd.flora.data.repository.HomeRepository
import com.cmd.flora.databinding.ActivityMainBinding
import com.cmd.flora.utils.MyFirebaseMessagingService
import com.cmd.flora.utils.TopDecoration
import com.cmd.flora.utils.closeDrawer
import com.cmd.flora.utils.cv
import com.cmd.flora.utils.dpToPx
import com.cmd.flora.utils.navigate
import com.cmd.flora.utils.orNull
import com.cmd.flora.utils.widget.CustomInputView
import com.cmd.flora.view.addmail.AddEmailActivity
import com.cmd.flora.view.authen.activity.LoginActivity
import com.cmd.flora.view.authen.activity.getCode
import com.cmd.flora.view.authen.activity.getEmail
import com.cmd.flora.view.home.HomeFragment
import com.cmd.flora.view.home.orNull
import com.cmd.flora.view.menu.MenuItem
import com.cmd.flora.view.menu.MenuItemAdapter
import com.cmd.flora.view.qrscreen.QRActivity
import com.cmd.flora.view.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    BaseVMActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {
    override val viewModel: MainViewModel by viewModels()

    companion object {
        const val StartActivityForResult = "StartActivityForResult"
        const val StartActivityForResult_Logout = "StartActivityForResult_Logout"
    }

    var loginCallback: ((Boolean) -> Unit)? = null

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            if (result.data?.getBooleanExtra(StartActivityForResult_Logout, false) == true) {
                logOut()
                return@registerForActivityResult
            }
            when (result.data?.getStringExtra(StartActivityForResult)) {
                LoginActivity::class.java.name -> {
                    loginCallback?.invoke(true)
                }

                QRActivity::class.java.name -> {
                    tabSelect(MainViewModel.TabBarItem.HOME)
                }

                AddEmailActivity::class.java.name -> {
                    viewModel.getUserInformation()
                }
            }

        }

    private val menuItemAdapter by lazy {
        MenuItemAdapter(this) {
            closeDrawer {
                handleMenuItem(it)
            }
        }
    }

    private val detailScreen = setOf(R.id.emptyFragment, R.id.detailFragment, R.id.myPageFragment)
    private val hideTabBarScreen = setOf(R.id.callFragment, R.id.settingFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        setupNavListener()

        viewModel.userInformation.observe(this) {
            if (it.member_code != null && it.email?.orNull() == null && viewModel.shouldShowAddMail) {
                viewModel.shouldShowAddMail = false
                activityResultLauncher.launch(Intent(this, AddEmailActivity::class.java))
            }
        }

        viewModel.getUserInformation()

        onBackPressedDispatcher.addCallback {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                closeDrawer()
            } else {
                finishAffinity()
            }
        }

        setUpObserve()

        binding.header.setOnLeftClickListener(onHambergerClick = {
            openDrawer()
        }, onBackClick = {
            popTo()
        })

        binding.icClose.setOnSingleClickListener {
            closeDrawer()
        }
        binding.recyclerMenu.adapter = menuItemAdapter
        binding.recyclerMenu.addItemDecoration(TopDecoration(40.cv.dpToPx()))

        binding.tabbar.setOnTabChangeListener {
            val value = MainViewModel.TabBarItem.get(it)
            viewModel.visibleTabBar.value = value != MainViewModel.TabBarItem.CONTACT
            tabSelect(value)
        }

        binding.header.setOnUserClickListener {
            handleMenuItem(MenuItem.PERSON)
        }

        binding.header.setOnNotifyClickListener {
            goToNotification()
        }
    }

    fun goToNotification(isPrivate: Boolean = false) {
        viewModel.notifyNewSelect.postValue(if (isPrivate) 3 else 2)
        tabSelect(MainViewModel.TabBarItem.NEW)
    }


    private fun setupNavListener() {
        navContainer?.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.header.isShowBack = detailScreen.contains(destination.id)
            if (binding.header.isShowBack) {
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

//            if (destination.id == R.id.homeTabbarFragment) {
//                viewModel.visibleTabBar.value =
//                    viewModel.currentTabSelected.value != MainViewModel.TabBarItem.CONTACT
//            } else viewModel.visibleTabBar.value = !hideTabBarScreen.contains(destination.id)

            viewModel.visibleTabBar.value = !hideTabBarScreen.contains(destination.id)
        }
    }

    override fun setupInset(insets: WindowInsetModel) {
        super.setupInset(insets)
        noInset()
        binding.container.setPadding(0, insets.top, 0, 0)
        binding.tabbar.setPadding(0, 0, 0, insets.bottom)
        binding.navView.setPadding(0, insets.top, 0, insets.bottom)
    }

    private fun setUpObserve() {
        viewModel.visibleTabBar.observe(this) {
            hideTabBar(it)
        }

        viewModel.currentTabSelected.observe(this) {
            viewModel.visibleTabBar.value = it != MainViewModel.TabBarItem.CONTACT
        }
    }

    fun goToPhoneList() {
        checkLoginAndOpen {
            pushTo(R.id.actionToCallFragment)
        }
    }

    fun handleMenuItem(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.HOME -> {
                tabSelect(MainViewModel.TabBarItem.HOME)
            }

            MenuItem.FEATURE_QR -> {
                checkLoginAndOpen {
                    activityResultLauncher.launch(Intent(this, QRActivity::class.java).apply {
                        putExtra(QRActivity.VIEW_MODE, QRActivity.VIEW_MODE_QR)
                    })
                }
            }

            MenuItem.PERSON -> {
                checkLoginAndOpen { pushTo(R.id.actionToMyPageFragment) }
            }

            MenuItem.NEWS -> {
                checkLoginAndOpen {
                    viewModel.notifyNewSelect.postValue(0)
                    tabSelect(MainViewModel.TabBarItem.NEW)
                }
            }

            MenuItem.WEDDING -> {
                pushTo(R.id.actionToWeddingFragment)
            }

            MenuItem.FUNERAL -> {
                pushTo(R.id.actionToFuneralFragment)
            }

            MenuItem.FLOWER -> {
                WebViewActivity.start(this, BuildConfig.Flora_URL)
            }

            MenuItem.CART -> {
                WebViewActivity.start(this, BuildConfig.CART_URL)
            }

            MenuItem.NEWSLETTER -> {
                pushTo(R.id.actionToNewsLetterFragment)
            }

            MenuItem.SETTING -> {
                checkLoginAndOpen { pushTo(R.id.actionToSettingFragment) }
            }

            MenuItem.CONTACT -> {
//                checkLoginAndOpen {
//                    pushTo(R.id.actionToCallFragment)
                tabSelect(MainViewModel.TabBarItem.CONTACT)
//                }
            }

            MenuItem.MEMBER -> {
                handleMenuItem(MenuItem.PERSON)
            }
        }
    }

    private fun tabSelect(position: MainViewModel.TabBarItem) {

        fun selectPage() {
//            if (visibleFragment() !is HomeTabbarFragment) {
//                popToRoot()
//            }
            viewModel.currentTabSelected.postValue(position)
            binding.tabbar.currentSelectedIndex = position.position
            when (position) {
                MainViewModel.TabBarItem.NEW -> {
                    position.navigation?.let { pushTo(it) }
                }

                MainViewModel.TabBarItem.QR -> {

                }

                MainViewModel.TabBarItem.HOME -> popToRoot()
                MainViewModel.TabBarItem.CONTACT -> {
                    position.navigation?.let { pushTo(it) }
                }
            }

        }

        when (position) {
            MainViewModel.TabBarItem.NEW -> {
                checkLoginAndOpen { selectPage() }
            }

            MainViewModel.TabBarItem.QR -> {
                handleMenuItem(MenuItem.FEATURE_QR)
            }

            else -> {
                selectPage()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent ?: return
        if (intent.getBooleanExtra(MyFirebaseMessagingService.NOTIFICATION, false)) {
            if (!storage.isLogin) return
            navigate(intent.extras)
        } else {
            if (storage.isLogin) return
            if (intent.action != Intent.ACTION_VIEW) return
            (intent.getCode() to intent.getEmail()).orNull()
                ?.let { checkLoginAndOpen(intent.data) { reloadWhenLogin() } }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val nextViewEdittext =
                    (v.parent.parent.parent as? ConstraintLayout)?.children?.firstOrNull {
                        val outRect = Rect()
                        it.getGlobalVisibleRect(outRect)
                        return@firstOrNull outRect.contains(
                            event.rawX.toInt(), event.rawY.toInt()
                        ) && it is CustomInputView
                    } != null

                if (!nextViewEdittext) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    override val tabBar: Group? by lazy { binding.groupTabbar }

    override val rootDes: Int? by lazy { R.id.homeFragment }

    override val navHostId: Int? by lazy { binding.navHostFragment.id }

    override val graphId: Int? by lazy { R.navigation.nav_main }
    override fun reloadWhenLogin() {
        (visibleFragment() as? MyFragment)?.reloadWhenLogin()
    }

    override fun reloadWhenLogout() {
        lifecycleScope.launch {
            handleMenuItem(MenuItem.HOME)
            resetNav()
            viewModel.getUserInformation()
        }
    }

}

fun MyActivity.logOut() {
    storage.logout()
    HomeFragment.READ_HOME_NOTY.clear()
    reloadWhenLogout()
}

fun MainActivity.closeDrawer(completion: (() -> Unit)? = null) {
    binding.drawer.closeDrawer(completion)
}

fun MainActivity.openDrawer() {
    binding.root.open()
}

fun MainActivity.checkLoginAndOpen(data: Uri? = null, callback: () -> Unit) {
    if (!storage.isLogin) {
        this.loginCallback = {
            if (it) {
                viewModel.getUserInformation()
//                callback.invoke()
                this.popToRoot()
                reloadWhenLogin()
            }
            this.loginCallback = null
        }
        activityResultLauncher.launch(Intent(this, LoginActivity::class.java).apply {
            setData(data)
        })
    } else {
        callback.invoke()
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    val homeRepository: HomeRepository
) : BaseViewModel() {

    enum class TabBarItem(val position: Int, @NavigationRes val navigation: Int? = null) {
        HOME(0, R.id.homeFragment),
        NEW(1, R.id.actionToNewsFragment),
        QR(2),
        CONTACT(3, R.id.actionToInquiryFragment);

        companion object {
            fun get(position: Int): TabBarItem =
                entries.firstOrNull { it.position == position } ?: HOME
        }
    }

    var shouldShowAddMail = true

    val currentTabSelected = MutableLiveData(TabBarItem.HOME)
    val notifyNewSelect = MutableLiveData<Int>()

    val visibleTabBar = MutableLiveData(true)
    val userInformation = MutableLiveData<UserData>()

    fun getUserInformation() {
        if (!storage.isLogin) {
            userInformation.postValue(UserData(member = Member(point = "0")))
            return
        }
        storage.userInfo?.let { userInformation.postValue(it) }
        launch {
            val userData = homeRepository.getUserInfor()?.data
            userData?.let {
                storage.userInfo = it
                userInformation.postValue(storage.userInfo)
            }
        }
    }
}

val MyActivity.userInformation: LiveData<UserData>?
    get() = (this as? MainActivity)?.viewModel?.userInformation

val MyFragment.userInformation: LiveData<UserData>?
    get() = mActivity?.userInformation
