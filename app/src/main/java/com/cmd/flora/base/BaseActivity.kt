package com.cmd.flora.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.cmd.flora.R
import com.cmd.flora.data.model.WindowInsetModel
import com.cmd.flora.databinding.ProgresstViewBinding
import com.cmd.flora.utils.getInset
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.visible
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.dialog.CustomAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


typealias MyActivity = BaseActivity<*>
typealias TabBarView = Group

typealias Inflate<B> = (LayoutInflater) -> B

abstract class BaseActivity<B : ViewBinding>(private val inflate: Inflate<B>) :
    AppCompatActivity() {

    open val binding: B by lazy { inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        launch { if (Build.VERSION.SDK_INT >= 27) setupInset(getInset()) }
        setupView(savedInstanceState)
    }

    open val tabBar: TabBarView? = null

    @IdRes
    open val rootDes: Int? = null

    @IdRes
    open val navHostId: Int? = null

    @IdRes
    open val graphId: Int? = null

    val navContainer: NavController? by lazy {
        (navHostId?.let {
            supportFragmentManager.findFragmentById(
                it
            )
        } as? NavHostFragment)?.navController
    }

    open fun setupView(savedInstanceState: Bundle?) {}

    val permissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                onPermissionGranted(permissions)
            } else {
                onPermissionDenied(permissions)
            }
        }

    fun showProgress(isShow: Boolean) {
        (binding.root as? ViewGroup)?.showProgress(isShow, layoutInflater)
    }

    open fun setupInset(insets: WindowInsetModel) {}

    open fun reloadWhenLogin() {}

    open fun reloadWhenLogout() {}

    open fun onPermissionGranted(permissions: Map<String, Boolean>) {}

    open fun onPermissionDenied(permissions: Map<String, Boolean>) {}

    open fun hideTabBar(isShow: Boolean = false) {
        if (isShow) tabBar?.visible() else tabBar?.gone()
    }
}

abstract class BaseVMActivity<B : ViewBinding, VM : BaseViewModel>(inflate: (LayoutInflater) -> B) :
    BaseActivity<B>(inflate) {
    abstract val viewModel: VM

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        viewModel.progress.observe(this) {
            showProgress(it)
        }
    }
}

@MainThread
fun AppCompatActivity.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start, block)

@MainThread
fun BaseActivity<*>.pushTo(
    @IdRes resId: Int,
    args: Bundle? = null,
    isAddNew: Boolean = false
) {

    val currentDes = navContainer?.currentDestination

    val destinationId = currentDes?.getAction(resId)?.destinationId

    if (currentDes?.id == destinationId && !isAddNew) return

    if (destinationId != null && navContainer?.popBackStack(destinationId, false) == true) {
        return
    }

    currentDes?.getAction(resId)?.navOptions?.let {
        navContainer?.navigate(
            resId,
            args,
            navOptions {
                anim {
                    enter = R.anim.fade_in
                    exit = R.anim.fade_out
                    popEnter = R.anim.fade_in
                    popExit = R.anim.fade_out
                }
                popUpTo(it.popUpToId) {
                    inclusive = it.isPopUpToInclusive()
                }

                launchSingleTop = it.shouldLaunchSingleTop()

                restoreState = it.shouldRestoreState()
            }

        )
    }
}

@MainThread
fun BaseActivity<*>.popTo(@IdRes destinationId: Int? = null, inclusive: Boolean = false) {
    navContainer?.apply {
        if (destinationId == null) popBackStack()
        else popBackStack(destinationId, inclusive)
    }
}

fun BaseActivity<*>.visibleFragment(): Fragment? {
    navHostId?.let {
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(it) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    } ?: return null
}

fun BaseActivity<*>.popToRoot() {
    rootDes?.let { popTo(it, false) }
}

@MainThread
fun BaseActivity<*>.resetNav() {
    graphId ?: return
    navContainer?.setGraph(R.navigation.nav_empty)
    navContainer?.setGraph(graphId!!)
}

@Synchronized
@MainThread
fun AppCompatActivity.showCustomAlertDialog(
    alertData: AlertData
) {
    this.lifecycleScope.launch(Dispatchers.Main) {
        CustomAlertDialog.show(
            supportFragmentManager,
            alertData
        )
    }
}

@MainThread
@Synchronized
fun ViewGroup.showProgress(isShow: Boolean, layoutInflater: LayoutInflater) {
    if (isShow) {
        if (this.findViewById<RelativeLayout>(R.id.progressView) != null) return
        val progressView = ProgresstViewBinding.inflate(layoutInflater)
        addView(
            progressView.root,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    } else {
        this.findViewById<RelativeLayout>(R.id.progressView)?.let {
            this.removeView(it)
        }
    }
}

fun AppCompatActivity.noInset() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}


