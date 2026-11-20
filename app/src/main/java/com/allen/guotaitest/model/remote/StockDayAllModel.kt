package com.allen.guotaitest.model.remote

import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

class StockDayAllModel {
    @Keep
    @Serializable
    data class Response(
        @Json(name = "Code")
        val code: String,
        @Json(name = "Change")
        val change: String?,
        @Json(name = "ClosingPrice")
        val closingPrice: String?,
        @Json(name = "Date")
        val date: String?,
        @Json(name = "HighestPrice")
        val highestPrice: String?,
        @Json(name = "LowestPrice")
        val lowestPrice: String?,
        @Json(name = "Name")
        val name: String?,
        @Json(name = "OpeningPrice")
        val openingPrice: String?,
        @Json(name = "TradeValue")
        val tradeValue: String?,
        @Json(name = "TradeVolume")
        val tradeVolume: String?,
        @Json(name = "Transaction")
        val transaction: String?
    )
}