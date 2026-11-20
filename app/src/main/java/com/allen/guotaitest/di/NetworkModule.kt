package com.allen.guotaitest.di

import android.os.Build
import android.util.Log
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

//    @Provides
//    @Singleton
//    fun providesNetworkJson(): Json = Json {
//        ignoreUnknownKeys = true
//        isLenient = true
//        explicitNulls = false
//        coerceInputValues = true
//    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun okHttpCallFactory(
//        mockApiInterceptor: MockApiInterceptor, 測試用
    ): Call.Factory {
        return try {
            // 建立一個信任所有憑證的 TrustManager
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    // 不進行任何驗證
                }

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    // 不進行任何驗證
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // 初始化 SSLContext 使用 "SSL" 協議
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 連線超時
                .readTimeout(30, TimeUnit.SECONDS)     // 讀取超時
                .writeTimeout(30, TimeUnit.SECONDS)    // 寫入超時
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(HostnameVerifier { _, _ -> true })
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}