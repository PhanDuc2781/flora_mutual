package com.cmd.flora.data.network.base

import com.cmd.flora.application.activeActivity
import com.cmd.flora.application.deviceToken
import com.cmd.flora.application.storage
import com.cmd.flora.base.MyActivity
import com.cmd.flora.data.network.APIPath
import com.cmd.flora.data.network.response.LoginResponseModel
import com.cmd.flora.data.network.response.getAccessToken
import com.cmd.flora.view.main.logOut
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Singleton

@Singleton
class AuthInterceptor(private val service: ApiService, val gson: Gson) : Interceptor {

    private val mutex = Mutex()
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val token = runBlocking { storage.accessToken }
        val res = chain.proceedWithToken(req, token)
        if (res.code != HTTPError.UNAUTHORISE.code) return res

        val newToken: String? = runBlocking { getToken() }

        return if (newToken != null) {
            res.close()
            chain.proceedWithToken(req, newToken)
        } else {
            (activeActivity() as? MyActivity)?.logOut()
            CoroutineScope(Dispatchers.IO).launch {
                unregisterToken()
            }
            res
        }
    }

    private suspend fun getToken(): String? = mutex.withLock {
        try {
            refreshToken()
        } catch (e: Throwable) {
            return@withLock null
        } catch (e: Exception) {
            return@withLock null
        }
    }

    private suspend fun unregisterToken() = withContext(Dispatchers.IO) {
        try {
            val router = ApiRouter(
                APIPath.unregisterToken(),
                HTTPMethod.POST,
                parameters = hashMapOf("device_token" to (deviceToken() ?: "")),
                needLogin = false
            )
            service.request(router)
        } catch (e: Throwable) {
            print(e.localizedMessage)
        } catch (e: Exception) {
            print(e.localizedMessage)
        }
    }

    private suspend fun refreshToken(): String? {
        val router = ApiRouter(
            APIPath.refreshToken(),
            HTTPMethod.POST,
            headers = mutableMapOf(AuthorizationHeader).apply {
                this.putAll(JsonFormatter)
            }.toHashMap()
        )
        val refreshTokenRes = service.request(router)
        val result: LoginResponseModel = gson.fromJson(refreshTokenRes.string())
        storage.accessToken = result.getAccessToken() ?: ""
        return result.getAccessToken()
    }

    private fun Interceptor.Chain.proceedWithToken(req: Request, token: String): Response =
        req.newBuilder().apply {
            addHeader(AuthorizationHeader.first, token)
        }.build().let(::proceed)
}