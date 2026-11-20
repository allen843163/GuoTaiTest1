package com.allen.guotaitest.api

import com.allen.guotaitest.model.remote.BWIBBUModel
import com.allen.guotaitest.model.remote.StockDayAllModel
import com.allen.guotaitest.model.remote.StockDayAvgAllModel
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import okhttp3.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

interface ApiService {
//
//    // ============= Auth =============
//    @POST(ApiRoute.AUTH_GOOGLE_LOGIN)
//    suspend fun authGoogleLogin(@Body request: AuthLoginModel.Request): Response<ApiResponse<AuthLoginModel.Response>>
//
//    @POST(ApiRoute.AUTH_LINE_LOGIN)
//    suspend fun authLineLogin(@Body request: AuthLoginModel.Request): Response<ApiResponse<AuthLoginModel.Response>>
//
//    @Authorized
//    @POST(ApiRoute.AUTH_BIND_LINE)
//    suspend fun authBindLine(
//        @Body request: AuthBindLineModel.Request,
//    ): Response<ApiResponse<Any?>>

    @GET(ApiRoute.EXCHANGEREPORT_BWIBBU_ALL)
    suspend fun getExchangeReportBWIBBUAll(): Response<List<BWIBBUModel.Response>>

    @GET(ApiRoute.EXCHANGEREPORT_STOCK_DAY_AVG_ALL)
    suspend fun getExchangeReportStockDayAvgAll(): Response<List<StockDayAvgAllModel.Response>>

    @GET(ApiRoute.EXCHANGEREPORT_STOCK_DAY_ALL)
    suspend fun getExchangeReportStockDayAll(): Response<List<StockDayAllModel.Response>>
}

@Singleton
class RetrofitNetwork @Inject constructor(
    okhttpCallFactory: Call.Factory,
) {
    private val apiService = Retrofit.Builder()
        .baseUrl("https://openapi.twse.com.tw/v1/ ")
        .callFactory(okhttpCallFactory)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory()).build()
            )
        )
        .build()
        .create(ApiService::class.java)

    suspend fun getExchangeReportBWIBBUAll(): ApiResult<List<BWIBBUModel.Response>> =
        safeApiCall { apiService.getExchangeReportBWIBBUAll() }

    suspend fun getExchangeReportStockDayAvgAll(): ApiResult<List<StockDayAvgAllModel.Response>> =
        safeApiCall { apiService.getExchangeReportStockDayAvgAll() }

    suspend fun getExchangeReportStockDayAll(): ApiResult<List<StockDayAllModel.Response>> =
        safeApiCall { apiService.getExchangeReportStockDayAll() }

}