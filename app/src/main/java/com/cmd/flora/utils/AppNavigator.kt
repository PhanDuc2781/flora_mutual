package com.cmd.flora.utils

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.cmd.flora.application.isInternetAvailable
import com.cmd.flora.application.showNetworkError
import com.cmd.flora.base.MyActivity
import com.cmd.flora.data.network.response.HomeNotify
import com.cmd.flora.data.network.response.NewResponse
import com.cmd.flora.data.network.response.NotificationResponse
import com.cmd.flora.view.detail.DetailFragment
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.qrscreen.QRActivity
import kotlinx.coroutines.launch

enum class AppNavigatorType(val value: String) {
    INFOMATION("information"), POINT("point");

    companion object {
        fun get(value: String?) = entries.firstOrNull { it.value == value } ?: INFOMATION
    }
}

data class NotificationNavigator(
    val type: AppNavigatorType,
    val target_id: String?,
    val information_id: String?,
    val url: String?
)

fun Bundle.mapToNotificationNavigator(): NotificationNavigator = NotificationNavigator(
    type = AppNavigatorType.get(this.getString("type")),
    target_id = this.getString("target_id"),
    information_id = this.getString("information_id"),
    null
)

fun MyActivity.navigate(data: Bundle?, completion: (() -> Unit)? = null) {
    navigate(
        data?.mapToNotificationNavigator(),
        completion
    )
}

fun MyActivity.navigate(data: HomeNotify?, completion: (() -> Unit)? = null) {
    navigate(
        NotificationNavigator(
            AppNavigatorType.get(data?.type),
            data?.target_id,
            data?.id.toString(),
            data?.url
        ),
        completion
    )
}

fun MyActivity.navigate(data: NewResponse?, completion: (() -> Unit)? = null) {
    navigate(
        NotificationNavigator(
            AppNavigatorType.get(data?.type),
            data?.target_id,
            data?.id.toString(),
            data?.url
        ),
        completion
    )
}

fun MyActivity.navigate(data: NotificationResponse?, completion: (() -> Unit)? = null) {
    navigate(
        NotificationNavigator(
            AppNavigatorType.get(data?.type),
            data?.target_id,
            data?.id.toString(),
            data?.url
        ),
        completion
    )
}

fun MyActivity.navigate(data: NotificationNavigator?, completion: (() -> Unit)? = null) {
    data ?: return
    if (!isInternetAvailable) {
        showNetworkError()
        return
    }
    
    fun getDetail(
        showProgress: Boolean = false,
        completion: ((String) -> Unit)? = null,
        error: (() -> Unit)? = null
    ) {
        (this as? MainActivity)?.viewModel.apply {
            this?.viewModelScope?.launch {
                val response = data.information_id?.toIntOrNull()?.let {
                    showProgress(showProgress)
                    this@apply.homeRepository.getDetailInformation(it, showProgress)
                }
                showProgress(false)
                response?.url?.let {
                    completion?.invoke(it)
                } ?: error?.invoke()
            }
        }
    }
    when (data.type) {
        AppNavigatorType.INFOMATION -> {

            if (data.url != null) {
                completion?.invoke()
                DetailFragment.start(this, data.url)
                getDetail {}
            } else {
                getDetail(false, completion = {
                    completion?.invoke()
                    DetailFragment.start(this, it)
                }) {
                    completion?.invoke()
                    DetailFragment.start(this, null)
                }
            }
        }

        AppNavigatorType.POINT -> {
            data.target_id?.let {
                completion?.invoke()
                getDetail(false) { }
                (this as? MainActivity)?.activityResultLauncher?.launch(
                    Intent(this, QRActivity::class.java).apply {
                        putExtra(QRActivity.VIEW_MODE, QRActivity.VIEW_MODE_SCORE)
                        putExtra(QRActivity.TARGET_ID, it)
                    })
            }
        }
    }
}