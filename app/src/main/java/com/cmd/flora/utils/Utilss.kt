package com.cmd.flora.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cmd.flora.BuildConfig
import com.cmd.flora.R
import com.cmd.flora.application.MainApplication
import com.cmd.flora.base.MyActivity
import com.cmd.flora.data.model.WindowInsetModel
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

fun <A, B> Pair<A?, B?>.orNull(): Pair<A, B>? {
    return if (first != null && second != null) first!! to second!! else null
}

fun RecyclerView.setupLoadMore(
    positionInset: Int = 2, addCondition: (() -> Boolean) = { true }, onLoadMore: () -> Unit
) {
    adapter ?: return
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let {
                if ((it.findLastVisibleItemPosition() > (adapter!!.itemCount) - positionInset) && addCondition()) {
                    onLoadMore.invoke()
                }
            }
        }
    })
}

infix fun Context.compatColor(@ColorRes int: Int): Int {
    return ContextCompat.getColor(this, int)
}

infix fun Context.compactDrawable(@DrawableRes int: Int): Drawable? {
    return ContextCompat.getDrawable(this, int)
}

fun Context.launchMarket() {
    val uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
    val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
    launchIntent(myAppLinkToMarket)
}

fun Context.shareThisApp() {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    )
    launchIntent(Intent.createChooser(shareIntent, "Share"))
}

infix fun Context.launchIntent(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: Exception) {
        //error message
    }
}


infix fun Context.launchUrl(url: String) {
    launchIntent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Context.launchSetting() {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    }
    launchIntent(intent)
}

infix fun Context.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun MyActivity.checkPermission(
    perms: Array<String>
) {
    if (hasPermissions(perms)) {
        val a = mutableMapOf<String, Boolean>()
        perms.forEach { s -> a[s] = true }
        onPermissionGranted(a)
    } else {
//        permissionsResult.launch(perms)
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
    this.isEnabled = true
}

fun View.hidden() {
    this.visibility = View.INVISIBLE
    this.isEnabled = false
}

fun View.gone() {
    this.visibility = View.GONE
    this.isEnabled = false
}

fun DrawerLayout.closeDrawer(completion: (() -> Unit)? = null) {
    val listener = object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        }

        override fun onDrawerOpened(drawerView: View) {

        }

        override fun onDrawerClosed(drawerView: View) {
            completion?.invoke()
            removeDrawerListener(this)
        }

        override fun onDrawerStateChanged(newState: Int) {

        }

    }
    addDrawerListener(listener)
    closeDrawers()
}

suspend fun Activity.getInset(): WindowInsetModel = suspendCancellableCoroutine { conti ->

    fun returnData() {
        conti.resume(MainApplication.windowInset)
        conti.cancel()
    }
    if (MainApplication.windowInset.top > 0) {
        returnData()
    } else {
        window.decorView.setOnApplyWindowInsetsListener { v, insets ->
            if (insets.systemWindowInsetTop > 0 && conti.isActive) {
                MainApplication.windowInset =
                    WindowInsetModel(insets.systemWindowInsetTop, insets.systemWindowInsetBottom)
                returnData()
            }
            insets
        }
    }
}

fun ViewPager2.findCurrentFragment(fragmentManager: FragmentManager): Fragment? {
    return fragmentManager.findFragmentByTag("f$currentItem")
}

fun ViewPager2.findFragmentAtPosition(
    fragmentManager: FragmentManager, position: Int
): Fragment? {
    return fragmentManager.findFragmentByTag("f$position")
}

object DeviceInfo {
    val width: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val height: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    val ratio: Float
        get() = width.toFloat() / height.toFloat()
}

fun <I, O> ComponentActivity.registerActivityResultLauncher(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    val key = UUID.randomUUID().toString()
    return activityResultRegistry.register(key, contract, callback)
}

@SuppressLint("StaticFieldLeak")
object MessageError {
    private val context = MainApplication.instance.applicationContext

    val INVALID_ACCOUNT_ID_MESSAGE = context.getString(R.string.invalid_account_message)
    val INVALID_PASSWORD_MESSAGE = context.getString(R.string.invalid_password_message)
    val INVALID_EMAIL_MESSAGE = context.getString(R.string.invalid_email_message)
    val MSG_ERROR_LOGIN_REQUEST = context.getString(R.string.msg_error_login_request)
    val MSG_ERROR_FORGOTPASS_REQUEST = context.getString(R.string.msg_error_forgotpass_request)
    val MSG_PASSWORD_NOT_MATCH = context.getString(R.string.msg_password_not_match)
    val MSG_VALID_RESET_PASSWORD = context.getString(R.string.msg_valid_reset_password)
}