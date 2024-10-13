package com.cmd.flora.application

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.cmd.flora.R
import com.cmd.flora.base.Storage
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.data.model.WindowInsetModel
import com.cmd.flora.utils.MyFirebaseMessagingService
import com.cmd.flora.utils.NotificationUtils
import com.cmd.flora.view.dialog.AlertData
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltAndroidApp
class MainApplication : Application() {

    private val activeActivityCallbacks by lazy { ActiveActivityLifecycleCallbacks() }

    @Inject
    lateinit var storage: Storage

    companion object {
        lateinit var instance: MainApplication
        var windowInset = WindowInsetModel(0, 0)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        storage.migrate()
        registerActivityLifecycleCallbacks(activeActivityCallbacks)
        NetworkConnectionManager.shared.registerForNetworkUpdates(this)

        NotificationUtils.initialize(this)
        NotificationUtils.register(MyFirebaseMessagingService::class.java)
    }

    override fun onTerminate() {
        unregisterActivityLifecycleCallbacks(activeActivityCallbacks)
        super.onTerminate()
    }

    fun getActiveActivity(): AppCompatActivity? =
        activeActivityCallbacks.getActiveActivity() as? AppCompatActivity

    @SuppressLint("HardwareIds")
    fun getDeviceUUID(): String? =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    suspend fun getFirebaseToken(): String? = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine<String?> {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    it.resume(token)
                } else {
                    it.resume(null)
                }
            }
        }
    }
}

fun activeActivity(): AppCompatActivity? = MainApplication.instance.getActiveActivity()
fun deviceUUID(): String? = MainApplication.instance.getDeviceUUID()
suspend fun deviceToken(): String? = MainApplication.instance.getFirebaseToken()


val storage: Storage
    get() = MainApplication.instance.storage

val isInternetAvailable: Boolean
    get() = MainApplication.instance.isInternetAvailable()

fun showNetworkError() = activeActivity()?.let {
    it.showCustomAlertDialog(
        AlertData(
            title = it.getString(R.string.error_deffault_title),
            it.getString(R.string.connectErrorMsg),
            it.getString(R.string.cancelMsg)
        )
    )
}

fun showDefaultError() = activeActivity()?.let {
    it.showCustomAlertDialog(
        AlertData(
            title = it.getString(R.string.error_deffault_title),
            "",
            it.getString(R.string.cancelMsg)
        )
    )
}