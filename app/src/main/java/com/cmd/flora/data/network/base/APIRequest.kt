package com.cmd.flora.data.network.base

import com.cmd.flora.BuildConfig
import com.cmd.flora.data.repository.NoInternet
import com.cmd.flora.data.repository.OtherError
import com.cmd.flora.data.repository.RepositoryResult
import com.cmd.flora.data.repository.ServerError
import com.cmd.flora.data.repository.TimeOutError
import com.cmd.flora.data.repository.handleDefaultError
import com.cmd.flora.utils.injection.NoConnectivityException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Singleton

@Singleton
class APIRequest(
    private val authService: AuthApiService,
    private val noAuthService: ApiService,
    val gson: Gson
) {
    companion object {
        const val BASE_URL = BuildConfig.API_ENDPOINT
        val isMock = BuildConfig.FLAVOR === "MOCK"
    }

    inline fun <reified T> request(
        router: ApiRouter
    ): Flow<T> = flow { emit(gson.fromJson(getMethodCall(router).string())) }

    suspend inline fun <reified T> requestRepositoryResult(
        router: ApiRouter,
    ): RepositoryResult<T> {
        return router.mockResponse(gson) ?: checkTryError {
            val value = getMethodCall(router).string()
            if (T::class.isInstance(true)) {
                RepositoryResult.Success(value.isNotEmpty() as T)
            } else if (T::class.isInstance("")) {
                RepositoryResult.Success(value as T)
            } else {
                val result: T? = gson.fromJson(value)
                RepositoryResult.Success(result)
            }
        }
    }

    inline fun <reified T> checkTryError(success: () -> RepositoryResult<T>): RepositoryResult<T> {
        return try {
            success.invoke()
        } catch (e: NoConnectivityException) {
            RepositoryResult.Error(NoInternet)
        } catch (e: SocketTimeoutException) {
            RepositoryResult.Error(TimeOutError)
        } catch (e: HttpException) {
            when (HTTPError.get(e.code())) {
                HTTPError.SERVER_ERROR -> RepositoryResult.Error(ServerError)
                else -> RepositoryResult.Error(OtherError(e, e.response()?.errorBody()))
            }
        } catch (e: Exception) {
            RepositoryResult.Error(OtherError(e, null))
        }
    }

    suspend inline fun <reified T> requestResult(
        router: ApiRouter,
        shouldCheckError: Boolean = true,
    ) = requestRepositoryResult<T>(router)
        .handleDefaultError(shouldCheckError)

    suspend fun getMethodCall(router: ApiRouter): ResponseBody =
        getService(router.needLogin).request(router)

    private fun getService(needLogin: Boolean = false) =
        if (needLogin) authService else noAuthService
}

suspend inline fun <reified T, reified U> APIRequest.requestRepositoryResult2(
    router1: ApiRouter,
    router2: ApiRouter,
): RepositoryResult<Pair<T, U>> = Pair(router1, router2).mockResponse(gson) ?: checkTryError {
    supervisorScope {
        val response1 = async { getMethodCall(router1).string() }
        val response2 = async { getMethodCall(router2).string() }
        val result1: T = gson.fromJson(response1.await())
        val result2: U = gson.fromJson(response2.await())
        RepositoryResult.Success(Pair(result1, result2))
    }
}

suspend inline fun <reified T, reified U, reified Y> APIRequest.requestRepositoryResult3(
    router1: ApiRouter,
    router2: ApiRouter,
    router3: ApiRouter
): RepositoryResult<Triple<T, U, Y>> =
    Triple(router1, router2, router3).mockResponse(gson) ?: checkTryError {
        runBlocking {
            val response1 = async { getMethodCall(router1).string() }
            val response2 = async { getMethodCall(router2).string() }
            val response3 = async { getMethodCall(router3).string() }
            val result1: T = gson.fromJson(response1.await())
            val result2: U = gson.fromJson(response2.await())
            val result3: Y = gson.fromJson(response3.await())
            RepositoryResult.Success(Triple(result1, result2, result3))
        }
    }

suspend inline fun <reified T, reified U, reified Y> APIRequest.requestResult3(
    router1: ApiRouter,
    router2: ApiRouter,
    router3: ApiRouter,
    shouldCheckError: Boolean = true,
) = requestRepositoryResult3<T, U, Y>(router1, router2, router3)
    .handleDefaultError(shouldCheckError)

suspend inline fun <reified T, reified U> APIRequest.requestResult2(
    router1: ApiRouter,
    router2: ApiRouter,
    shouldCheckError: Boolean = true,
) = requestRepositoryResult2<T, U>(router1, router2)
    .handleDefaultError(shouldCheckError)

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

fun Throwable.httpCode(): Int? = (this as? HttpException)?.code()