package com.allen.guotaitest.api

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException


sealed class ApiResult<out T> {
    data object Initial : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T, val raw: Response?) : ApiResult<T>()
    data class Error(
        val code: Int?,
        val message: String?,
        val raw: Response?,
        val errorBody: ResponseBody?,
        val exception: Exception?,
        val data: Any? = null
    ) : ApiResult<Nothing>()
}


inline fun <T> ApiResult<T>.doOnSuccess(action: (ApiResult.Success<T>) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success<*>) {
        action(this as ApiResult.Success<T>)
    }
    return this
}
inline fun <T> ApiResult<T>.doOnError(action: (ApiResult.Error) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) {
        action(this)
    }
    return this
}

fun <T> ApiResult<T>.get(): T? {
    return if (this is ApiResult.Success) {
        this.data
    } else {
        null
    }
}
suspend inline fun <reified T> safeApiCall(
    crossinline body: suspend () -> retrofit2.Response<T>
): ApiResult<T> {
    return try {
        // blocking block
        val result = withContext(Dispatchers.IO) {
            body()
        }

        if (result.isSuccessful) {
            ApiResult.Success(result.body() as T, result.raw())
        } else {
            ApiResult.Error(
                code = result.code(),
                message = result.message(),
                raw = result.raw(),
                errorBody = result.errorBody(),
                exception = null
            )
        }
    } catch (e: Exception) {
        when (e) {
            is IOException -> {
                ApiResult.Error(100001, "網路錯誤", null, null, e)
            }
            else -> {
                ApiResult.Error(100002, "未知錯誤", null, null, e)
            }
        }
    }
}