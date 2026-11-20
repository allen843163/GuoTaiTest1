package com.allen.guotaitest.repository

import com.allen.guotaitest.api.ApiService
import com.allen.guotaitest.api.RetrofitNetwork
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StockRepository @Inject constructor(
    private val apiService: RetrofitNetwork
) {
    suspend fun getExchangeReportBWIBBUAll() = apiService.getExchangeReportBWIBBUAll()

    suspend fun getExchangeReportStockDayAvgAll() = apiService.getExchangeReportStockDayAvgAll()

    suspend fun getExchangeReportStockDayAll() = apiService.getExchangeReportStockDayAll()
}
