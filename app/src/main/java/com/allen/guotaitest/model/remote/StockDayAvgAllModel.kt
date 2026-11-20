package com.allen.guotaitest.model.remote

import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class StockDayAvgAllModel {
    @Keep
    @Serializable
    data class Response(
        @Json(name = "Code")
        val code: String,
        @Json(name = "ClosingPrice")
        val closingPrice: String?,
        @Json(name = "Date")
        val date: String?,
        @Json(name = "MonthlyAveragePrice")
        val monthlyAveragePrice: String?,
        @Json(name = "Name")
        val name: String?
    )
}